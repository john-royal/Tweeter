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

	private static final boolean USE_SAMPLE_TIMELINE = true;

    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
    public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    private final List<Tweet> sampleTweets;

    public TwitterClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                null,  // OAuth2 scope, null for OAuth1
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
        Log.i("TwitterClient", "Callback URL is: " + String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));

        if (USE_SAMPLE_TIMELINE) {
            sampleTweets = loadSampleHomeTimeline(context.getResources());
        }
    }

    public void getHomeTimeline(TimelineResponseHandler handler) {
        if (USE_SAMPLE_TIMELINE) {
			Log.i("TwitterClient", "Returning tweets from sample JSON");
            handler.onSuccess(HTTPResponseCode.OK, sampleTweets);
            return;
        }

		Log.i("TwitterClient", "Fetching tweets from Twitter API");

        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", 1);

        client.get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                assert statusCode == 200 || statusCode == 304;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(json.jsonArray);
                    handler.onSuccess(HTTPResponseCode.OK, tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to parse response JSON.");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                HTTPResponseCode responseCode = HTTPResponseCode.fromStatusCode(statusCode);
                FailureResponse failureResponse = new FailureResponse(responseCode, headers, response);
                handler.onFailure(failureResponse);
            }
        });
    }

    private List<Tweet> loadSampleHomeTimeline(Resources resources) {
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

    public enum HTTPResponseCode {
        OK(200),
        NOT_MODIFIED(304),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        NOT_ACCEPTABLE(406),
        GONE(410),
        UNPROCESSABLE_ENTITY(422),
        TOO_MANY_REQUESTS(429),
        INTERNAL_SERVER_ERROR(500),
        BAD_GATEWAY(502),
        SERVICE_UNAVAILABLE(503),
        GATEWAY_TIMEOUT(504);

        public final int statusCode;

        HTTPResponseCode(int statusCode) {
            this.statusCode = statusCode;
        }

        static HTTPResponseCode fromStatusCode(int statusCode) {
            for (HTTPResponseCode value : HTTPResponseCode.values()) {
                if (value.statusCode == statusCode) {
                    return value;
                }
            }
            return null;
        }

        public String toString() {
            switch (this) {
                case OK:
                    return "Request was successful.";
                case NOT_MODIFIED:
                    return "No new data to return.";
                case BAD_REQUEST:
                    return "Request was invalid or cannot otherwise be served.";
                case UNAUTHORIZED:
                    return "Request could not be authenticated.";
                case FORBIDDEN:
                    return "Access denied. You may need to sign up for API access.";
                case NOT_FOUND:
                    return "Resource not found.";
                case NOT_ACCEPTABLE:
                    return "Invalid format specified in request.";
                case GONE:
                    return "Resource is gone. This may mean the API endpoint was turned off.";
                case UNPROCESSABLE_ENTITY:
                    return "Request contained data that could not be processed.";
                case TOO_MANY_REQUESTS:
                    return "Rate limit has been exhausted for this resource.";
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                case GATEWAY_TIMEOUT:
                    return "API is unavailable because of a server error or outage.";
                default:
                    return "An unknown error occurred (HTTP status code: " + statusCode + ").";
            }
        }
    }

    public abstract static class TimelineResponseHandler {
        public abstract void onSuccess(HTTPResponseCode responseCode, List<Tweet> tweets);

        public abstract void onFailure(FailureResponse failureResponse);
    }

    public class FailureResponse {
        HTTPResponseCode code;
        Headers headers;
        String body;

        FailureResponse(HTTPResponseCode code, Headers headers, String body) {
            this.code = code;
            this.headers = headers;
            this.body = body;
        }
    }
}
