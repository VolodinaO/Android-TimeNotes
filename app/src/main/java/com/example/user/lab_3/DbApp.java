package com.example.user.lab_3;

import android.app.Application;

public class DbApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DbHelper.init(getApplicationContext());
    }

}
