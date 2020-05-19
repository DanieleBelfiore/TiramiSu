package com.dreamingbetter.tiramisu.ui.history;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.adapters.HistoryRecyclerViewAdapter;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentRead;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    //private OnListFragmentInteractionListener mListener;

    public HistoryFragment() { }

    @SuppressWarnings("unused")
    public static HistoryFragment newInstance(int columnCount) {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        final FragmentActivity activity = getActivity();

        if (activity == null) return view;

        final AppDatabase database = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "db").build();

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            final HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(new ArrayList<ContentRead>());
            recyclerView.setAdapter(adapter);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    adapter.addAll(database.contentReadDao().getAll());
                }
            });
        }

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //mListener = null;
    }

    /*public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ContentRead item);
    }*/
}
