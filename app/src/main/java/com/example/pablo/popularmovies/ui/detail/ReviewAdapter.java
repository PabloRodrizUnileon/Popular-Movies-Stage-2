package com.example.pablo.popularmovies.ui.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pablo.popularmovies.R;
import com.example.pablo.popularmovies.data.models.Review;

import org.w3c.dom.Text;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private Context mContext;
    private List<Review> reviewList;

    public ReviewAdapter(Context mContext, List<Review> reviewList) {
        this.mContext = mContext;
        this.reviewList = reviewList;
    }


    public void setData(List<Review> reviewList){
        if(this.reviewList != null){
            this.reviewList.clear();
            this.reviewList.addAll(reviewList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, viewGroup, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.reviewAuthor.setText(review.getAuthor());
        holder.reviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewAuthor;
        private TextView reviewContent;


        public ReviewViewHolder(@NonNull View view) {
            super(view);
            reviewAuthor = view.findViewById(R.id.tv_review_author);
            reviewContent = view.findViewById(R.id.tv_review_content);
        }
    }
}
