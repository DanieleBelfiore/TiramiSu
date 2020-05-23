package com.dreamingbetter.tiramisu.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.blankj.utilcode.util.AppUtils;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentRead;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import static com.blankj.utilcode.util.StringUtils.getString;
import static com.dreamingbetter.tiramisu.utils.Helper.sendNotification;

public class DailyWorker extends Worker {
    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        final AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").allowMainThreadQueries().build();

        long last = Hawk.get("timestamp", 0);
        long now = System.currentTimeMillis();

        // After 24h
        if (now - last >= 86400000) {
            String[] uidRead = database.contentReadDao().getAllIds();
            List<Content> contentsNotRead = database.contentDao().getAllNotRead(uidRead);

            // No quote are available
            if (contentsNotRead.size() == 0) {
                database.contentReadDao().deleteAll();
                contentsNotRead = database.contentDao().getAll();
            }

            int index = getRandomNumberInRange(0, contentsNotRead.size() - 1);

            Content content = contentsNotRead.get(index);

            Hawk.put("content", content);
            Hawk.put("timestamp", now);

            ContentRead contentRead = new ContentRead();
            contentRead.uid = content.uid;
            contentRead.author = content.author;
            contentRead.text = content.text;
            contentRead.timestamp = now;

            database.contentReadDao().insert(contentRead);

            EventBus.getDefault().post(new UpdateQuoteEvent());

            boolean notificationsEnabled = Hawk.get("notifications", true);

            if (notificationsEnabled && ! AppUtils.isAppForeground()) {
                sendNotification(getApplicationContext(), 0, getString(R.string.notification));
            }
        }

        return Result.success();
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) return min;

        return new Random().nextInt((max - min) + 1) + min;
    }
}