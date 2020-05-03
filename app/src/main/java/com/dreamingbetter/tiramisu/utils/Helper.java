package com.dreamingbetter.tiramisu.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.dreamingbetter.tiramisu.MainActivity;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentHistory;
import com.dreamingbetter.tiramisu.entities.ContentRead;

import java.util.List;
import java.util.Random;

public class Helper {
    public static void getNextQuote(final Context context) {
        final AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "db").build();

        long last = Long.parseLong(getValue(context, "timestamp", "0"));
        final long now = System.currentTimeMillis() / 1000L;

        // After 24h
        if (now - last >= 86400000) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String[] uidRead = database.contentReadDao().getAllIds();
                    List<Content> contentsNotRead = database.contentDao().getAllNotRead(uidRead);

                    // No phrase are available
                    if (contentsNotRead.size() == 0) {
                        database.contentReadDao().deleteAll();
                        uidRead = database.contentReadDao().getAllIds();
                        contentsNotRead = database.contentDao().getAllNotRead(uidRead);
                    }

                    int index = getRandomNumberInRange(0, contentsNotRead.size() - 1);

                    Content content = contentsNotRead.get(index);

                    setValue(context, "content", GsonUtils.toJson(content));
                    setValue(context, "timestamp", String.valueOf(now));

                    ContentRead contentRead = new ContentRead();
                    contentRead.uid = content.uid;
                    contentRead.timestamp = now;
                    database.contentReadDao().insert(contentRead);

                    ContentHistory contentHistory = new ContentHistory();
                    contentHistory.uid = content.uid;
                    contentHistory.timestamp = now;
                    database.contentHistoryDao().insert(contentHistory);

                    if (! AppUtils.isAppForeground()) {
                        sendNotification(context, 0, "Un nuovo pensiero è pronto per te!");
                    }
                }
            });
        }
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();

        return r.nextInt((max - min) + 1) + min;
    }

    /* REGION SharedPreferences */

    public static void setValue(Context context, String key, String text) {
        SharedPreferences sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(key, text);
        editor.commit();
    }

    public static String getValue(Context context, String key, String defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        return sharedPref.getString(key, defaultValue);
    }

    /* ENDREGION SharedPreferences */

    public static void sendNotification(Context context, int requestCode, @SuppressWarnings("SameParameterValue") String message) {
        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, new Intent(context, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.ic_card_giftcard_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle(context.getString(R.string.app_name))
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
}