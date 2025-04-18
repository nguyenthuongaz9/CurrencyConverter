package com.example.currency_converter.ui;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.example.currency_converter.data.api.HistoricalRatesResponse;
import com.example.currency_converter.databinding.FragmentChartsBinding;
import com.example.currency_converter.viewmodel.ChartsViewModel;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class ChartsFragment extends Fragment {
    private FragmentChartsBinding binding;
    private ChartsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChartsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ChartsViewModel.class);

        setupObservers();
        binding.etStartDate.setOnClickListener(v -> showDatePickerDialog(binding.etStartDate));
        binding.etEndDate.setOnClickListener(v -> showDatePickerDialog(binding.etEndDate));

        binding.btnLoadChart.setOnClickListener(v -> loadChartData());

        return binding.getRoot();
    }

    private void setupObservers() {
        viewModel.getChartData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && !data.isEmpty()) {
                setupChart(data);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadChartData() {
        String startDate = binding.etStartDate.getText().toString();
        String endDate = binding.etEndDate.getText().toString();

        viewModel.loadHistoricalData(startDate, endDate);
    }

    private void setupChart(List<HistoricalRatesResponse> data) {
        List<ILineDataSet> dataSets = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        String[] currencies = {"CHF", "MXN", "ZAR", "INR", "CNY", "THB", "AUD", "ILS", "KRW"};
        int[] colors = {
                Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA,
                Color.YELLOW, Color.GRAY, Color.BLACK, Color.DKGRAY
        };

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

        for (int i = 0; i < currencies.length; i++) {
            String currency = currencies[i];
            List<Entry> entries = new ArrayList<>();

            for (int j = 0; j < data.size(); j++) {
                HistoricalRatesResponse dayData = data.get(j);
                Double rate = dayData.getRates().get(currency);
                if (rate != null) {
                    entries.add(new Entry(j, rate.floatValue()));
                }

                if (i == 0) {
                    String rawDate = dayData.getDate();
                    String formattedDate = rawDate;
                    try {
                        Date date = inputFormat.parse(rawDate);
                        formattedDate = outputFormat.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    labels.add(formattedDate);
                }
            }

            LineDataSet dataSet = new LineDataSet(entries, currency);
            dataSet.setColor(colors[i % colors.length]);
            dataSet.setCircleColor(Color.CYAN);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setDrawValues(false);

            dataSets.add(dataSet);
        }

        LineData lineData = new LineData(dataSets);
        binding.lineChart.setData(lineData);
        binding.lineChart.invalidate();

        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-30f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size(), true);

        YAxis leftAxis = binding.lineChart.getAxisLeft();
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = binding.lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        binding.lineChart.setExtraBottomOffset(30f);
    }


    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    editText.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
