package com.dreamingbetter.tiramisu.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentHistory;
import com.dreamingbetter.tiramisu.entities.ContentRead;

import java.util.List;

@Dao
public interface ContentHistoryDao {
    @Query("SELECT * FROM contentHistory ORDER BY timestamp")
    List<ContentRead> getAll();

    @Insert
    void insert(ContentHistory contentHistory);
}
