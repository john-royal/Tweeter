package com.codepath.johnroyal.tweeter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.johnroyal.tweeter.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.Headers;

/*
 *
 * This is the object responsible for communicating with a REST API.
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes:
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 *
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 *
 */
public class TwitterClient extends OAuthBaseClient {

    private static final boolean USE_SAMPLE_TIMELINE = false;

    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
    public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    private static String createCallbackUrl(Context context) {
        return String.format(REST_CALLBACK_URL_TEMPLATE,
                context.getString(R.string.intent_host),
                context.getString(R.string.intent_scheme),
                context.getPackageName(),
                FALLBACK_URL);
    }

    public TwitterClient(Context context) {
        super(context,
                REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                null, // OAuth 2.0 scope; null for OAuth 1.0
                createCallbackUrl(context));
    }

    public void publishTweet(String tweetContent, PublishTweetResponseHandler handler) {
        String endpointUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweetContent);

        client.post(endpointUrl, params, "", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                assert statusCode == 201;
                try {
                    Tweet tweet = new Tweet(json.jsonObject);
                    handler.onSuccess(tweet);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to parse response JSON.");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                APIError error = new APIError(statusCode, response);
                handler.onFailure(error);
            }
        });
    }

    public abstract static class PublishTweetResponseHandler {
        public abstract void onSuccess(Tweet tweet);
        public abstract void onFailure(APIError error);
    }

    public void getHomeTimeline(HomeTimelineResponseHandler handler) {
        if (USE_SAMPLE_TIMELINE) {
            Log.i("TwitterClient", "Returning tweets from sample JSON");
            List<Tweet> sampleTweets = loadSampleHomeTimeline();
            handler.onSuccess(sampleTweets);
            return;
        }

        Log.i("TwitterClient", "Fetching tweets from Twitter API");

        String endpointUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", 1);

        client.get(endpointUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                assert statusCode == 200 || statusCode == 304;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(json.jsonArray);
                    handler.onSuccess(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to parse response JSON.");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                APIError error = new APIError(statusCode, response);
                handler.onFailure(error);
            }
        });
    }

    public abstract static class HomeTimelineResponseHandler {
        public abstract void onSuccess(List<Tweet> tweets);
        public abstract void onFailure(APIError error);
    }

    private List<Tweet> loadSampleHomeTimeline() {
        Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.sample_home_timeline);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();

        try {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(currentLine);
            }

            JSONArray tweetsJsonArray = new JSONArray(stringBuilder.toString());
            return Tweet.fromJsonArray(tweetsJsonArray);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read the sample data file.");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse JSON for sample data.");
        }
    }

    public class APIError {
        int statusCode;
        JSONArray errors;

        APIError(int statusCode, String body) {
            this.statusCode = statusCode;
            try {
                errors = new JSONObject(body).getJSONArray("errors");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to parse Twitter error response JSON.");
            }
        }
    }
}
