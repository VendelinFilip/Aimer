package com.example.android.aimer;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

public class BookLoader extends AsyncTaskLoader<List<book>> {


    /** Query URL */
    private String mUrl;

    private static final String LOG_TAG = booksActivity.class.getName();

    public BookLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.d( LOG_TAG, "TEST: onStartLoading");
        forceLoad();
    }

    //This is happening while loading and getting the data.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public List<book> loadInBackground() {
        Log.d( LOG_TAG, "TEST: loadInBackground");
        // Don't perform the request if there are no URLs, or the first URL is null.
        if(mUrl == null){
            return null;
        }
        Log.d(LOG_TAG, "Before fetching data");
        //Calling the method that gets all that data from web page
        ArrayList<book> books = QueryUtils.fetchBooks(mUrl);
        Log.d(LOG_TAG, "After fetching data");

        return books;
    }
}
