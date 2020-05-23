package com.dreamingbetter.tiramisu.ui.settings;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.LanguageUtils;
import com.dreamingbetter.tiramisu.MainActivity;
import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class SettingsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        final FragmentActivity activity = getActivity();

        if (activity != null) {
            final Switch notifications = root.findViewById(R.id.switch_notifications);
            final Spinner languages = root.findViewById(R.id.spinner_language);
            final Button notificationTimeButton = root.findViewById(R.id.new_quote_time_button);

            notifications.setChecked(Hawk.get("notifications", true));
            notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Hawk.put("notifications", isChecked);
                }
            });

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
                        Hawk.put("timestamp", 0L);

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

            updateNotificationTimeBtn(notificationTimeButton);

            notificationTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar schedule = Calendar.getInstance();
                    schedule.set(Calendar.HOUR_OF_DAY, Hawk.get("notificationHour", 8));
                    schedule.set(Calendar.MINUTE, Hawk.get("notificationMinute", 0));
                    schedule.set(Calendar.SECOND, 0);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Hawk.put("notificationHour", selectedHour);
                            Hawk.put("notificationMinute", selectedMinute);

                            updateNotificationTimeBtn(notificationTimeButton);

                            Helper.removeWorker(activity.getApplicationContext(), "nextQuote");
                            Helper.addWorker(activity.getApplicationContext(), "nextQuote");

                        }
                    }, schedule.get(Calendar.HOUR_OF_DAY), schedule.get(Calendar.MINUTE), true);
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });
        }

        return root;
    }

    private void updateNotificationTimeBtn(Button btn) {
        String hour = String.format(Locale.ITALY, "%02d", Hawk.get("notificationHour", 8));
        String minute = String.format(Locale.ITALY, "%02d", Hawk.get("notificationMinute", 0));

        btn.setText(String.format("%s %s:%s", getString(R.string.fa_clock), hour, minute));
    }
}
