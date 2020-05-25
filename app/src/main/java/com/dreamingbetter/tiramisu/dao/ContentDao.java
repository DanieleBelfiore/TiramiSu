package com.dreamingbetter.tiramisu.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dreamingbetter.tiramisu.entities.Content;

import java.util.List;

@Dao
public interface ContentDao {
    @Query("SELECT * FROM content")
    List<Content> getAll();

    @Query("SELECT c.* FROM content c LEFT JOIN contentread cr ON c.uid = cr.uid WHERE cr.uid IS NULL")
    List<Content> getAllNotRead();

    @Query("SELECT c.* FROM content c JOIN contentfavorite cf ON c.uid = cf.uid ORDER BY cf.timestamp DESC")
    List<Content> getAllFavorites();

    @Insert
    void insertAll(Content... contents);

    @Query("DELETE FROM content")
    void deleteAll();
}
