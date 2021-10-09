package com.codepath.johnroyal.tweeter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.johnroyal.tweeter.models.Tweet;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";
    private static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;
    TextView tvCharacterCount;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCharacterCount = findViewById(R.id.tvCharacterCount);

        client = TwitterApp.getRestClient(this);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int characterCount = editable.toString().length();
                String characterCountString = String.valueOf(characterCount);
                tvCharacterCount.setText(characterCountString);
                btnTweet.setEnabled(characterCount > 0 && characterCount <= MAX_TWEET_LENGTH);
            }
        });
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                TweetValidationError error = TweetValidationError.forTweetContent(tweetContent);
                if (error != null) {
                    Toast.makeText(ComposeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                client.publishTweet(tweetContent, new TwitterClient.PublishTweetResponseHandler() {
                    @Override
                    public void onSuccess(Tweet tweet) {
                        Log.i(TAG, "Tweet published successfully");

                        Intent data = new Intent();
                        data.putExtra("tweet", Parcels.wrap(tweet));
                        setResult(RESULT_OK, data);
                        finish();
                    }

                    @Override
                    public void onFailure(TwitterClient.APIError error) {
                        Log.w(TAG, "Failed to publish tweet: " + error.toString());
                        Toast.makeText(ComposeActivity.this, "Failed to publish tweet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private enum TweetValidationError {
        EMPTY, TOO_LONG;

        public static TweetValidationError forTweetContent(String tweetContent) {
            if (tweetContent.isEmpty()) {
                return EMPTY;
            } else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                return TOO_LONG;
            }
            return null;
        }

        @Override
        public String toString() {
            switch (this) {
                case EMPTY:
                    return "The tweet is empty.";
                case TOO_LONG:
                    return "The tweet is too long.";
            }
            return null;
        }
    }
}