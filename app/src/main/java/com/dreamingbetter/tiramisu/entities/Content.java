package com.dreamingbetter.tiramisu.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@SuppressWarnings({"NullableProblems", "RedundantSuppression", "NotNullFieldNotInitialized"})
@Entity
public class Content {
    @PrimaryKey
    @NonNull
    public String uid;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "text")
    public String text;
}