package com.dreamingbetter.tiramisu.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@SuppressWarnings("NullableProblems")
@Entity
public class ContentRead {
    @PrimaryKey
    @NonNull
    public String uid;

    @NonNull
    public long timestamp;
}