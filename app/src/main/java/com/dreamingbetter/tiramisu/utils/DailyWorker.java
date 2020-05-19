package com.dreamingbetter.tiramisu.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentHistory;
import com.dreamingbetter.tiramisu.entities.ContentRead;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import static com.blankj.utilcode.util.StringUtils.getString;
import static com.dreamingbetter.tiramisu.utils.Helper.getValue;
import static com.dreamingbetter.tiramisu.utils.Helper.sendNotification;
import static com.dreamingbetter.tiramisu.utils.Helper.setValue;

public class DailyWorker extends Worker {
    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        final AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").build();

        long last = Long.parseLong(getValue(getApplicationContext(), "timestamp", "0"));
        final long now = System.currentTimeMillis() / 1000L;

        // After 24h
        if (now - last >= 86400000) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String[] uidRead = database.contentReadDao().getAllIds();
                    List<Content> contentsNotRead = database.contentDao().getAllNotRead(uidRead);

                    // No quote are available
                    if (contentsNotRead.size() == 0) {
                        database.contentReadDao().deleteAll();
                        uidRead = database.contentReadDao().getAllIds();
                        contentsNotRead = database.contentDao().getAllNotRead(uidRead);
                    }

                    int index = getRandomNumberInRange(0, contentsNotRead.size() - 1);

                    Content content = contentsNotRead.get(index);
                    String jsonContent = GsonUtils.toJson(content);

                    setValue(getApplicationContext(), "content", jsonContent);
                    setValue(getApplicationContext(), "timestamp", String.valueOf(now));

                    ContentRead contentRead = new ContentRead();
                    contentRead.uid = content.uid;
                    contentRead.timestamp = now;
                    database.contentReadDao().insert(contentRead);

                    ContentHistory contentHistory = new ContentHistory();
                    contentHistory.uid = content.uid;
                    contentHistory.timestamp = now;
                    database.contentHistoryDao().insert(contentHistory);

                    EventBus.getDefault().post(new UpdateQuoteEvent());

                    boolean notificationsEnabled = Boolean.parseBoolean(Helper.getValue(getApplicationContext(), "notifications", "true"));

                    if (notificationsEnabled && ! AppUtils.isAppForeground()) {
                        sendNotification(getApplicationContext(), 0, getString(R.string.notification));
                    }
                }
            });
        }

        return Result.success();
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();

        return r.nextInt((max - min) + 1) + min;
    }
}