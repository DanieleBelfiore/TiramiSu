package com.dreamingbetter.tiramisu.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity
public class ContentRead {
    @PrimaryKey
    @NonNull
    public String uid;

    @NonNull
    public int timestamp;
}