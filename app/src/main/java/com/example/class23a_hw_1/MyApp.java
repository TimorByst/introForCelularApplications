package com.example.class23a_hw_1;

import android.app.Application;

import androidx.appcompat.widget.AppCompatImageView;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MySignal.init(this);
    }
}