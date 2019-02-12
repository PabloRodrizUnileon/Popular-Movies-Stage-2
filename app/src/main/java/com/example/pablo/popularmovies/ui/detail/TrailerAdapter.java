package com.example.pablo.popularmovies.ui.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pablo.popularmovies.R;
import com.example.pablo.popularmovies.data.models.Trailer;
import com.example.pablo.popularmovies.ui.master.MovieAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Context mContext;
    private List<Trailer> trailerList;

    private final TrailerAdapterOnClickListener mClickHandler;

    public interface TrailerAdapterOnClickListener {
        void onTrailerClick(Trailer trailer);
    }

    public TrailerAdapter(Context mContext, List<Trailer> trailer, TrailerAdapterOnClickListener mClickHandler) {
        this.mContext = mContext;
        this.trailerList = trailer;
        this.mClickHandler = mClickHandler;
    }

    public void setData(List<Trailer> trailerList){
        if(this.trailerList != null){
            this.trailerList.clear();
            this.trailerList.addAll(trailerList);
            notifyDataSetChanged();
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_item, viewGroup, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);
        ImageView thumbnail = holder.trailerThumbnail;
        Drawable errorPlaceHolder = mContext.getResources().getDrawable(R.drawable.placeholder_error);
        Picasso.with(mContext).load(trailer.getThumbnailUrl()).error(errorPlaceHolder).into(thumbnail);
        holder.tvTrailerTitle.setText(trailer.getName());

    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView trailerThumbnail;
        private TextView tvTrailerTitle;

        public TrailerViewHolder(@NonNull View view) {
            super(view);
            trailerThumbnail = view.findViewById(R.id.imgTrailerThumbnail);
            tvTrailerTitle = view.findViewById(R.id.tvTrailerTitle);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Trailer trailer = trailerList.get(getAdapterPosition());
            mClickHandler.onTrailerClick(trailer);
        }
    }


}
