package edu.yu.parallel.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import edu.yu.parallel.DataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerStats;

public class ParallelDataProcessor implements DataProcessor {

    private volatile boolean headerValid = false;

    @Override
    public Map<Integer, TickerStats> processFile(String filePath) throws IOException, ProcessingException {
        File file = new File(filePath);
        long fileSize = file.length();
        final int numThreads = Runtime.getRuntime().availableProcessors();
        int optimalChunkSize = (int) Math.max(1000, Math.min(fileSize / (numThreads * 10), 50000));

        Map<Integer, TickerStats> results = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();


        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new ProcessingException("File is empty");
            }
            String[] header = firstLine.split(",");
            if(header.length != 4 || !header[0].trim().equals("Date") || !header[1].trim().equals("Ticker") || !header[2].trim().equals("Adj Close") || !header[3].trim().equals("Volume")) {
                throw new ProcessingException("Invalid header: " + firstLine);
            }
            headerValid = true;

            List<String> currentChunk = new ArrayList<>(optimalChunkSize);
            String line;
            while ((line = reader.readLine()) != null) {
                currentChunk.add(line);
                if (currentChunk.size() >= optimalChunkSize) {
                    List<String> chunk = new ArrayList<>(currentChunk);
                    currentChunk.clear();
                    futures.add(executor.submit(() -> processChunk(chunk, results)));
                }
            }

            if (!currentChunk.isEmpty()) {
                List<String> chunk = new ArrayList<>(currentChunk);
                futures.add(executor.submit(() -> processChunk(chunk, results)));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new ProcessingException("Error processing chunk", e);
                }
            }
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        return results;
    }

    private void processChunk(List<String> chunk, Map<Integer, TickerStats> results) {
        for(String line : chunk) {
            String[] parts = line.split(",");
            if(parts.length != 4) {
                continue;
            }

            String date = parts[0].trim();
            String ticker = parts[1].trim();
            double close = Double.parseDouble(parts[2].trim());
            long volume = (long) Double.parseDouble(parts[3].trim());

            int year = getYear(date);

            results.compute(year, (k, v) -> {
                if (v == null) {
                    TickerStatsImpl stats = new TickerStatsImpl();
                    stats.updateStats(ticker, close, volume);
                    return stats;
                } else {
                    ((TickerStatsImpl) v).updateStats(ticker, close, volume);
                    return v;
                }
            });
        }

    }

    private int getYear(String date) {
        return Integer.parseInt(date.substring(0, 4));
    }
}