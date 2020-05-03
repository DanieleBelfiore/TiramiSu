package com.dreamingbetter.tiramisu.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DailyWorker extends Worker {
    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        Helper.getNextQuote(getApplicationContext());

        return Result.success();
    }
}