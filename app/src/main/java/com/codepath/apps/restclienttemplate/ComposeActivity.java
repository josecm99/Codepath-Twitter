package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 140;
    private EditText etTweet;
    private Button btnSend;
    private TextView tvCharsRemaining;
    private TwitterClient client;

    private User currUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient(this);

        Log.d("ComposeActivityDebug", "Got into the onCreate in ComposeActivity");

        etTweet = (EditText) findViewById(R.id.etTweet);
        btnSend = (Button) findViewById(R.id.btnTweet);
        tvCharsRemaining = findViewById(R.id.tvCharsRemaining);

        tvCharsRemaining.setText(Integer.toString(MAX_TWEET_LENGTH) );


        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvCharsRemaining.setText(Integer.toString(MAX_TWEET_LENGTH) );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharsRemaining.setText(Integer.toString(MAX_TWEET_LENGTH - etTweet.getText().length()) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        client = TwitterApplication.getRestClient(this);


        //INCLUDE SOMETHING HERE FOR PUTTING USER INTO COMPOSE_ACTIVITY

        btnSend.setBackgroundColor(getResources().getColor(R.color.twitter_blue) );
        btnSend.setTextColor(Color.WHITE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTweet();
            }
        });



    }

    private void sendTweet() {



        //Start the network request here.
        //Includes creating Tweet object to send back to the original Activity
        client.sendTweet(etTweet.getText().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //First create a JSONObject from this responseBody passed in
                if (statusCode == 200) {

                    try {

                        Log.d("ComposeActivityDebug", "Sent the Tweet and am now doing stuff with the JSONObject");


                        JSONObject jsonResponse = new JSONObject(new String(responseBody));

                        Tweet newTweet = Tweet.fromJSON(jsonResponse);

                        //Once this Tweet has been created, create a Result Intent to pass it back
                        //to the calling Activity

                        Intent resultTweet = new Intent();

                        resultTweet.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(newTweet));

                        //Once we've done that, we can call setResult and then finish with this Activity
                        setResult(RESULT_OK, resultTweet);

                        Log.d("ComposeActivityDebug", "About to finish with my onSuccess (tweet sending stuff after button press)");
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }// end onSuccess

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }// end onFailure
        });

    }
}
