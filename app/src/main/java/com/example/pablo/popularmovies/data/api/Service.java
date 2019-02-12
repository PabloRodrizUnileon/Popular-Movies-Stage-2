package com.example.pablo.popularmovies.data.api;

import com.example.pablo.popularmovies.data.models.ReviewsData;
import com.example.pablo.popularmovies.data.models.TrailersData;
import com.example.pablo.popularmovies.data.room.entity.MoviePageResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {



    @GET("movie/popular")
    Call<MoviePageResult> getPopularMovies(@Query("page") int page, @Query("api_key") String apiKey);
    @GET("movie/top_rated")
    Call<MoviePageResult> getTopRatedMovies(@Query("page") int page, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailersData> getTrailers(@Path("movie_id") long movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewsData> getReviews(@Path("movie_id") long movieId, @Query("api_key") String apiKey);




}
