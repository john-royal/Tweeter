package com.codepath.johnroyal.tweeter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public String name;
    public String screenName;
    public String profileImageUrl;
    public boolean isVerified;

    // Empty constructor required by Parceler
    public User() {}


    public User(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        screenName = jsonObject.getString("screen_name");
        profileImageUrl = jsonObject.getString("profile_image_url_https");
        isVerified = jsonObject.getBoolean("verified");
    }
}
