package com.dreamingbetter.tiramisu.ui.favorite;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public FavoriteFragment() { }

    @SuppressWarnings("unused")
    public static FavoriteFragment newInstance(int columnCount) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);

        FragmentActivity activity = getActivity();

        if (activity == null) return view;

        AppDatabase database = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "db").allowMainThreadQueries().build();

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            FavoriteRecyclerViewAdapter adapter = new FavoriteRecyclerViewAdapter(database.contentFavoriteDao().getAll());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
