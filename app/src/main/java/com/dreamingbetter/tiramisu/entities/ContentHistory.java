package com.dreamingbetter.tiramisu.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ContentHistory {
    @PrimaryKey
    @NonNull
    public String uid;

    @NonNull
    public long timestamp;
}