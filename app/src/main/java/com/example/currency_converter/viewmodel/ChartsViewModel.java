package com.example.currency_converter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.currency_converter.data.api.HistoricalRatesResponse;
import com.example.currency_converter.data.respository.CurrencyRepository;
import com.example.currency_converter.models.Currency;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartsViewModel extends AndroidViewModel {
    private CurrencyRepository repository;
    MutableLiveData<List<HistoricalRatesResponse>> chartData = new MutableLiveData<>();

    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ChartsViewModel(@NonNull Application application) {
        super(application);
        repository = new CurrencyRepository(application);
    }

    public List<Currency> getCurrencies() {
        return repository.getAllCurrencies();
    }


    public void loadHistoricalData(String startDate, String endDate) {
        loading.setValue(true);

        repository.getHistoricalRates(startDate, endDate, new Callback<List<HistoricalRatesResponse>>() {
            @Override
            public void onResponse(Call<List<HistoricalRatesResponse>> call,
                                   Response<List<HistoricalRatesResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chartData.setValue(response.body());
                } else {
                    error.setValue("Failed to load historical data");
                }
                loading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<HistoricalRatesResponse>> call, Throwable t) {
                error.setValue(t.getMessage());
                loading.setValue(false);
            }
        });
    }



    public MutableLiveData<List<HistoricalRatesResponse>> getChartData() {
        return chartData;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }
}