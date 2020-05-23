package com.dreamingbetter.tiramisu.ui.settings;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.LanguageUtils;
import com.dreamingbetter.tiramisu.MainActivity;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.orhanobut.hawk.Hawk;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        final FragmentActivity activity = getActivity();

        if (activity != null) {
            Switch notifications = root.findViewById(R.id.switch_notifications);

            notifications.setChecked(Hawk.get("notifications", true));

            notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Hawk.put("notifications", isChecked);
                }
            });

            Spinner languages = root.findViewById(R.id.spinnerLanguage);
            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.languages, R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            languages.setAdapter(adapter);

            final int lang = Hawk.get("lang", 0);
            languages.setSelection(lang);

            languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != lang) {
                        Hawk.put("lang", i);
                        Hawk.put("timestamp", 0);

                        if (i == 0) {
                            LanguageUtils.applyLanguage(Locale.ITALY, MainActivity.class);
                        }

                        if (i == 1) {
                            LanguageUtils.applyLanguage(Locale.US, MainActivity.class);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });
        }

        return root;
    }
}
