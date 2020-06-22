package com.dreamingbetter.tiramisu.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.impl.model.WorkSpec;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.dreamingbetter.tiramisu.MainActivity;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentRead;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Helper {
    public static void sendNotification(Context context, int requestCode, String title, String message) {
        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, new Intent(context, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.logo_action_bar)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.app_name));
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(R.string.app_name, notificationBuilder.build());
        }
    }

    public static Content updateQuote(Context context) {
        final AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "db").allowMainThreadQueries().build();

        List<Content> contents = database.contentDao().getAll();

        if (contents.isEmpty()) return null;

        List<Content> contentsNotRead = database.contentDao().getAllNotRead();

        // No quote are available
        if (contentsNotRead.size() == 0) {
            database.contentReadDao().deleteAll();
            contentsNotRead = database.contentDao().getAll();
        }

        int index = getRandomNumberInRange(0, contentsNotRead.size() - 1);

        Content content = contentsNotRead.get(index);

        long now = System.currentTimeMillis();

        Hawk.put("content", content);
        Hawk.put("timestamp", now);

        ContentRead contentRead = new ContentRead();
        contentRead.uid = content.uid;
        contentRead.timestamp = now;

        database.contentReadDao().insert(contentRead);

        EventBus.getDefault().post(new UpdateQuoteEvent());

        return content;
    }

    private static int getRandomNumberInRange(@SuppressWarnings("SameParameterValue") int min, int max) {
        if (min >= max) return min;

        return new Random().nextInt((max - min) + 1) + min;
    }

    public static void addWorker(Context context, String name) {
        // To have a new quote every day at desired time
        Calendar schedule = Calendar.getInstance();

        schedule.set(Calendar.HOUR_OF_DAY, Hawk.get("notificationHour", 8));
        schedule.set(Calendar.MINUTE, Hawk.get("notificationMinute", 0));
        schedule.set(Calendar.SECOND, 0);

        long delay = TimeUtils.getTimeSpanByNow(schedule.getTime(), TimeConstants.MIN);

        PeriodicWorkRequest worker = new PeriodicWorkRequest.Builder(DailyWorker.class, 24, TimeUnit.HOURS).setInitialDelay(delay, TimeUnit.MINUTES).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(name, ExistingPeriodicWorkPolicy.KEEP, worker);
    }

    public static void removeWorker(Context context, String name) {
        WorkManager.getInstance(context).cancelUniqueWork(name);
    }

    public static int getResId(Context context, String category, String id) {
        return context.getResources().getIdentifier(id, category,  context.getPackageName());
    }

    public static Content checkNewQuote(Context context) {
        long last = Hawk.get("timestamp", 0L);

        // Every 24h
        long diff = -TimeUtils.getTimeSpanByNow(last, TimeConstants.HOUR);
        if (diff >= 24) {
            return Helper.updateQuote(context);
        }

        return null;
    }
}