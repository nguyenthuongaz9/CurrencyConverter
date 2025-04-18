package com.example.currency_converter.data.api;

import java.util.Map;

public class HistoricalRatesResponse {
    private String date;
    private Map<String, Double> rates;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}