package com.dreamingbetter.tiramisu.utils;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.room.Room;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.dreamingbetter.tiramisu.MainActivity;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentRead;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Helper {
    public static void sendQuoteNotification(Context context, int requestCode, Content content) {
        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, new Intent(context, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        Intent intentAction = new Intent(context, NotificationActionReceiver.class);
        intentAction.putExtra("action","addOrRemoveToFavorites");
        intentAction.putExtra("uid", content.uid);

        String title = content.author;
        String message = String.format("%s%s\"", '"', content.text);

        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "db").allowMainThreadQueries().build();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.logo_action_bar)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .addAction(R.drawable.ic_star_primary_24dp,
                        (database.contentFavoriteDao().isFavorite(content.uid) == 1) ? context.getString(R.string.remove_from_favorites) : context.getString(R.string.add_to_favorites),
                        PendingIntent.getBroadcast(context, requestCode, intentAction, PendingIntent.FLAG_IMMUTABLE)
                );

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
        WorkManager.getInstance(context).cancelAllWorkByTag(name);

        // To have a new quote every day at desired time
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        dueDate.set(Calendar.HOUR_OF_DAY, Hawk.get("notificationHour", 8));
        dueDate.set(Calendar.MINUTE, Hawk.get("notificationMinute", 0));
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        OneTimeWorkRequest dailyWorkRequest = new OneTimeWorkRequest.Builder(DailyWorker.class).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS).addTag(name).build();
        WorkManager.getInstance(context).enqueue(dailyWorkRequest);
    }

    @SuppressLint("DiscouragedApi")
    public static int getResId(Context context, String category, String id) {
        return context.getResources().getIdentifier(id, category,  context.getPackageName());
    }

    public static void checkNewQuote(Context context) {
        long last = Hawk.get("timestamp", 0L);

        // Every 24h
        long diff = -TimeUtils.getTimeSpanByNow(last, TimeConstants.HOUR);

        if (diff >= 24) {
            Helper.updateQuote(context);
        }
    }

    @SuppressLint("SetWorldReadable")
    public static Uri shareUriBitmap(Context context, Bitmap bitmap) {
        try {
            File file = new File(context.getCacheDir(), System.currentTimeMillis() / 1000 + ".png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            //noinspection ResultOfMethodCallIgnored
            file.setReadable(true, false);
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}