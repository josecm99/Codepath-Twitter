package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{


    private List<Tweet> mTweets;

    //Context that we get from onCreateViewHolder
    Context context;

    //TwitterClient object we will use to update the Tweet information when necessary
    TwitterClient client;

    //Pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets){

        mTweets = tweets;
    }// end constructor


    //This method needed when we need to create a new row.
    //For each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate what will be the Tweet Row
        View tweetView = inflater.inflate(R.layout.item_tweet, viewGroup, false);

        //Create a ViewHolder object
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;

    }// end onCreateViewHolder

    //Bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Tweet tweet = mTweets.get(i);

        //Initialize the client here for updating use later on.
        client = TwitterApplication.getRestClient(context);

        //Populate the views according to this data
        viewHolder.tvUserName.setText("@" + tweet.user.screenName);

        viewHolder.tvBody.setText(tweet.body);

        viewHolder.tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt) );

        viewHolder.tvActualName.setText(tweet.user.name);

        viewHolder.tvRetweetNum.setText(Integer.toString(tweet.retweetCount) );

        viewHolder.tvFavoriteNum.setText(Integer.toString(tweet.favoriteCount) );

        // Doing this for the profile pic
        Glide.with(context)
                .load(tweet.user.profileImageURL)
                .bitmapTransform(new CenterCrop(context) )
                .bitmapTransform(new CropCircleTransformation(context) )
                .into(viewHolder.ivProfileImage);


        viewHolder.ivOptionalImage.setVisibility(View.GONE);

        if (tweet.hasRetweeted){
            viewHolder.ivRetweetImage.setImageResource(R.drawable.ic_vector_retweet);
        }else{
            viewHolder.ivRetweetImage.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }
        if (tweet.hasFavorited){
            viewHolder.ivFavoriteImage.setImageResource(R.drawable.ic_vector_heart);
        }else{
            viewHolder.ivFavoriteImage.setImageResource(R.drawable.ic_vector_heart_stroke);
        }




        viewHolder.ivFavoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.hasFavorited){
                    client.unFavoriteTweet(tweet.uid, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            //Change the state of the image
                            viewHolder.ivFavoriteImage.setImageResource(R.drawable.ic_vector_heart_stroke);

                            //Update the number of favorites
                            viewHolder.tvFavoriteNum.setText(Integer.toString(tweet.favoriteCount - 1) );
                        }// end onSuccess

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("TweetAdapterDebug", "Something went wack over here somewhere...");
                        }
                    });
                }// end if
                else{
                    client.favoriteTweet(tweet.uid, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            //Change the state of the image
                            viewHolder.ivFavoriteImage.setImageResource(R.drawable.ic_vector_heart);
                            //Update the number of favorites
                            viewHolder.tvFavoriteNum.setText(Integer.toString(tweet.favoriteCount + 1) );
                        }// end onSuccess

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("TweetAdapterDebug", "Something went wack over here somewhere...");
                        }
                    });
                }// end else
            }
        });


        viewHolder.ivRetweetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.hasRetweeted){
                    client.unRetweetTweet(tweet.uid, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            //Change the state of the image
                            viewHolder.ivRetweetImage.setImageResource(R.drawable.ic_vector_retweet_stroke);

                            //Update the number of Retweets
                            viewHolder.tvRetweetNum.setText(Integer.toString(tweet.retweetCount - 1) );
                            notifyDataSetChanged();
                        }// end onSuccess

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("TweetAdapterDebug", "Something went wack over here somewhere...");
                        }
                    });
                }// end if
                else{
                    client.retweetTweet(tweet.uid, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            //Change the state of the image
                            viewHolder.ivRetweetImage.setImageResource(R.drawable.ic_vector_retweet);
                            //Update the number of retweets
                            viewHolder.tvRetweetNum.setText(Integer.toString(tweet.retweetCount + 1) );
                            notifyDataSetChanged();
                        }// end onSuccess

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("TweetAdapterDebug", "Something went wack over here somewhere...");
                        }
                    });
                }// end else
            }
        });

        //Perform check here to see if tweet media is empty or not
        if (tweet.media == null) {
            viewHolder.ivOptionalImage.setVisibility(View.GONE);
        }// end if
        else{
            viewHolder.ivOptionalImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(tweet.media.mediaURLHttps)
                    .bitmapTransform(new RoundedCornersTransformation(context, 30, 20) )
                    .into(viewHolder.ivOptionalImage);
        }// end else


    }// end onBindViewHolder

    @Override
    public int getItemCount() {
        return mTweets.size();
    }// end getItemCount



    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add tweets
    public void addAll(JSONArray jsonArray) {
        for (int i =0; i < jsonArray.length(); i++){
            //Convert each object to a Tweet Model
            try {
                Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
                //Add that Tweet Model to our data source
                mTweets.add(tweet);
                //Notify the adapter that we've added an item
                notifyItemInserted(mTweets.size() - 1);
            }catch(JSONException e){
                e.printStackTrace();
            }


        }// end for
        notifyDataSetChanged();
    }



    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    } // end getRelativeTimeAgo

    //create inner ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder{


        //List the XML elements we will be inflating onto RecyclerView
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvRelativeTime;
        public TextView tvActualName;
        public TextView tvRetweetNum;
        public TextView tvFavoriteNum;
        public ImageView ivRetweetImage;
        public ImageView ivFavoriteImage;

        public ImageView ivOptionalImage;

        public ViewHolder(View itemView){
            super(itemView);

            //Perform findViewByID lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
            tvActualName = (TextView) itemView.findViewById(R.id.tvActualName);
            tvRetweetNum = (TextView) itemView.findViewById(R.id.tvRetweetNum);
            tvFavoriteNum = (TextView) itemView.findViewById(R.id.tvFavoriteNum);
            ivOptionalImage = (ImageView) itemView.findViewById(R.id.ivOptionalImage);
            ivRetweetImage = (ImageView) itemView.findViewById(R.id.ivRetweetImage);
            ivFavoriteImage = (ImageView) itemView.findViewById(R.id.ivFavoriteImage);

        }// end constructor

    }// end inner class ViewHolder


}// end outer class TweetAdapter
