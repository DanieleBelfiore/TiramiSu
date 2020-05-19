package com.dreamingbetter.tiramisu.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentRead;

import java.util.List;

@Dao
public interface ContentReadDao {
    @Query("SELECT uid FROM contentRead")
    String[] getAllIds();

    @Insert
    void insert(ContentRead contentRead);

    @Query("DELETE FROM contentRead")
    void deleteAll();
}
