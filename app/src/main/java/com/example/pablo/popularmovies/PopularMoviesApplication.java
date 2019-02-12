package com.example.pablo.popularmovies;

import android.app.Application;

import com.example.pablo.popularmovies.SharedPreferences.MySharedPreferences;
import com.facebook.stetho.Stetho;

public class PopularMoviesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MySharedPreferences.init(this.getApplicationContext());
        Stetho.initializeWithDefaults(this);

    }
}
