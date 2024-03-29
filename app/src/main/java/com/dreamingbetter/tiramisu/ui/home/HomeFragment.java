package com.dreamingbetter.tiramisu.ui.home;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import com.blankj.utilcode.util.FragmentUtils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.entities.ContentFavorite;
import com.dreamingbetter.tiramisu.ui.share.ShareFragment;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.dreamingbetter.tiramisu.utils.UpdateQuoteEvent;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class HomeFragment extends Fragment {
    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@SuppressWarnings("unused") UpdateQuoteEvent event) {
        updateUI(getView());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        updateUI(root);

        return root;
    }

    private void updateUI(View view) {
        final FragmentActivity activity = getActivity();

        if (activity == null || view == null) return;

        final Content content = Hawk.get("content", null);

        if (content == null) return;

        RoundedImageView monthImage = view.findViewById(R.id.content_image);

        // Set the month image
        String month = String.format(Locale.getDefault(), "month_%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
        monthImage.setImageResource(Helper.getResId(activity.getApplicationContext(), "drawable", month));

        final AppDatabase database = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "db").allowMainThreadQueries().build();

        TextView text = view.findViewById(R.id.content_text);
        text.setText(String.format("%s%s\"", '"', content.text));
        YoYo.with(Techniques.Landing).duration(1500).playOn(text);

        TextView author = view.findViewById(R.id.content_author);
        author.setText(String.format("%s ", content.author));
        YoYo.with(Techniques.Landing).duration(1500).playOn(author);

        final Button favoriteButton = view.findViewById(R.id.favorite_button);

        favoriteButton.setOnClickListener(v -> {
            YoYo.with(Techniques.Pulse).duration(500).playOn(favoriteButton);

            if (database.contentFavoriteDao().isFavorite(content.uid) == 1) {
                database.contentFavoriteDao().delete(content.uid);

                Typeface typeface = ResourcesCompat.getFont(activity.getApplicationContext(), R.font.fa_regular);
                favoriteButton.setTypeface(typeface);

                Toast.makeText(activity.getApplicationContext(), R.string.removed_from_favorite, Toast.LENGTH_SHORT).show();
            } else {
                ContentFavorite c = new ContentFavorite();

                c.uid = content.uid;
                c.timestamp = System.currentTimeMillis();

                database.contentFavoriteDao().insert(c);

                Typeface typeface = ResourcesCompat.getFont(activity.getApplicationContext(), R.font.fa_solid);
                favoriteButton.setTypeface(typeface);

                Toast.makeText(activity.getApplicationContext(), R.string.added_to_favorite, Toast.LENGTH_SHORT).show();
            }
        });

        if (database.contentFavoriteDao().isFavorite(content.uid) == 1) {
            Typeface typeface = ResourcesCompat.getFont(activity.getApplicationContext(), R.font.fa_solid);
            favoriteButton.setTypeface(typeface);
        }

        Button amazonButton = view.findViewById(R.id.amazon_button);
        amazonButton.setOnClickListener(v -> {
            String query = content.author.replace(" ", "+");
            String lang = "it";

            if (Hawk.get("lang", 0) == 1) {
                lang = "com";
            }

            String url = "https://www.amazon." + lang + "/s?k=" + query;

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        Button wikipediaButton = view.findViewById(R.id.wikipedia_button);
        wikipediaButton.setOnClickListener(v -> {
            String query = content.author.replace(" ", "_");
            String lang = "it";

            if (Hawk.get("lang", 0) == 1) {
                lang = "en";
            }

            String url = "https://" + lang + ".wikipedia.org/wiki/" + query;

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        LinearLayout buttons = view.findViewById(R.id.buttons);
        buttons.setVisibility(View.VISIBLE);

        final HomeFragment homeFragment = this;
        ImageView shareButton = view.findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> {
            Hawk.put("lastSharedContent", content);
            FragmentUtils.replace(homeFragment, new ShareFragment());
        });
    }
}
