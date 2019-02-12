package com.example.pablo.popularmovies.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.pablo.popularmovies.Utils.InjectorUtils;

public class MoviesSyncIntentService extends IntentService {

    private static final String TAG = MoviesSyncIntentService.class.getSimpleName();
    private static final String NAME = "MoviesSyncIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public MoviesSyncIntentService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, " Intent Service started");
        MoviesNetworkDataSource moviesNetworkDataSource = InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        moviesNetworkDataSource.fetchMovies();
    }
}
