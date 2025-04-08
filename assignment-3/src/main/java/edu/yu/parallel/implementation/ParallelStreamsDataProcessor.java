package edu.yu.parallel.implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import edu.yu.parallel.DataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerStats;

public class ParallelStreamsDataProcessor implements DataProcessor {

    @Override
    public Map<Integer, TickerStats> processFile(String filePath) throws IOException, ProcessingException {
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            String firstLine = lines.findFirst()
                    .orElseThrow(() -> new ProcessingException("File is empty"));

            String[] header = firstLine.split(",");
            if (header.length != 4 || !header[0].trim().equals("Date") || !header[1].trim().equals("Ticker") || !header[2].trim().equals("Adj Close") || !header[3].trim().equals("Volume")) {
                throw new ProcessingException("Invalid header: " + firstLine);
            }
        }

        return Files.lines(Path.of(filePath))
                .skip(1)
                .parallel()
                .map(this::processLine)
                .collect(Collectors.groupingByConcurrent(
                        data -> data.year,
                        Collectors.collectingAndThen(Collectors.toList(), this::createYearlyStats)
                ));
    }

    private StockData processLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }
        return new StockData(Integer.parseInt(parts[0].trim().substring(0, 4)), parts[1].trim(), Double.parseDouble(parts[2].trim()), (long) Double.parseDouble(parts[3].trim()));
    }

    private TickerStats createYearlyStats(List<StockData> yearData) {
        TickerStatsImpl stats = new TickerStatsImpl();
        yearData.forEach(data ->
                stats.updateStats(data.ticker, data.close, data.volume)
        );
        return stats;
    }

    private static class StockData {
        final int year;
        final String ticker;
        final double close;
        final long volume;

        StockData(int year, String ticker, double close, long volume) {
            this.year = year;
            this.ticker = ticker;
            this.close = close;
            this.volume = volume;
        }
    }
}