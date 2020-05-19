package com.dreamingbetter.tiramisu.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.entities.ContentRead;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {
    private final List<ContentRead> mValues;

    public HistoryRecyclerViewAdapter(List<ContentRead> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        Date date = new Date(holder.mItem.timestamp);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
;
        holder.mDateView.setText(format.format(date));
        holder.mContentView.setText(holder.mItem.text);
        holder.mAuthorView.setText(holder.mItem.author);

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ContentRead> newList) {
        mValues.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDateView;
        public final TextView mContentView;
        public final TextView mAuthorView;
        public ContentRead mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mDateView = view.findViewById(R.id.item_date);
            mContentView = view.findViewById(R.id.item_content);
            mAuthorView = view.findViewById(R.id.item_author);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}