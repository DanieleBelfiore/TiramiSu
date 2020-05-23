package com.dreamingbetter.tiramisu.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dreamingbetter.tiramisu.entities.ContentFavorite;

import java.util.List;

@Dao
public interface ContentFavoriteDao {
    @Query("SELECT * FROM contentFavorite ORDER BY timestamp DESC")
    List<ContentFavorite> getAll();

    @Query("SELECT COUNT(*) FROM ContentFavorite WHERE uid == :uid")
    int isFavorite(String uid);

    @Insert
    void insert(ContentFavorite contentFavorite);

    @Query("DELETE FROM contentFavorite WHERE uid == :uid")
    void delete(String uid);
}
