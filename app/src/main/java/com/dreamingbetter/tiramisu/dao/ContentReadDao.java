package com.dreamingbetter.tiramisu.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dreamingbetter.tiramisu.entities.ContentRead;

@Dao
public interface ContentReadDao {
    @Query("SELECT uid FROM contentRead")
    String[] getAllIds();

    @Insert
    void insert(ContentRead contentRead);

    @Query("DELETE FROM contentRead")
    void deleteAll();
}
