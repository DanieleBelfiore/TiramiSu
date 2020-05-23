package com.dreamingbetter.tiramisu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentFavorite;
import com.dreamingbetter.tiramisu.types.QuoteBook;
import com.dreamingbetter.tiramisu.ui.favorite.FavoriteFragment;
import com.dreamingbetter.tiramisu.utils.DailyWorker;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.impl.utils.futures.SettableFuture;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_history, R.id.navigation_settings).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.action_bar_icon);
        }

        final AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").allowMainThreadQueries().build();

        if (Hawk.get("lang", -1) < 0) {
            Hawk.put("lang", 0);

            // System Locale
            if (Locale.getDefault().getLanguage().equals("en")) {
                Hawk.put("lang", 1);
            }
        }

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                try {
                    DataSnapshot build = data.child("release");

                    if (BuildConfig.DEBUG) {
                        build = data.child("debug");
                    }

                    int lang = Hawk.get("lang", 0);

                    DataSnapshot languageData = build.child(String.valueOf(lang));
                    QuoteBook quoteBook = languageData.getValue(QuoteBook.class);
                    QuoteBook prevQuoteBook = Hawk.get("quoteBook", null);

                    if (quoteBook != null && !GsonUtils.toJson(quoteBook).equals(GsonUtils.toJson(prevQuoteBook))) {
                        database.contentDao().deleteAll();

                        Content[] contents = new Content[quoteBook.contents.size()];
                        quoteBook.contents.toArray(contents);

                        database.contentDao().insertAll(contents);

                        Hawk.put("quoteBook", quoteBook);
                    }

                    long last = Hawk.get("timestamp", 0L);

                    // Every 24h
                    long diff = TimeUtils.getTimeSpanByNow(last, TimeConstants.HOUR);
                    if (diff < 0 || diff >= 24) {
                        Helper.updateQuote(getApplicationContext());
                    }

                    ListenableFuture<List<WorkInfo>> workersInfo = WorkManager.getInstance(getApplicationContext()).getWorkInfosForUniqueWork("nextQuote");
                    List<WorkInfo> workers = workersInfo.get();

                    if (workers == null || workers.size() == 0) {
                        Helper.addWorker(getApplicationContext(), "nextQuote");
                    }
                } catch (Exception e) {
                    Log.w("Error", "Failed to read value.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });
    }
}
