package com.example.currency_converter.data.respository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.example.currency_converter.data.api.ApiClient;
import com.example.currency_converter.data.api.ApiService;
import com.example.currency_converter.data.api.HistoricalRatesResponse;
import com.example.currency_converter.data.database.AppDatabase;
import com.example.currency_converter.data.database.HistoryDao;
import com.example.currency_converter.models.ConversionHistory;
import com.example.currency_converter.models.Currency;
import com.example.currency_converter.models.ExchangeRate;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Callback;

public class CurrencyRepository {
    private ApiService apiService;
    private HistoryDao historyDao;
    private List<Currency> currencies;


    public CurrencyRepository(Application application) {
        apiService = ApiClient.getApiService();
        historyDao = AppDatabase.getDatabase(application).historyDao();
        initializeCurrencies();
    }

    private void initializeCurrencies() {
        currencies = Arrays.asList(
                new Currency("USD", "US Dollar", "$"),
                new Currency("EUR", "Euro", "€"),
                new Currency("JPY", "Japanese Yen", "¥"),
                new Currency("GBP", "British Pound", "£"),
                new Currency("AUD", "Australian Dollar", "A$"),
                new Currency("CAD", "Canadian Dollar", "C$"),
                new Currency("CHF", "Swiss Franc", "CHF"),
                new Currency("CNY", "Chinese Yuan", "¥"),
                new Currency("INR", "Indian Rupee", "₹"),
                new Currency("KRW", "South Korean Won", "₩")
        );
    }

    public List<Currency> getAllCurrencies() {
        return currencies;
    }

    public void getExchangeRate(String from, String to, Callback<ExchangeRate> callback) {
        apiService.getLatestRates(from, to).enqueue(callback);
    }


    public void saveConversion(ConversionHistory history) {
        new Thread(() -> historyDao.insert(history)).start();
    }



    public void getHistoricalRates(String startDate, String endDate, Callback<List<HistoricalRatesResponse>> callback) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();


                String localStartDate = startDate;
                String localEndDate = endDate;

                if (localStartDate.isEmpty() || localEndDate.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    localEndDate = sdf.format(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    localStartDate = sdf.format(calendar.getTime());
                }

                String url = "https://api.frankfurter.app/" + localStartDate + ".." + localEndDate + "?to=AUD,BGN,BRL,CAD,CHF,CNY,CZK,DKK,GBP,HKD,HUF,IDR,ILS,INR,ISK,JPY,KRW,MXN,MYR,NOK,NZD,PHP,PLN,RON,SEK,SGD,THB,TRY,USD,ZAR";

                Log.d("HistoricalRatesURL", url);

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject ratesJson = jsonObject.getJSONObject("rates");

                    List<HistoricalRatesResponse> responseList = new ArrayList<>();

                    Iterator<String> dateKeys = ratesJson.keys();
                    while (dateKeys.hasNext()) {
                        String date = dateKeys.next();
                        JSONObject dateRates = ratesJson.getJSONObject(date);

                        HistoricalRatesResponse responseObj = new HistoricalRatesResponse();
                        responseObj.setDate(date);

                        Map<String, Double> ratesMap = new HashMap<>();
                        Iterator<String> currencyKeys = dateRates.keys();
                        while (currencyKeys.hasNext()) {
                            String currency = currencyKeys.next();
                            ratesMap.put(currency, dateRates.getDouble(currency));
                        }

                        responseObj.setRates(ratesMap);
                        responseList.add(responseObj);
                    }


                    Collections.sort(responseList, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onResponse(null, retrofit2.Response.success(responseList))
                    );
                } else {
                    String errorMessage = "";
                    try {
                        if (response.body() != null) {
                            errorMessage += " - " + response.body().string();
                        } else {
                            errorMessage += " - " + response.message();
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    Log.d("Call api failed", errorMessage);

                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure(null, e)
                );
            }
        }).start();
    }


}
