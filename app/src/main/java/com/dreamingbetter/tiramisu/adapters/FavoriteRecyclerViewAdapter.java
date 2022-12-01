package com.dreamingbetter.tiramisu.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FragmentUtils;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.ui.favorite.FavoriteFragment;
import com.dreamingbetter.tiramisu.ui.share.ShareFragment;
import com.orhanobut.hawk.Hawk;

import java.util.List;

public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.ViewHolder> {
    private final List<Content> mValues;
    private final FavoriteFragment mFragment;

    public FavoriteRecyclerViewAdapter(List<Content> items, FavoriteFragment fragment) {
        mValues = items;
        mFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_favorite, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mContentView.setText(String.format("%s%s\"", '"', holder.mItem.text));
        holder.mAuthorView.setText(String.format("%s ", holder.mItem.author));

        holder.mFavoriteButtonView.setOnClickListener(v -> new AlertDialog.Builder(holder.mView.getContext())
                .setMessage(R.string.alert_remove_favorite)
                .setCancelable(true)
                .setNegativeButton("No", null)
                .setPositiveButton(R.string.yes, (paramDialogInterface, paramInt) -> {
                    AppDatabase database = Room.databaseBuilder(holder.mView.getContext(), AppDatabase.class, "db").allowMainThreadQueries().build();
                    database.contentFavoriteDao().delete(holder.mItem.uid);

                    Toast.makeText(holder.mView.getContext(), R.string.removed_from_favorite, Toast.LENGTH_SHORT).show();

                    holder.mView.setVisibility(View.GONE);

                    List<Content> dataset = database.contentDao().getAllFavorites();

                    if (dataset.isEmpty()) {
                        mFragment.goToHome();
                    }
                }).show());

        holder.mShareButtonView.setOnClickListener(v -> {
            Hawk.put("lastSharedContent", holder.mItem);
            FragmentUtils.replace(mFragment, new ShareFragment());
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        final TextView mAuthorView;
        final ImageView mFavoriteButtonView;
        final ImageView mShareButtonView;
        Content mItem;

        ViewHolder(View view) {
            super(view);

            mView = view;
            mContentView = view.findViewById(R.id.item_content);
            mAuthorView = view.findViewById(R.id.item_author);
            mFavoriteButtonView = view.findViewById(R.id.item_favorite_button);
            mShareButtonView = view.findViewById(R.id.item_share_button);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}