package com.example.currency_converter.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.currency_converter.databinding.FragmentConverterBinding;
import com.example.currency_converter.models.Currency;
import com.example.currency_converter.models.ExchangeRate;
import com.example.currency_converter.viewmodel.ConverterViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConverterFragment extends Fragment {
    private FragmentConverterBinding binding;
    private ConverterViewModel viewModel;
    private ArrayAdapter<String> fromAdapter, toAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConverterBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ConverterViewModel.class);

        setupViews();
        setupObservers();
        loadPopularRates();

        return binding.getRoot();
    }

    private void setupViews() {
        List<Currency> currencies = viewModel.getCurrencies();

        fromAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getCurrencyDisplayList(currencies)
        );
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spFromCurrency.setAdapter(fromAdapter);

        toAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getCurrencyDisplayList(currencies)
        );
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spToCurrency.setAdapter(toAdapter);

        setDefaultSelections(currencies);

        binding.btnConvert.setOnClickListener(v -> convertCurrency());
        binding.btnSwap.setOnClickListener(v -> swapCurrencies());

        binding.etAmount.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                convertCurrency();
                return true;
            }
            return false;
        });
    }

    private void setDefaultSelections(List<Currency> currencies) {
        int usdIndex = -1, eurIndex = -1;
        for (int i = 0; i < currencies.size(); i++) {
            if (currencies.get(i).getCode().equals("USD")) {
                usdIndex = i;
            }
            if (currencies.get(i).getCode().equals("EUR")) {
                eurIndex = i;
            }
        }

        if (usdIndex != -1) binding.spFromCurrency.setSelection(usdIndex);
        if (eurIndex != -1) binding.spToCurrency.setSelection(eurIndex);
    }

    private List<String> getCurrencyDisplayList(List<Currency> currencies) {
        List<String> displayList = new ArrayList<>();
        for (Currency currency : currencies) {
            displayList.add(String.format("%s - %s", currency.getCode(), currency.getName()));
        }
        return displayList;
    }

    private void setupObservers() {
        viewModel.getConversionResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                binding.tvResult.setText(String.format(Locale.getDefault(), "%.2f", result));
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnConvert.setEnabled(!isLoading);
        });
    }

    private void convertCurrency() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.etAmount.getWindowToken(), 0);

        String amountText = binding.etAmount.getText().toString().trim();
        if (amountText.isEmpty()) {
            binding.etAmount.setError("Please enter an amount");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            List<Currency> currencies = viewModel.getCurrencies();

            String fromCurrency = currencies.get(binding.spFromCurrency.getSelectedItemPosition()).getCode();
            String toCurrency = currencies.get(binding.spToCurrency.getSelectedItemPosition()).getCode();

            viewModel.convertCurrency(amount, fromCurrency, toCurrency);
        } catch (NumberFormatException e) {
            binding.etAmount.setError("Invalid amount format");
        }
    }

    private void swapCurrencies() {
        int fromPos = binding.spFromCurrency.getSelectedItemPosition();
        int toPos = binding.spToCurrency.getSelectedItemPosition();

        binding.spFromCurrency.setSelection(toPos);
        binding.spToCurrency.setSelection(fromPos);

        String resultText = binding.tvResult.getText().toString();
        if (!resultText.isEmpty() && !resultText.equals("0.00")) {
            binding.etAmount.setText(resultText);
            convertCurrency();
        }
    }

    private void loadPopularRates() {
        List<Currency> currencies = viewModel.getCurrencies();
        int usdIndex = getCurrencyIndex(currencies, "USD");
        int eurIndex = getCurrencyIndex(currencies, "EUR");
        int jpyIndex = getCurrencyIndex(currencies, "JPY");
        int gbpIndex = getCurrencyIndex(currencies, "GBP");

        if (usdIndex != -1 && eurIndex != -1) {
            loadRateDisplay("USD → EUR", "USD", "EUR", binding.tvRate1);
        }
        if (usdIndex != -1 && gbpIndex != -1) {
            loadRateDisplay("USD → GBP", "USD", "GBP", binding.tvRate2);
        }
        if (eurIndex != -1 && jpyIndex != -1) {
            loadRateDisplay("EUR → JPY", "EUR", "JPY", binding.tvRate3);
        }
    }

    private void loadRateDisplay(String title, String from, String to, TextView textView) {
        textView.setText(String.format("%s: loading...", title));
        viewModel.getExchangeRate(from, to, new Callback<ExchangeRate>() {
            @Override
            public void onResponse(Call<ExchangeRate> call, Response<ExchangeRate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Double rate = response.body().getRates().get(to);
                    if (rate != null) {
                        String rateText = String.format(Locale.getDefault(), "%s: %.4f", title, rate);
                        textView.setText(rateText);
                    }
                }
            }

            @Override
            public void onFailure(Call<ExchangeRate> call, Throwable t) {
                textView.setText(String.format("%s: failed to load", title));
            }
        });
    }

    private int getCurrencyIndex(List<Currency> currencies, String code) {
        for (int i = 0; i < currencies.size(); i++) {
            if (currencies.get(i).getCode().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}