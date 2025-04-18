package com.example.currency_converter.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.currency_converter.data.database.AppDatabase;
import com.example.currency_converter.data.database.HistoryDao;
import com.example.currency_converter.models.ConversionHistory;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private final HistoryDao historyDao;
    private final LiveData<List<ConversionHistory>> allHistory;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyDao = AppDatabase.getDatabase(application).historyDao();
        allHistory = historyDao.getAllHistory();
        isLoading.setValue(false);
    }

    public LiveData<List<ConversionHistory>> getAllHistory() {
        return allHistory;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void clearHistory() {
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                historyDao.clearAll();
                isLoading.postValue(false);
            } catch (Exception e) {
                error.postValue("Failed to clear history: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }

    public void deleteHistoryItem(int id) {
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                historyDao.deleteById(id);
                isLoading.postValue(false);
            } catch (Exception e) {
                error.postValue("Failed to delete item: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }

    public void refreshHistory() {
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                allHistory.getValue();
                isLoading.postValue(false);
            } catch (Exception e) {
                error.postValue("Failed to refresh: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }
}