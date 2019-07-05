package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    public static final int COMPOSE_TWEET_REQUEST_CODE = 20;
    //Client we are using to connect to Twitter
    private TwitterClient client;

    private TweetAdapter tweetAdapter;

    private ArrayList<Tweet> tweets;

    private RecyclerView rvTweets;

    private Toolbar toolbar;


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

        toolbar = (Toolbar) findViewById(R.id.tbTimeline);
        setSupportActionBar(toolbar);


        //RecyclerView Setup(layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this) );
        rvTweets.setAdapter(tweetAdapter);



        populateTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }// end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                Log.d("TimelineActivityDebug", "Pressed the Pencil Icon");
                composeMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }// end onOptionsItemSelected

    //This method opens up an Intent that will take us to the ComposeActivity
        //Allowing us to create and submit an actual tweet.
    private void composeMessage() {

        Log.d("TimelineActivityDebug", "About to enter the ComposeActivity");

        Intent tweetIntent = new Intent(this, ComposeActivity.class);



        startActivityForResult(tweetIntent, COMPOSE_TWEET_REQUEST_CODE);

    }// end composeMessage

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check request code and result code first
        if (requestCode == COMPOSE_TWEET_REQUEST_CODE && resultCode == RESULT_OK){

            Log.d("TimelineDebug", "About to try and unwrap my Tweet from ComposeActivity");

            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()) );

            //Once we have this tweet, add it to the ArrayList in the first position and scroll up to see it having been added
                //Don't forget to notify the adapter that you have inserted an item
            Log.d("TimelineDebug", "Was able to get into onActivityResult");
            tweets.add(0, newTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
            Log.d("TimelineDebug", "Was able to run the code in onActivityResult");
        }// end if

    }// end onActivityResult



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
