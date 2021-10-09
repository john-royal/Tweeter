package com.codepath.johnroyal.tweeter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.johnroyal.tweeter.models.Tweet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = "TimelineActivity";
    private static final int COMPOSE_REQUEST_CODE = 1;

    SwipeRefreshLayout swipeContainer;
    RecyclerView rvTweets;
    FloatingActionButton fabCompose;

    TwitterClient client;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeContainer = findViewById(R.id.swipeContainer);
        rvTweets = findViewById(R.id.rvTweets);
        fabCompose = findViewById(R.id.fabCompose);

        client = TwitterApp.getRestClient(this);
        adapter = new TweetsAdapter(this);

        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        configureEventListeners();
        populateHomeTimeline();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == COMPOSE_REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            adapter.add(0, tweet);
            rvTweets.smoothScrollToPosition(0);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void configureEventListeners() {
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComposeActivity();
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });
    }

    private void showComposeActivity() {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, COMPOSE_REQUEST_CODE);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new TwitterClient.HomeTimelineResponseHandler() {
            @Override
            public void onSuccess(List<Tweet> tweets) {
                Log.i(TAG, "onSuccess");
                adapter.setTweets(tweets);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(TwitterClient.APIError error) {
                Log.w(TAG, "Failed to fetch home timeline: " + error.toString());
            }
        });
    }
}