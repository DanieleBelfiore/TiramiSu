package com.dreamingbetter.tiramisu.ui.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import com.dreamingbetter.tiramisu.R;
import com.dreamingbetter.tiramisu.database.AppDatabase;
import com.dreamingbetter.tiramisu.entities.Content;
import com.dreamingbetter.tiramisu.utils.Helper;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class SettingsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        final FragmentActivity activity = getActivity();

        if (activity != null) {
            final Switch notifications = root.findViewById(R.id.switch_notifications);
            final Button notificationTimeButton = root.findViewById(R.id.new_quote_time_button);
            final TextView readQuotes = root.findViewById(R.id.read_quotes);

            notifications.setChecked(Hawk.get("notifications", true));
            notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Hawk.put("notifications", isChecked);
                }
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

            readQuotes.setText(String.format(Locale.getDefault(),  "%s: %d%%", getString(R.string.read_quotes), readPercentage(activity.getApplicationContext())));
        }

        return root;
    }

    private void updateNotificationTimeBtn(Button btn) {
        String hour = String.format(Locale.getDefault(), "%02d", Hawk.get("notificationHour", 8));
        String minute = String.format(Locale.getDefault(), "%02d", Hawk.get("notificationMinute", 0));

        btn.setText(String.format("%s %s:%s", getString(R.string.fa_clock), hour, minute));
    }

    private int readPercentage(Context context) {
        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "db").allowMainThreadQueries().build();
        List<Content> contents = database.contentDao().getAll();
        List<Content> contentsNotRead = database.contentDao().getAllNotRead();

        int result = (contents.size() - contentsNotRead.size()) * 100 / contents.size();

        return result == 0 ? 1 : result;
    }
}
