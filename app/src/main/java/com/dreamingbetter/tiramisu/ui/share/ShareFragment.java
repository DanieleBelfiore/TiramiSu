package com.dreamingbetter.tiramisu.ui.share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.dreamingbetter.tiramisu.MainActivity;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class ShareFragment extends Fragment {
    FragmentActivity activity;

    final ActivityResultLauncher<Intent> updateUiResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            ActivityCompat.finishAffinity(activity);

            Intent i = new Intent(getActivity(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    });

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

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Bitmap bm = ConvertUtils.view2Bitmap(sharingContentView);
            Uri uri = Helper.shareUriBitmap(activity.getApplicationContext(), bm);

            Intent i = new Intent();
            i.setDataAndType(uri, "image/*");
            i.setAction(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            updateUiResultLauncher.launch(i);
        });
    }
}
