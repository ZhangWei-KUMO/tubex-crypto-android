package com.example.tubex.ui.home;

public class TopSearchItem {
    private String symbol;
    private int rank;
    private double ratio;

    public TopSearchItem(String symbol, int rank, double ratio) {
        this.symbol = symbol;
        this.rank = rank;
        this.ratio = ratio;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getRank() {
        return rank;
    }

    public double getRatio() {
        return ratio;
    }
}