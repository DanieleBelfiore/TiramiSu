package com.dreamingbetter.tiramisu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.types.PhraseBook;
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

import java.util.List;

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

        final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        final String lang = sharedPref.getString("lang", "it");

        final AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").build();

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

                                if (phraseBook.version == 1 && phraseBook.lang.equals(lang)) {
                                    String prevPhraseBook = sharedPref.getString("phraseBook", "");
                                    String newPhraseBook = GsonUtils.toJson(phraseBook);

                                    if (!newPhraseBook.equals(prevPhraseBook)) {
                                        database.contentDao().deleteAll();

                                        Content[] contents = new Content[phraseBook.contents.size()];
                                        phraseBook.contents.toArray(contents);

                                        database.contentDao().insertAll(contents);

                                        List<Content> prova = database.contentDao().getAll();

                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("phraseBook", newPhraseBook);
                                        editor.commit();
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
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });

        long last = sharedPref.getLong("timestamp", 0);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Content> prova = database.contentDao().getAll();
            }
        });
    }

    long now () {
        return System.currentTimeMillis() / 1000L;
    }
}
