package com.example.currency_converter.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.currency_converter.models.ConversionHistory;

import java.util.List;

@Dao
public interface HistoryDao {
    @Insert
    void insert(ConversionHistory history);

    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    LiveData<List<ConversionHistory>> getAllHistory();

    @Query("DELETE FROM conversion_history")
    void clearAll();

    @Query("DELETE FROM conversion_history WHERE id = :id")
    void deleteById(int id);
}