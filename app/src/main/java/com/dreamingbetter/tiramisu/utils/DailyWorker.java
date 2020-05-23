package com.dreamingbetter.tiramisu.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.blankj.utilcode.util.AppUtils;
import com.dreamingbetter.tiramisu.entities.ContentRead;
import com.orhanobut.hawk.Hawk;

import static com.dreamingbetter.tiramisu.utils.Helper.sendNotification;

public class DailyWorker extends Worker {
    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        ContentRead contentRead = Helper.updateQuote(getApplicationContext());

        boolean notificationsEnabled = Hawk.get("notifications", true);

        if (notificationsEnabled && ! AppUtils.isAppForeground()) {
            sendNotification(getApplicationContext(), 0, contentRead.author, String.format("%s%s\"", '"', contentRead.text));
        }

        return Result.success();
    }
}