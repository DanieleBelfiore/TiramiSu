package com.dreamingbetter.tiramisu.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dreamingbetter.tiramisu.entities.ContentRead;

@Dao
public interface ContentReadDao {
    @Insert
    void insert(ContentRead contentRead);

    @Query("DELETE FROM contentRead")
    void deleteAll();
}
