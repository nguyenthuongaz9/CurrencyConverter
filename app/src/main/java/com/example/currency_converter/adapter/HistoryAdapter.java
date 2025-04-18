package com.example.currency_converter.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currency_converter.databinding.ItemHistoryBinding;
import com.example.currency_converter.models.ConversionHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<ConversionHistory> histories = new ArrayList<>();
    private OnHistoryItemClickListener listener;

    public HistoryAdapter(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ConversionHistory history = histories.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public void submitList(List<ConversionHistory> newHistories) {
        this.histories = newHistories;
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private ItemHistoryBinding binding;

        public HistoryViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ConversionHistory history) {
            binding.tvFromAmount.setText(String.format("%.2f %s",
                    history.getFromAmount(), history.getFromCurrency()));
            binding.tvToAmount.setText(String.format("%.2f %s",
                    history.getToAmount(), history.getToCurrency()));
            binding.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(history.getTimestamp())));

            itemView.setOnClickListener(v -> listener.onItemClick(history));
        }
    }

    public interface OnHistoryItemClickListener {
        void onItemClick(ConversionHistory history);
    }
}