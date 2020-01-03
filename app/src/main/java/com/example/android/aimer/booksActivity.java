package com.example.android.aimer;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class booksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<book>> {

    private final static int BOOK_LOADER_ID = 1;
    private static final String LOG_TAG = booksActivity.class.getName();
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_list);

        //Setting new loader manager
        LoaderManager loaderManager = getLoaderManager();

        //Starting loader manager
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        Log.d( LOG_TAG, "TEST: loaderManager.initLoader()");
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    //Overriding the onCreateLoader method that is executed when loader is created.
    public Loader<List<book>> onCreateLoader(int i, Bundle bundle) {
        Log.d( LOG_TAG, "TEST: onCreateLoader");
        //Receiving variable searchedWord (the text you typed into that edit text)
        final String searchedWord = getIntent().getExtras().getString("searchedWord");
        // Create a new loader for the given URL
        final String BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=" + searchedWord + "&maxResults=10";
        Loader<List<book>> result =  new BookLoader(this, BOOKS_URL);
        //If we haven´t got the result we just display blank screen with a ProgressBar
        if (result == null){
            //Finding the right listView
            ListView mainListView = (ListView)findViewById(R.id.list);
            //Setting its data to null so it doesn´t display anything
            mainListView.setEmptyView(mainListView);
        }
        return result;
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link)
     * FragmentManager.openTransaction()} for further discussion on this.
     *
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     *
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<List<book>> loader, List<book> books) {
        //Finding the right ProgressBar
        ProgressBar loading_progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        //Setting its visibility to gone
        loading_progressBar.setVisibility(View.GONE);
        //Finding the right image
        ImageView problemImage = (ImageView)findViewById(R.id.problem_image);
        //Setting its visibility to gone
        problemImage.setVisibility(View.GONE);
        //Clearing the adapter if it isn´t already equal to null
        if (mAdapter != null) {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();
        }

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            updateUi(books);
            Log.d(LOG_TAG, "TEST: UI updated");
        }

        //Receiving variable searchedWord (the text you typed into that edit text)
        final String searchedWord = getIntent().getExtras().getString("searchedWord");
        // Create a new loader for the given URL
        final String BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=" + searchedWord + "&maxResults=10";

        //If there are no data to display change the layout like this
        if(books == null){
            //Find the right textView
            TextView emptyTextVIew = (TextView)findViewById(R.id.empty_view);
            //Set its text to ...
            emptyTextVIew.setText("Your search did not match any results.\n" +
                    "\n" +
                    "-Suggestions:\n" +
                    "\n" +
                    "-Make sure that all words are spelled correctly.\n" +
                    "-Try different keywords.\n" +
                    "-Try more general keywords.\n" +
                    "-Try fewer keywords.\n" +
                    "-Search Results\n");

            //Finding the right image
            problemImage.setImageResource(R.drawable.no_data_image);
            //Setting its resource (well the image which the imageView should display)
            problemImage.setVisibility(View.VISIBLE);
        }

        //Setting connectivityManager (thanks to this I can then say if the device is connected to wifi)
        final ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Finding out if device is connected to WIFI.
        final NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //Finding out if device is using mobile data.
        final NetworkInfo mMobileData = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        //If device isn´t connected to both mobile data and WIFI:
        if (mWifi.isConnected() != true && mMobileData.isConnectedOrConnecting() != true){
            //Find the text view that will show that you are not connected to WIFI or mobile DATA.
            TextView emptyTextView = (TextView)findViewById(R.id.empty_view);
            //Show this text in it
            emptyTextView.setText("Internet connection is not available.");

            //Find the image view that will show that you are not connected to WIFI or mobile DATA.
            problemImage.setImageResource(R.drawable.no_internet_image);
            //Set its resource (the image that will be shown it that image view)
            problemImage.setVisibility(View.VISIBLE);
        }

        if (mWifi.isConnected() == true) {
            //Find the text view that will show if no earthquakes are available.
            TextView emptyTextView = (TextView)findViewById(R.id.empty_view);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<book>> loader) {
        Log.d(LOG_TAG, "DONE");
        //When reseting loader clear the adapter so next time it can show other data
        if(mAdapter != null){
            mAdapter.clear();
            Log.d( LOG_TAG, "TEST: onLoaderReset");
        }
    }

    /**
     * Method that updates the ListView.
     * Is called only when everything goes fine.
     * @param books
     */
    private void updateUi(List<book> books){
        //Finding the right ListView
        ListView booksListView = (ListView) findViewById(R.id.list);
        //Creating new BookAdapter
        BookAdapter adapter = new BookAdapter(this, books);

        //Setting that adapter onto that ListView.
        booksListView.setAdapter(adapter);
    }
}