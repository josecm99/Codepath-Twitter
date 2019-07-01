package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    //Client we are using to connect to Twitter
    private TwitterClient client;

    private TweetAdapter tweetAdapter;

    private ArrayList<Tweet> tweets;

    private RecyclerView rvTweets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient(this);

        //Find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);

        //Initialize the ArrayList
        tweets = new ArrayList<>();

        //Construct adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);

        //RecyclerView Setup(layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this) );
        rvTweets.setAdapter(tweetAdapter);



        populateTimeline();
    }

    //This function will take care of populating our user's timeline once they are authenticated and logged in
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                //Iterate through the JSONArray
                    //For each entry, deserialize the JSON Object

                for (int i =0; i < response.length(); i++){
                    //Convert each object to a Tweet Model
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        //Add that Tweet Model to our data source
                        tweets.add(tweet);
                        //Notify the adapter that we've added an item
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }


                }// end for


                Log.d("TwitterClient", response.toString() );
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString() );
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString );
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString() );
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString() );
                throwable.printStackTrace();
            }
        }); // end anonymous inner class

    }// end populateTimeline

}// end TimelineActivity Class
