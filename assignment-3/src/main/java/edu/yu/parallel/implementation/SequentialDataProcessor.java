package edu.yu.parallel.implementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.yu.parallel.DataProcessor;
import edu.yu.parallel.ProcessingException;
import edu.yu.parallel.TickerStats;

public class SequentialDataProcessor implements DataProcessor {

    @Override
    public Map<Integer, TickerStats> processFile(String filePath) throws IOException, ProcessingException {

        Map<Integer, TickerStats> tickerStatsMap = new HashMap<>();

        try(BufferedReader reader = new BufferedReader((new FileReader(filePath)))) {
            String firstLine = reader.readLine();
            if(firstLine == null) {
                throw new ProcessingException("File is empty");
            }

            String[] header = firstLine.split(",");
            if(header.length != 4 || !header[0].trim().equals("Date") || !header[1].trim().equals("Ticker") || !header[2].trim().equals("Adj Close") || !header[3].trim().equals("Volume")) {
                throw new ProcessingException("Invalid header: " + firstLine);
            }

            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length != 4) {
                    throw new ProcessingException("Invalid line: " + line);
                }
                String date = parts[0].trim();
                //System.out.println(date);
                String ticker = parts[1].trim();
                //System.out.println(ticker);
                double close = Double.parseDouble(parts[2].trim());
                //System.out.println(close);
                long volume = (long) Double.parseDouble(parts[3].trim());
                //System.out.println(volume);

                int year = getYear(date);

                //System.out.println("Data: " + date + ", " + ticker + ", " + close + ", " + volume + ", " + year);

                TickerStats stats = tickerStatsMap.get(year);
                //System.out.println("Stats: " + stats);
                if(stats == null) {
                    stats = new TickerStatsImpl();
                    tickerStatsMap.put(year, stats);
                }
                ((TickerStatsImpl)stats).updateStats(ticker, close, volume);

            }

        }

        return tickerStatsMap;
    }

    private int getYear(String date) {
        return Integer.parseInt(date.substring(0, 4));
    }
}