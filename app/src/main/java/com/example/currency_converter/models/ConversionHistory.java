package com.example.currency_converter.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversion_history")
public class ConversionHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String fromCurrency;
    private String toCurrency;
    private double fromAmount;
    private double toAmount;
    private long timestamp;

    public ConversionHistory(String fromCurrency, String toCurrency,
                             double fromAmount, double toAmount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
        this.timestamp = System.currentTimeMillis();
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public double getFromAmount() { return fromAmount; }
    public void setFromAmount(double fromAmount) { this.fromAmount = fromAmount; }

    public double getToAmount() { return toAmount; }
    public void setToAmount(double toAmount) { this.toAmount = toAmount; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
