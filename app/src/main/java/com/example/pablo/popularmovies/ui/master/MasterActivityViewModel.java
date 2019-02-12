package com.example.pablo.popularmovies.ui.master;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.pablo.popularmovies.data.MovieRepository;
import com.example.pablo.popularmovies.data.room.entity.Movie;

import java.util.List;

public class MasterActivityViewModel extends ViewModel {

    private final MovieRepository mRepository;
    private final LiveData<List<Movie>> popularMovies;
    private final LiveData<List<Movie>> topRatedMovies;
    private final LiveData<List<Movie>> favouriteMovies;

    public MasterActivityViewModel(MovieRepository movieRepository) {
        this.mRepository = movieRepository;
        popularMovies = mRepository.getPopularMovies();
        topRatedMovies = mRepository.getTopRatedMovies();
        favouriteMovies = mRepository.getFavouriteMovies();
    }

    public LiveData<List<Movie>> getPopularMovies() {
        return popularMovies;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        return topRatedMovies;
    }

    public LiveData<List<Movie>> getFavouriteMovies(){
        return favouriteMovies;
    }


}