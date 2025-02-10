package edu.yu.parallel.implementation;

import edu.yu.parallel.TickerStats;

public class TickerStatsImpl implements TickerStats {

    private String highestCloseTicker;
    private double highestClose;
    private String highestVolumeTicker;
    private long highestVolume;
    private int quoteCount;

    public TickerStatsImpl() {
        this.highestCloseTicker = "";
        this.highestClose = Double.MIN_VALUE;
        this.highestVolumeTicker = "";
        this.highestVolume = Long.MIN_VALUE;
        this.quoteCount = 0;
    }

    public synchronized void updateStats(String ticker, double close, long volume) {
        //System.out.println("Ticker: " + ticker + ", Close: " + close + ", Volume: " + volume);

        if (close > this.highestClose) {
            this.highestClose = close;
            this.highestCloseTicker = ticker;
        }
        if (volume > this.highestVolume) {
            this.highestVolume = volume;
            this.highestVolumeTicker = ticker;
        }
        this.quoteCount++;
    }

    @Override
    public String getHighestCloseTicker() {
        return this.highestCloseTicker;
    }

    @Override
    public double getHighestClose() {
        return this.highestClose;
    }

    @Override
    public String getHighestVolumeTicker() {
        return this.highestVolumeTicker;
    }

    @Override
    public long getHighestVolume() {
        return this.highestVolume;
    }

    @Override
    public int getQuoteCount() {
        return this.quoteCount;
    }

    @Override
    public String toString() {
        return String.format("Quotes: %d, Highest Close: (%s, %.2f), Highest Volume: (%s, %d)",
                getQuoteCount(),
                getHighestCloseTicker(), getHighestClose(),
                getHighestVolumeTicker(), getHighestVolume());
    }
}
