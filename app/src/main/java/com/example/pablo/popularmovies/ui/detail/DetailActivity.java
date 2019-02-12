package com.example.pablo.popularmovies.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pablo.popularmovies.R;
import com.example.pablo.popularmovies.Utils.InjectorUtils;
import com.example.pablo.popularmovies.Utils.NetworkUtils;
import com.example.pablo.popularmovies.data.api.Api;
import com.example.pablo.popularmovies.data.api.Service;
import com.example.pablo.popularmovies.data.models.Review;
import com.example.pablo.popularmovies.data.models.ReviewsData;
import com.example.pablo.popularmovies.data.models.Trailer;
import com.example.pablo.popularmovies.data.models.TrailersData;
import com.example.pablo.popularmovies.data.room.entity.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickListener{

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String DETAIL_INTENT_KEY = "detail_intent_key";


    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolBarLayout;
    @BindView(R.id.text_view_title)
    TextView mTitle;
    @BindView(R.id.text_view_detail_synopsis)
    TextView mSynopsis;
    @BindView(R.id.text_view_detail_release_date)
    TextView mReleaseDate;
    @BindView(R.id.ratingBar)
    RatingBar mUserRating;
    @BindView(R.id.header)
    ImageView mHeader;
    @BindView(R.id.fab_set_favourite)
    ImageButton fabSetFavouriteMovie;
    //  Trailers
    @BindView(R.id.tv_trailer_header)
    TextView trailerHeaderSeparator;
    @BindView(R.id.rvTrailers)
    RecyclerView rvTrailers;
    //  Reviews
    @BindView(R.id.tv_reviews_header)
    TextView reviewHeaderSeparator;
    @BindView(R.id.rvReviews)
    RecyclerView rvReviews;

    //  Trailer adapter & list
    private TrailerAdapter trailerAdapter;
    private List<Trailer> trailerList = new ArrayList<>();

    //  Review adapter & list
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    // Movie's ViewModel
    private DetailActivityViewModel detailActivityViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        //  Set some View data
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolBarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        //  Set Trailer adapter
        trailerAdapter = new TrailerAdapter(this, trailerList, DetailActivity.this);
        rvTrailers.setLayoutManager(new LinearLayoutManager(this));
        rvTrailers.setAdapter(trailerAdapter);

        //  Set Reviews adapter
        reviewAdapter = new ReviewAdapter(this, reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);

        //  Get intent
        Intent intentFromMasterActivity = getIntent();
        if (intentFromMasterActivity != null) {
            long movieId = intentFromMasterActivity.getIntExtra(DETAIL_INTENT_KEY, -1);
            if (movieId != -1) {
                //  Obtain ViewModel Factory of Movies
                DetailActivityViewModelFactory factory = InjectorUtils.provideDetailActivityViewModelFactory(this, movieId);
                detailActivityViewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel.class);
                //  Observe the movie
                detailActivityViewModel.getSelectedMovie().observe(this, movie -> {
                    if(movie!= null) {
                        //  Binds data to the User Interface
                        bindDataToUI(movie);
                        //  Sets the listener of the favourite button
                        setListenerMovieFavourite(movie);
                        //  Loads trailers.
                        loadTrailers(movieId);
                        //  load reviews
                        loadReviews(movieId);
                    }
                });


            }
        }
    }

    /**
     * Binds the data to the User Interface.
     *
     * @param movie {@link Movie} used to show the data in the screen.
     */
    private void bindDataToUI(Movie movie) {
        collapsingToolBarLayout.setTitle(movie.getTitle());
        if (movie.isFavourite()) {
            fabSetFavouriteMovie.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_black_24dp));
            fabSetFavouriteMovie.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen), PorterDuff.Mode.SRC_IN);
        } else {
            fabSetFavouriteMovie.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black_24dp));
            fabSetFavouriteMovie.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }
        mTitle.setText(movie.getTitle());
        mSynopsis.setText(movie.getOverview());
        mReleaseDate.setText(movie.getReleaseDate());
        mUserRating.setRating((float) movie.getVoteAverage() / 2);
        Picasso.with(this)
                .load(Movie.POSTER_PATH_high_res + movie.getPosterPath())
                .into(mHeader, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bm = ((BitmapDrawable) mHeader.getDrawable()).getBitmap();
                        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorWhite);
                        int colorSUper = new Palette.Builder(bm)
                                .generate().getDominantColor(color);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(colorSUper);
                            collapsingToolBarLayout.setContentScrimColor(colorSUper);
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(DetailActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Sets the listener of the favourite button.
     * If the button favourite is clicked, it updates the movie by selecting it as favourite or removing it from them.
     *
     * @param movie {@link Movie} to update.
     */
    private void setListenerMovieFavourite(Movie movie) {
        fabSetFavouriteMovie.setOnClickListener(view ->
        {
            if (movie != null) {
                if (movie.isFavourite()) {
                    movie.setFavourite(false);
                } else {
                    movie.setFavourite(true);
                }
                detailActivityViewModel.updateMovie(movie);
            }
        });
    }

    /**
     * Calls the API to load the trailers, based on the movie ID.
     *
     * @param movieId   used to find the trailers.
     */
    private void loadTrailers(long movieId) {
        Api.getService().create(Service.class).getTrailers(movieId, NetworkUtils.MY_API_KEY).enqueue(new retrofit2.Callback<TrailersData>() {
            @Override
            public void onResponse(Call<TrailersData> call, Response<TrailersData> response) {
                List<Trailer> trailerList = response.body().getTrailerList();
                if(trailerList != null){


                    int numberOfTrailers = trailerList.size();
                    String trailerHeaderSeparatorText = getResources().getQuantityString(R.plurals.numberOfTrailers, numberOfTrailers, numberOfTrailers );
                    trailerHeaderSeparator.setText(trailerHeaderSeparatorText);
                    trailerAdapter.setData(trailerList);
                }

            }

            @Override
            public void onFailure(Call<TrailersData> call, Throwable t) {

            }
        });
    }

    /**
     * Calls the API to load the rebiews, based on the movie ID.
     *
     * @param movieId   used to find the reviews.
     */
    private void loadReviews(long movieId) {
        Api.getService().create(Service.class).getReviews(movieId, NetworkUtils.MY_API_KEY).enqueue(new retrofit2.Callback<ReviewsData>() {
            @Override
            public void onResponse(Call<ReviewsData> call, Response<ReviewsData> response) {
                List<Review> reviewList = response.body().getReviewList();
                if(reviewList != null){

                    int numberOfReviews = reviewList.size();
                    String reviewHeaderSeparatorText = getResources().getQuantityString(R.plurals.numberOfReviews, numberOfReviews, numberOfReviews );
                    reviewHeaderSeparator.setText(reviewHeaderSeparatorText);
                    reviewAdapter.setData(reviewList);
                }
            }

            @Override
            public void onFailure(Call<ReviewsData> call, Throwable t) {

            }
        });
    }

    /**
     * Listener to the click event of a trailer.
     *
     * @param trailer   {@link Trailer} clicked by the user.
     */
    @Override
    public void onTrailerClick(Trailer trailer) {
        Intent startYoutubeApp = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + trailer.getKey()));
        try {
            startActivity(startYoutubeApp);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading trailer", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
