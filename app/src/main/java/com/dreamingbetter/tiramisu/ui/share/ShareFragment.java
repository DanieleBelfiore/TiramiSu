package com.dreamingbetter.tiramisu.ui.share;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.ui.home.HomeFragment;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("WeakerAccess")
public class ShareFragment extends Fragment {
    FragmentActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_share, container, false);

        updateUI(root);

        return root;
    }

    private void updateUI(final View view) {
        activity = getActivity();

        if (activity == null || view == null) return;

        final Content content = Hawk.get("lastSharedContent", null);

        if (content == null) return;

        RoundedImageView monthImage = view.findViewById(R.id.content_image);

        // Set the month image
        String month = String.format(Locale.getDefault(), "month_%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
        monthImage.setImageResource(Helper.getResId(activity.getApplicationContext(), "drawable", month));

        TextView text = view.findViewById(R.id.content_text);
        text.setText(String.format("%s%s\"", '"', content.text));

        TextView author = view.findViewById(R.id.content_author);
        author.setText(String.format("%s ", content.author));

        final LinearLayout sharingContentView = view.findViewById(R.id.sharing_content);

        AtomicInteger count = new AtomicInteger();

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            count.getAndIncrement();

            Bitmap bm = ConvertUtils.view2Bitmap(sharingContentView);
            Uri uri = Helper.shareUriBitmap(activity.getApplicationContext(), bm);

            // Run the intent only the first time
            if (count.get() == 1) {
                startActivity(IntentUtils.getShareImageIntent(uri));
            } else {
                final ShareFragment shareFragment = this;
                FragmentUtils.replace(shareFragment, new HomeFragment());
            }
        });
    }
}
