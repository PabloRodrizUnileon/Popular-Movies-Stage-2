package com.example.pablo.popularmovies.ui.master;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pablo.popularmovies.R;
import com.example.pablo.popularmovies.data.room.entity.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private Context mContext;
    private List<Movie> movieList;

    private final MovieAdapterOnclickHandler mClickHandler;

    public interface MovieAdapterOnclickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(Context context, List<Movie> movieList, MovieAdapterOnclickHandler clickHandler) {
        this.mContext = context;
        this.movieList = movieList;
        this.mClickHandler = clickHandler;
    }

    public void setData(List<Movie> movieList) {
        if (this.movieList != null) {
            this.movieList.clear();
            this.movieList.addAll(movieList);
            notifyDataSetChanged();
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.item_movie;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        ImageView imageView = holder.mImageView;
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.poster);
        Picasso.with(mContext).load(Movie.POSTER_PATH_low_res + movie.getPosterPath()).error(drawable).into(imageView);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }



    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.image_poster_movie);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie movie = movieList.get(position);
            mClickHandler.onClick(movie);
        }
    }


}
