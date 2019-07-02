package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{


    private List<Tweet> mTweets;

    //Context that we get from onCreateViewHolder
    Context context;

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Tweet tweet = mTweets.get(i);

        //Populate the views according to this data
        viewHolder.tvUserName.setText(tweet.user.name);

        viewHolder.tvBody.setText(tweet.body);

        viewHolder.tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt) );

        Glide.with(context)
                .load(tweet.user.profileImageURL)
                .into(viewHolder.ivProfileImage);

    }// end onBindViewHolder

    @Override
    public int getItemCount() {
        return mTweets.size();
    }// end getItemCount



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

    //create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder{


        //List the XML elements we will be inflating onto RecyclerView
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvRelativeTime;

        public ViewHolder(View itemView){
            super(itemView);

            //Perform findViewByID lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
        }// end constructor

    }// end inner class ViewHolder


}// end outer class TweetAdapter
