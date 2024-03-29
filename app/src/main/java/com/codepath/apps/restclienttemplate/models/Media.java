package com.codepath.apps.restclienttemplate.models;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

@Parcel
public class Media {

    //List out the attributes of a Media object
    public String mediaURLHttps;
    public String mediaType;



    public Media(){

    }// end default constructor


    //Use this method to take in a JSONArray and extract the necessary information for each media type
    public static Media fromJSON(JSONArray jsonArray) throws JSONException {

        Log.d("MediaDebug", "Inside fromJSON");

        Media media = new Media();


        //Make sure to check if the array has any information first
        if (jsonArray.length() == 0){
            Log.d("MediaDebug", "No associated media");
            return null;
        }// end if

        Log.d("MediaDebug", "Found an image and am populating the attributes before returning it");
        media.mediaType = jsonArray.getJSONObject(0).getString("type");
        media.mediaURLHttps = jsonArray.getJSONObject(0).getString("media_url_https");

        return media;

    }// end fromJSON


}// end class Media
