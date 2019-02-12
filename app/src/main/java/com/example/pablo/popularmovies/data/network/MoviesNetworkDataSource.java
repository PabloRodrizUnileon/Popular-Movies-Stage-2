package com.example.pablo.popularmovies.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.pablo.popularmovies.AppExecutors;
import com.example.pablo.popularmovies.Utils.NetworkUtils;
import com.example.pablo.popularmovies.data.api.Api;
import com.example.pablo.popularmovies.data.api.Service;
import com.example.pablo.popularmovies.data.models.Trailer;
import com.example.pablo.popularmovies.data.models.TrailersData;
import com.example.pablo.popularmovies.data.room.entity.Movie;
import com.example.pablo.popularmovies.data.room.entity.MoviePageResult;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesNetworkDataSource {

    private static final String TAG = MoviesNetworkDataSource.class.getSimpleName();


    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 24;
    private static final String MOVIES_JOB_SERVICE_SYNC_TAG = "MOVIES_JOB_SERVICE_SYNC_TAG";

    private static final Object LOCK = new Object();
    private static MoviesNetworkDataSource sInstance;
    private final Context mContext;

    private final MutableLiveData<List<Movie>> mDownloadedMovies;
    private final AppExecutors mExecutors;

    private MoviesNetworkDataSource(Context mContext, AppExecutors mExecutors) {
        this.mContext = mContext;
        this.mExecutors = mExecutors;
        mDownloadedMovies = new MutableLiveData<List<Movie>>();
    }


    public static MoviesNetworkDataSource getInstance(Context context, AppExecutors appExecutors) {

        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MoviesNetworkDataSource(context.getApplicationContext(), appExecutors);
                Log.d(TAG, "getInstance: New instance created");
            }
        }
        return sInstance;
    }


    public LiveData<List<Movie>> getCurrentMovies() {
        return mDownloadedMovies;
    }

    /**
     * Starts an Intent Service to fetch the movie data right away
     */
    public void startFetchMoviesIntentService() {
        Intent intentService = new Intent(mContext, MoviesSyncIntentService.class);
        mContext.startService(intentService);
    }


    /**
     * Schedules a repeating job service which fetches the weather.
     */
    public void scheduleRecurringFetchWeatherSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync the movies
        Job syncMoviesJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync the movies */
                .setService(MoviesFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(MOVIES_JOB_SERVICE_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want the movies data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the movies data to be synced every 24 - 25 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncMoviesJob);
        Log.d(TAG, "Job scheduled");
    }

    /**
     * Gets the latests movies
     */
    void fetchMovies() {
        Log.d(TAG, "Fetch latest movies started");
        mExecutors.networkIO().execute(() -> {

            Service service = Api.getService().create(Service.class);
            try {
                Call<MoviePageResult> moviePageResultCallPopularMovies = service.getPopularMovies(1, NetworkUtils.MY_API_KEY);
                Response<MoviePageResult> response_1 = moviePageResultCallPopularMovies.execute();
                Log.e(TAG, "fetchMovies: RESPONSE 1 --> Popular Movies");

                Call<MoviePageResult> moviePageResultCallTopRatedMovies = service.getTopRatedMovies(1, NetworkUtils.MY_API_KEY);
                Response<MoviePageResult> response_2 = moviePageResultCallTopRatedMovies.execute();
                Log.e(TAG, "fetchMovies: RESPONSE 2 --> Top Rated Movies");


                List<Movie> _movieList_1 = response_1.body().getMovieResult();
                List<Movie> _movieList_2 = response_2.body().getMovieResult();

                List<Movie> _movieList = new ArrayList<>(_movieList_1);
                _movieList.addAll(_movieList_2);


                // When you are off of the main thread and want to update LiveData, use postValue.
                // It posts the update to the main thread.
                Log.e(TAG, "onResponse: " + _movieList.size() + "  --  " + _movieList.size());
                mDownloadedMovies.postValue(_movieList);


            } catch (IOException e) {
                e.printStackTrace();
            }


            // If the code reaches this point, we have successfully performed our sync

        });
    }


//    public LiveData<Trailer> getMovieTrailer(long movieId){
//        mExecutors.networkIO().execute(() -> {
//
//            Service service = Api.getService().create(Service.class);
//            try {
//                Call<TrailersData> trailersDataCall = service.getTrailers(movieId, NetworkUtils.MY_API_KEY);
//
//                Response<TrailersData> response_1 = trailersDataCall.execute();
//
//
//
//                List<Trailer> _movieList_1 = response_1.body().getTrailerList();
//
//
//
//
//                // When you are off of the main thread and want to update LiveData, use postValue.
//                // It posts the update to the main thread.
//                Log.e(TAG, "onResponse: " + _movieList.size() + "  --  " + _movieList.size() );
//                mDownloadedMovies.postValue(_movieList);
//
//
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//            // If the code reaches this point, we have successfully performed our sync
//
//        });
//    }


}
