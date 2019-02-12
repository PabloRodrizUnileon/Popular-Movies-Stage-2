package com.example.pablo.popularmovies.data;


import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.pablo.popularmovies.AppExecutors;
import com.example.pablo.popularmovies.SharedPreferences.MySharedPreferences;
import com.example.pablo.popularmovies.Utils.DateUtils;
import com.example.pablo.popularmovies.data.models.Trailer;
import com.example.pablo.popularmovies.data.network.MoviesNetworkDataSource;
import com.example.pablo.popularmovies.data.room.dao.MovieDao;
import com.example.pablo.popularmovies.data.room.entity.Movie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();


    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private final MovieDao mMovieDao;
    private final MoviesNetworkDataSource mMoviesNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private MovieRepository(MovieDao weatherDao,
                            MoviesNetworkDataSource weatherNetworkDataSource,
                            AppExecutors executors) {
        mMovieDao = weatherDao;
        mMoviesNetworkDataSource = weatherNetworkDataSource;
        mExecutors = executors;

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        LiveData<List<Movie>> networkData = mMoviesNetworkDataSource.getCurrentMovies();
        networkData.observeForever(newMovieDataFromNetwork -> {
            mExecutors.diskIO().execute(() -> {

                // Insert or update new weather data into Sunshine's database
                mMovieDao.upsert(newMovieDataFromNetwork);
                Log.d(TAG, "New values inserted");
            });
        });
    }

    public synchronized static MovieRepository getInstance(
            MovieDao weatherDao, MoviesNetworkDataSource weatherNetworkDataSource,
            AppExecutors executors) {
        Log.d(TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieRepository(weatherDao, weatherNetworkDataSource,
                        executors);
                Log.d(TAG, "Made new repository");
            }
        }
        return sInstance;
    }


    public LiveData<List<Movie>> getPopularMovies(){
        initializeData();
        return mMovieDao.getPopularMovies();
    }

    public LiveData<List<Movie>> getTopRatedMovies(){
        initializeData();
        return mMovieDao.getTopRated();
    }

    public LiveData<List<Movie>> getFavouriteMovies(){
        initializeData();
        return mMovieDao.getFavouriteMovies();
    }

    public LiveData<Movie> getMovieById(long id){
        return mMovieDao.getMovieById(id);
    }

    public void updateMovie(Movie movie){
        mExecutors.diskIO().execute(() -> {
            mMovieDao.update(movie);
            Log.e(TAG, "updateMovie: Movie: " + movie.toString() + " updated" );
        });
    }




    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    private synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;

        // This method call triggers the scheduling of periodic synchronizations of Movie data
        mMoviesNetworkDataSource.scheduleRecurringFetchWeatherSync();

        mExecutors.diskIO().execute(() -> {
            boolean isFetchNeeded = isFetchNeeded();
            Log.e(TAG, "initializeData: isFetchNeeded " + isFetchNeeded );
            if (isFetchNeeded) {
                startFetchMoviesService();
            }
        });
    }


    /**
     * Checks if the data in database is older than 24 hours.
     *
     * @return  true if fetch is needed.
     *          false otherwise.
     */
    private boolean isFetchNeeded(){
        long lastTimeDataBaseWasUpdated = MySharedPreferences.getInstance().getLong(MySharedPreferences.Key.LAST_TIME_DATABASE_UPDATE_LONG, 0);
        if(lastTimeDataBaseWasUpdated == 0){
            return true;
        }
        Date dateLastUpdate = new Date(lastTimeDataBaseWasUpdated);
        Log.d(TAG, "isFetchNeeded: Las time of Data update was [" + dateLastUpdate.toString() + "].");
        return DateUtils.isDateOlderThan24Hours(dateLastUpdate);
    }

    /**
     * Network related operation
     */

    private void startFetchMoviesService() {
        mMoviesNetworkDataSource.startFetchMoviesIntentService();
    }
}
