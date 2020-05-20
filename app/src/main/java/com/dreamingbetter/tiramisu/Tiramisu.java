package com.dreamingbetter.tiramisu;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class Tiramisu extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Logger.addLogAdapter(new AndroidLogAdapter());
            Hawk.init(getApplicationContext()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}