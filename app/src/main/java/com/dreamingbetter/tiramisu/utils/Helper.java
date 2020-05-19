package com.dreamingbetter.tiramisu.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.dreamingbetter.tiramisu.MainActivity;
import com.dreamingbetter.tiramisu.R;

import static com.blankj.utilcode.util.StringUtils.getString;

public class Helper {
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
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
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