package com.dreamingbetter.tiramisu.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dreamingbetter.tiramisu.dao.ContentDao;
import com.dreamingbetter.tiramisu.dao.ContentHistoryDao;
import com.dreamingbetter.tiramisu.dao.ContentReadDao;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentHistory;
import com.dreamingbetter.tiramisu.entities.ContentRead;

@Database(entities = {Content.class, ContentRead.class, ContentHistory.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContentDao contentDao();
    public abstract ContentReadDao contentReadDao();
    public abstract ContentHistoryDao contentHistoryDao();
}
