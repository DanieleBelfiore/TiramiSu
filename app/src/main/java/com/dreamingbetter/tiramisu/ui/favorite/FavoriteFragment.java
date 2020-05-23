package com.dreamingbetter.tiramisu.ui.favorite;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.adapters.FavoriteRecyclerViewAdapter;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.ContentFavorite;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class FavoriteFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();

        if (activity == null) return null;

        AppDatabase database = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "db").allowMainThreadQueries().build();

        List<ContentFavorite> dataset = database.contentFavoriteDao().getAll();

        if (dataset.isEmpty()) {
            return inflater.inflate(R.layout.empty_recycler, container, false);
        }

        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        FavoriteRecyclerViewAdapter adapter = new FavoriteRecyclerViewAdapter(dataset, FavoriteFragment.this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void goToHome() {
        FragmentActivity activity = getActivity();

        if (activity == null) return;

        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

}
