package com.example.android.aimer;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

public class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static ArrayList<book> fetchBooks(String requestUrl){
        // Create URL object
        URL url = createUrl(requestUrl);

        //Create new jsonResponse
        String jsonResponse = null;
        try{
            //Set its to result of makeHttpRequest
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.d(LOG_TAG, "Error closing input stream", e);
        }

        //Creating ArrayList of books called books
        ArrayList<book> books = null;
        try {
            //Setting that ArrayList´s value to data we need.
            books = extractBooks(jsonResponse);
        } catch (JSONException e){
            e.printStackTrace();
        }

        //Returning ArrayList books
        Log.d( LOG_TAG, "TEST: fetchBooksData.");
        return books;
    }

    /**
     * Method that just creates the url
     * @param stringUrl
     * @return
     */
    public static URL createUrl(String stringUrl){
        URL url = null;
        try{
            //Creating new url of type URL
            url = new URL(stringUrl);
        }
        catch (MalformedURLException e){
            Log.d(LOG_TAG, "Error with creating URL.", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * @param url
     * @return
     * @throws IOException
     */
    public static String makeHttpRequest(URL url) throws  IOException{
        String jsonResponse = "";

        //If there is no url automatically return null.
        if (url == null){
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            //Opening connection.
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            //Setting what do you want to do on that website (GET = get data from there)
            urlConnection.setRequestMethod("GET");
            //Finally connect.
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if(urlConnection == null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        //String builder = data type a bit similar to String except StringBuilder is mutable (you can subtract and add other Strings to it and index through it and those indexes delete or whatever)
        StringBuilder output = new StringBuilder();

        if(inputStream != null){
            //Converts inputStream (InputStream) so than BufferedReader can convert it into String
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            //Do this for every line in that API
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        //Returning output as String
        return output.toString();
    }

    /**
     * Return a list of {@link book} objects that has been built up from
     * parsing a JSON response.
     * @param bookJSON
     * @return
     * @throws JSONException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static ArrayList<book> extractBooks(String bookJSON ) throws JSONException{
        // If the JSON string is empty or null, then return early.
        if(TextUtils.isEmpty(bookJSON)){
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try{
            // build up a list of book objects with the corresponding data.
            //Root = the whole data base
            JSONObject root = new JSONObject(bookJSON);
            //Items is a resource that contains all the books
            JSONArray items = root.getJSONArray("items");
            //We want books so we will loop through the items resource 10 times
            for(int i = 0; i < 10; i++){
                //That one item from items
                JSONObject book = items.getJSONObject(i);
                //Get one section of that item volumeInfo
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                //In volumeInfo there are data we need.
                //Get bookTitle with key title from section volumeInfo
                String bookTitle = volumeInfo.getString("title");
                String bookSubtitle = "";
                String bookName = "";
                //If book has no subtitle don´t add anything to the title
                //Whenever there is an error go to catch loop.
                try{
                    //If the book doesn´t  have any subtitle go to catch loop
                    //If yes get that subtitle
                    bookSubtitle = volumeInfo.getString("subtitle");
                    //And add title ": " and subtitle together and name it bookName
                    bookName = bookTitle + ": " + bookSubtitle;
                }catch (JSONException e){
                    //If there is no subtitle just use the title as name.
                    bookName = bookTitle;
                }
                //Because you can have more authors than one. In that database they use an Array
                JSONArray authorsNames = volumeInfo.getJSONArray("authors");
                String authors = "";
                //Loop through that array for every author and add them together in one String
                for(int e = 0; e<authorsNames.length(); e++){
                    if(e == 0){
                        Log.d(LOG_TAG, "e == 0");
                        //If the index is 0 the program is executing the first author so we don´t want a comma in front of it
                        authors = authors + authorsNames.getString(e);
                    }
                    else {
                        //If the index is not 0 the program is executing second or greater author so we want a comma in front of it
                        authors = authors + ", " + authorsNames.getString(e);
                    }
                }
                //Create new String description with start value "No description available".
                String description = "No description available";
                try{
                    //If there is a description available use it as value
                    description = volumeInfo.getString("description");
                }catch (JSONException e){
                    //If there is no description in that book try to use searchInfo as a value of description
                    try {
                        //If there is searchInfo in that book try to use it as a value of description
                        JSONObject searchInfo = book.getJSONObject("searchInfo");
                        description = searchInfo.getString("textSnippet");
                    }catch (JSONException e1){
                        //If not just let the value of description be "No description available"
                        description = "No description available";
                    }
                }
                //Get the url of that book on googleBooks and store it to value bookUrl
                String bookUrl = volumeInfo.getString("infoLink");

                //Add all those data into one book
                books.add(new book(bookName, authors, description, bookUrl));
                //That was just first round of that loop (we have to do 10)
            }
        }catch (JSONException e) {
        // If an error is thrown when executing any of the above statements in the "try" block,
        // catch the exception here, so the app doesn't crash. Print a log message
        // with the message from the exception.
        Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of 10 books
        return books;
    }

}
