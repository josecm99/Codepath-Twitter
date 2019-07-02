package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private EditText etTweet;
    private Button btnSend;
    private TwitterClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Log.d("ComposeActivityDebug", "Got into the onCreate in ComposeActivity");

        etTweet = (EditText) findViewById(R.id.etTweet);
        btnSend = (Button) findViewById(R.id.btnSend);

        client = TwitterApplication.getRestClient(this);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTweet();
            }
        });



    }

    private void sendTweet() {
        client = TwitterApplication.getRestClient(this);


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

                        resultTweet.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(resultTweet));

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
