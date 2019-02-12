package com.example.pablo.popularmovies.ui.master;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.pablo.popularmovies.data.MovieRepository;

public class MasterActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final MovieRepository movieRepository;

    public MasterActivityViewModelFactory(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    @Override
    public <T extends ViewModel> T create( Class<T> modelClass) {
        return (T) new MasterActivityViewModel(movieRepository);
    }
}
