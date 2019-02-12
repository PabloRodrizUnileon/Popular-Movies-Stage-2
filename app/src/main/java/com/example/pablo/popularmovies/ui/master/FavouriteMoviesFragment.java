package com.example.pablo.popularmovies.ui.master;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pablo.popularmovies.R;
import com.example.pablo.popularmovies.Utils.InjectorUtils;
import com.example.pablo.popularmovies.data.room.entity.Movie;
import com.example.pablo.popularmovies.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo on 19/06/2018.
 */

public class FavouriteMoviesFragment extends Fragment implements MovieAdapter.MovieAdapterOnclickHandler {
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;

    private MasterActivityViewModel masterActivityViewModel;
    private List<Movie> movieList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_movies, container, false);
        movieAdapter = new MovieAdapter(getActivity(), movieList, this);
        mRecyclerView = rootView.findViewById(R.id.list_movies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(movieAdapter);
        observeData();
        return rootView;
    }

    @Override
    public void onClick(Movie movie) {
        Intent intentDetailActivity = new Intent(getActivity(), DetailActivity.class);
        intentDetailActivity.putExtra(DetailActivity.DETAIL_INTENT_KEY, movie.getId());
        startActivity(intentDetailActivity);
    }

    private void observeData() {
        MasterActivityViewModelFactory factory = InjectorUtils.provideMasterActivityViewModelFactory(getActivity());
        masterActivityViewModel = ViewModelProviders.of(getActivity(), factory).get(MasterActivityViewModel.class);
        masterActivityViewModel.getFavouriteMovies().observe(this, movies -> {

            if (movies != null ) {
                movieAdapter.setData(movies);
                movieAdapter.notifyDataSetChanged();
            }

        });

    }
}
