package com.dreamingbetter.tiramisu.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.GsonUtils;
import com.dreamingbetter.tiramisu.MainActivity;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.dreamingbetter.tiramisu.utils.UpdateQuoteEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateQuoteEvent event) {
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

        final Content content = GsonUtils.fromJson(Helper.getValue(activity.getApplicationContext(), "content", null), Content.class);

        if (content == null) return;

        TextView author = view.findViewById(R.id.content_author);
        author.setText(content.author);

        TextView text = view.findViewById(R.id.content_text);
        text.setText(String.format("%s%s\"", '"', content.text));

        Button amazonButton = view.findViewById(R.id.amazonButton);
        amazonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = content.author.replace(" ", "+");
                String lang = "it";

                if (Helper.getValue(activity.getApplicationContext(), "lang", "it") == "en") {
                    lang = "com";
                }

                String url = "https://www.amazon." + lang + "/s?k=" + query;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button wikipediaButton = view.findViewById(R.id.wikipediaButton);
        wikipediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = content.author.replace(" ", "_");
                String lang = "it";

                if (Helper.getValue(activity.getApplicationContext(), "lang", "it") == "en") {
                    lang = "en";
                }

                String url = "https://" + lang + ".wikipedia.org/wiki/" + query;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        LinearLayout buttons = view.findViewById(R.id.buttons);
        buttons.setVisibility(View.VISIBLE);
    }
}
