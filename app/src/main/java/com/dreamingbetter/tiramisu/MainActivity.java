package com.dreamingbetter.tiramisu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.types.QuoteBook;

import com.dreamingbetter.tiramisu.utils.Helper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;
import java.util.Locale;

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

        if (Hawk.get("lang", -1) < 0) {
            Hawk.put("lang", 0);

            // System Locale
            if (Locale.getDefault().getLanguage().equals("en")) {
                Hawk.put("lang", 1);
            }
        }

        ListenableFuture<List<WorkInfo>> workersInfo = WorkManager.getInstance(getApplicationContext()).getWorkInfosForUniqueWork("nextQuote");
        List<WorkInfo> workers = null;

        try {
            workers = workersInfo.get();
        } catch (Exception e) {
            Logger.e("Failed to read workers", e);
        }

        if (workers == null || workers.size() == 0) {
            Helper.addWorker(getApplicationContext(), "nextQuote");
        }

        final AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").allowMainThreadQueries().build();
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

                        checkNewQuote();
                    }
                } catch (Exception e) {
                    Logger.e("Failed to read values from database", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Logger.e("Failed to read values from database", e);
            }
        });

        List<Content> contents = database.contentDao().getAll();

        if (contents.isEmpty() && !NetworkUtils.isConnected()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.network_error)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            AppUtils.exitApp();
                        }
                    }).show();
        }

        checkNewQuote();
    }

    private void checkNewQuote() {
        long last = Hawk.get("timestamp", 0L);

        // Every 24h
        long diff = TimeUtils.getTimeSpanByNow(last, TimeConstants.HOUR);
        if (diff < 0 || diff >= 24) {
            Helper.updateQuote(getApplicationContext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.info_version) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME + " (build " + BuildConfig.VERSION_CODE + ")")
                    .setCancelable(true)
                    .setPositiveButton(R.string.ok, null).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
