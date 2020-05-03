package com.dreamingbetter.tiramisu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.types.PhraseBook;
import com.dreamingbetter.tiramisu.utils.DailyWorker;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        final AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").build();

        final String lang = Helper.getValue(getApplicationContext(), "lang", "it");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                PhraseBook phraseBook = postSnapshot.getValue(PhraseBook.class);

                                if (phraseBook.lang.equals(lang)) {
                                    String prevPhraseBook = Helper.getValue(getApplicationContext(), "phraseBook", "");
                                    String newPhraseBook = GsonUtils.toJson(phraseBook);

                                    if (!newPhraseBook.equals(prevPhraseBook)) {
                                        database.contentDao().deleteAll();

                                        Content[] contents = new Content[phraseBook.contents.size()];
                                        phraseBook.contents.toArray(contents);

                                        database.contentDao().insertAll(contents);

                                        Helper.setValue(getApplicationContext(), "phraseBook", newPhraseBook);

                                        PeriodicWorkRequest worker = new PeriodicWorkRequest.Builder(DailyWorker.class, 1, TimeUnit.HOURS).build();
                                        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("nextQuote", ExistingPeriodicWorkPolicy.KEEP, worker);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.w("Error", "Failed to read value.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });
    }
}
