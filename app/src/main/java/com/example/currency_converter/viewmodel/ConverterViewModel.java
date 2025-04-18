package com.example.currency_converter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.currency_converter.data.respository.CurrencyRepository;
import com.example.currency_converter.models.ConversionHistory;
import com.example.currency_converter.models.Currency;
import com.example.currency_converter.models.ExchangeRate;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConverterViewModel extends AndroidViewModel {
    private CurrencyRepository repository;
    private MutableLiveData<Double> conversionResult = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ConverterViewModel(@NonNull Application application) {
        super(application);
        repository = new CurrencyRepository(application);
    }

    public List<Currency> getCurrencies() {
        return repository.getAllCurrencies();
    }

    public void convertCurrency(double amount, String from, String to) {
        loading.setValue(true);
        repository.getExchangeRate(from, to, new Callback<ExchangeRate>() {
            @Override
            public void onResponse(Call<ExchangeRate> call, Response<ExchangeRate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Double rate = response.body().getRates().get(to);
                    if (rate != null) {
                        double result = amount * rate;
                        conversionResult.setValue(result);

                        repository.saveConversion(new ConversionHistory(
                                from, to, amount, result
                        ));
                    } else {
                        error.setValue("Rate not found");
                    }
                } else {
                    error.setValue("Conversion failed");
                }
                loading.setValue(false);
            }

            @Override
            public void onFailure(Call<ExchangeRate> call, Throwable t) {
                error.setValue(t.getMessage());
                loading.setValue(false);
            }
        });
    }

    public MutableLiveData<Double> getConversionResult() {
        return conversionResult;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }
    public void getExchangeRate(String from, String to, Callback<ExchangeRate> callback) {
        repository.getExchangeRate(from, to, callback);
    }

}