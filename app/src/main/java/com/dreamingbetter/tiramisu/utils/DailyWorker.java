package com.dreamingbetter.tiramisu.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.blankj.utilcode.util.AppUtils;
import com.dreamingbetter.tiramisu.entities.Content;
import com.orhanobut.hawk.Hawk;

import static com.dreamingbetter.tiramisu.utils.Helper.sendNotification;

@SuppressWarnings("WeakerAccess")
public class DailyWorker extends Worker {
    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Content content = Helper.updateQuote(getApplicationContext());

        boolean notificationsEnabled = Hawk.get("notifications", true);

        if (notificationsEnabled && ! AppUtils.isAppForeground()) {
            sendNotification(getApplicationContext(), 0, content.author, String.format("%s%s\"", '"', content.text));
        }

        return Result.success();
    }
}