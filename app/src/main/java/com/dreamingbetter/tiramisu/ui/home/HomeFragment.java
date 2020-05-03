package com.dreamingbetter.tiramisu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.GsonUtils;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.utils.Helper;

public class HomeFragment extends Fragment {
    //private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /*homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        updateTextUI(root);

        return root;
    }

    protected void updateTextUI(View root) {
        Content content = GsonUtils.fromJson(Helper.getValue(getActivity().getApplicationContext(), "content", null), Content.class);

        TextView text = root.findViewById(R.id.content_text);
        TextView author = root.findViewById(R.id.content_author);

        if (content == null) {
            text.setText("Sto pensando a qualcosa di speciale per te!");
            author.setText("");
        } else {
            text.setText(String.format("%s%s\"", '"', content.text));
            author.setText(content.author);
        }
    }
}
