package com.example.pablo.popularmovies.Utils;

import android.content.Context;

import com.example.pablo.popularmovies.AppExecutors;
import com.example.pablo.popularmovies.data.MovieRepository;
import com.example.pablo.popularmovies.data.network.MoviesNetworkDataSource;
import com.example.pablo.popularmovies.data.room.MoviesDatabase;
import com.example.pablo.popularmovies.data.room.entity.Movie;
import com.example.pablo.popularmovies.ui.detail.DetailActivityViewModelFactory;
import com.example.pablo.popularmovies.ui.master.MasterActivityViewModelFactory;

public class InjectorUtils {


    public static MovieRepository provideMovieRepository(Context context){
        MoviesDatabase database = MoviesDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        MoviesNetworkDataSource networkDataSource = MoviesNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return MovieRepository.getInstance(database.movieDao(), networkDataSource, executors);
    }

    public static MoviesNetworkDataSource provideNetworkDataSource(Context context){
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideMovieRepository(context);
        AppExecutors executors = AppExecutors.getInstance();
        return MoviesNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

    public static MasterActivityViewModelFactory provideMasterActivityViewModelFactory(Context context){
        MovieRepository movieRepository = provideMovieRepository(context.getApplicationContext());
        return new MasterActivityViewModelFactory(movieRepository);
    }

    public static DetailActivityViewModelFactory provideDetailActivityViewModelFactory(Context context, long idMovie){
        MovieRepository movieRepository = provideMovieRepository(context.getApplicationContext());
        return new DetailActivityViewModelFactory(movieRepository, idMovie);
    }

}
