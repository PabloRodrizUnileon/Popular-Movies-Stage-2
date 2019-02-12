package com.example.pablo.popularmovies.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.pablo.popularmovies.data.MovieRepository;

public class DetailActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieRepository movieRepository;
    private final long movieId;

    public DetailActivityViewModelFactory(MovieRepository movieRepository, long idMovie) {
        this.movieRepository = movieRepository;
        this.movieId = idMovie;
    }


    @Override
    public <T extends ViewModel> T create( Class<T> modelClass) {
        return (T) new DetailActivityViewModel(movieRepository, movieId);
    }
}
