package com.dreamingbetter.tiramisu.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.ContentFavorite;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");

        if (action != null && action.equals("addToFavorites")) {
            String uid = intent.getStringExtra("uid");

            if (uid != null) {
                addToFavorites(context, uid);

                NotificationManagerCompat.from(context).cancel(R.string.app_name);
            }
        }

        //This is used to dismiss the notification and close the tray
        //Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //context.sendBroadcast(it);
    }

    public void addToFavorites(Context context, String uid) {
        final AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "db").allowMainThreadQueries().build();

        ContentFavorite c = new ContentFavorite();

        c.uid = uid;
        c.timestamp = System.currentTimeMillis();

        database.contentFavoriteDao().insert(c);
    }
}
