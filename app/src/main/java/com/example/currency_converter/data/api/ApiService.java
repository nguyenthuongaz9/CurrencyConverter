package com.example.currency_converter.data.api;

import retrofit2.http.Path;
import retrofit2.http.Query;
import com.example.currency_converter.models.ExchangeRate;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("latest")
    Call<ExchangeRate> getLatestRates(
            @Query("from") String baseCurrency,
            @Query("to") String symbols
    );

    @GET("timeseries")
    Call<HistoricalRatesResponse> getHistoricalRates(
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("base") String base
    );


}
