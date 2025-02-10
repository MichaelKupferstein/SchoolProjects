package edu.yu.parallel.implementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import edu.yu.parallel.DataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerStats;

public class ParallelDataProcessor_1 implements DataProcessor {


    @Override
    public Map<Integer, TickerStats> processFile(String filePath) throws IOException, ProcessingException {


        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new ProcessingException("File is empty");
            }

            String[] header = firstLine.split(",");
            if(header.length != 4 || !header[0].trim().equals("Date") || !header[1].trim().equals("Ticker") || !header[2].trim().equals("Adj Close") || !header[3].trim().equals("Volume")) {
                throw new ProcessingException("Invalid header: " + firstLine);
            }
            String line;
            while((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        final int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Map<Integer,TickerStatsImpl>>> futures = new ArrayList<>();

        int chunkSize = 10000;
        for(int i = 0; i < lines.size(); i += chunkSize){
            int end = Math.min(i + chunkSize, lines.size());
            List<String> chunk = lines.subList(i, end);
            futures.add(executor.submit(() -> processChunk(chunk)));
        }

        Map<Integer,TickerStats> result = new HashMap<>();

        try{
            for(Future<Map<Integer,TickerStatsImpl>> future : futures){
                Map<Integer,TickerStatsImpl> chunkResult = future.get();
                for(Map.Entry<Integer,TickerStatsImpl> entry : chunkResult.entrySet()){
                    int year = entry.getKey();
                    TickerStatsImpl stats = entry.getValue();
                    TickerStats existingStats = result.get(year);
                    if(existingStats == null){
                        result.put(year, stats);
                    } else {
                        ((TickerStatsImpl)existingStats).updateStats(stats.getHighestCloseTicker(), stats.getHighestClose(), stats.getHighestVolume());
                    }
                }
            }
        } catch (Exception e){
            throw new ProcessingException("Error processing chunks", e);
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

        return result;
    }

    private Map<Integer, TickerStatsImpl> processChunk(List<String> chunk) {
        Map<Integer, TickerStatsImpl> tickerStatsMap = new HashMap<>();
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

            TickerStatsImpl stats = tickerStatsMap.get(year);
            if(stats == null) {
                stats = new TickerStatsImpl();
                tickerStatsMap.put(year, stats);
            }
            stats.updateStats(ticker, close, volume);
        }
        return tickerStatsMap;
    }

    private int getYear(String date) {
        return Integer.parseInt(date.substring(0, 4));
    }
}