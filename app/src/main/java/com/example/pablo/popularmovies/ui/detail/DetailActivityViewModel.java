package com.example.pablo.popularmovies.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.pablo.popularmovies.data.MovieRepository;
import com.example.pablo.popularmovies.data.models.Trailer;
import com.example.pablo.popularmovies.data.room.entity.Movie;

public class DetailActivityViewModel extends ViewModel {
    private final MovieRepository mRepository;
    private final long movieId;
    private final LiveData<Movie> selectedMovie;

    public DetailActivityViewModel(MovieRepository movieRepository, long idMovie) {
        this.mRepository = movieRepository;
        this.movieId = idMovie;
        this.selectedMovie = mRepository.getMovieById(movieId);
    }

    public LiveData<Movie> getSelectedMovie() {
        return selectedMovie;
    }

    public void updateMovie(Movie movie){
        mRepository.updateMovie(movie);
    }
}
