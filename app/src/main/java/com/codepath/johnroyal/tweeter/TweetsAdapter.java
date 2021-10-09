package com.codepath.johnroyal.tweeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.johnroyal.tweeter.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context) {
        this.context = context;
        this.tweets = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for each row
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void setTweets(List<Tweet> newTweets) {
        tweets.clear();
        tweets.addAll(newTweets);
        notifyDataSetChanged();
    }

    public void add(int i, Tweet tweet) {
        tweets.add(i, tweet);
        notifyItemInserted(i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvUserScreenName;
        TextView tvUserVerifiedCheckmark;
        TextView tvTimestamp;
        TextView tvBody;
        ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserScreenName = itemView.findViewById(R.id.tvUserScreenName);
            tvUserVerifiedCheckmark = itemView.findViewById(R.id.tvUserVerifiedCheckmark);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }

        public void bind(Tweet tweet) {
            tvUserName.setText(tweet.user.name);
            tvUserScreenName.setText("@" + tweet.user.screenName);
            tvUserVerifiedCheckmark.setVisibility(tweet.user.isVerified ? View.VISIBLE : View.GONE);
            tvTimestamp.setText(tweet.getRelativeTimeAgo());
            tvBody.setText(tweet.body);
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
        }
    }
}
