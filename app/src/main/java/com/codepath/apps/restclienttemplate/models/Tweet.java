package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {

    //Attributes we are keeping from the Tweet
    public String body;
    public long uid;
    public User user;
    public String createdAt;
    public int retweetCount;
    public int favoriteCount;
    public boolean hasFavorited;
    public boolean hasRetweeted;

    //This attribute will be its own JSONArray (for extracting media)
    public Media media;
    public boolean hasMedia;

    public Tweet(){

    }// end default constructor

    //Deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        //Extract all of the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.hasRetweeted = jsonObject.getBoolean("retweeted");
        tweet.hasFavorited = jsonObject.getBoolean("favorited");


        tweet.user = User.fromJSON(jsonObject.getJSONObject("user") );

        Log.d("TweetDebug", "Current Tweet: \n\t" + tweet.user + "\n\t" + tweet.body);

        JSONObject jsonEntities = jsonObject.getJSONObject("entities");

        if (jsonEntities.has("media") ){
            tweet.media = Media.fromJSON(jsonEntities.getJSONArray("media") );
        }// end if
        else{
            tweet.media = null;
        }

        return tweet;
    }// end fromJSON



}
