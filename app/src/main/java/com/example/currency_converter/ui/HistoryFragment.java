package com.example.currency_converter.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.currency_converter.adapter.HistoryAdapter;
import com.example.currency_converter.databinding.FragmentHistoryBinding;
import com.example.currency_converter.viewmodel.HistoryViewModel;


public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private HistoryViewModel viewModel;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        setupRecyclerView();
        setupObservers();

        binding.btnClearHistory.setOnClickListener(v -> viewModel.clearHistory());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(history -> {

        });

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getAllHistory().observe(getViewLifecycleOwner(), histories -> {
            adapter.submitList(histories);
            binding.tvEmpty.setVisibility(histories.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}