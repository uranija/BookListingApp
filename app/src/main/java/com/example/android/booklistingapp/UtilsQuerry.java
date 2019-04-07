package com.example.android.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class UtilsQuerry {
    private static final String LOG_TAG = UtilsQuerry.class.getSimpleName();

    //An empty private constructor makes sure that the class is not going to be initialised.
    private UtilsQuerry() {
    }

    /**
     * Return list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */

    public static List<Book> extractFeaturesFromJson(String booksJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Create an empty ArrayList to which we can start adding books
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Convert JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            if (baseJsonResponse.has("items")) {
                // Extract "items" JSONArray associated with the key called "items"
                // which represents a list of information about the book
                JSONArray booksArray = baseJsonResponse.getJSONArray("items");

                // For each book in the booksArray, create an {@link Books} object
                for (int i = 0; i < booksArray.length(); i++) {
                    // Get a single book and position it within the list of books
                    JSONObject currentBook = booksArray.getJSONObject(i);


                    // For a given book, extract the JSONObject associated with the
                    // key called "volumeInfo", which represents a list of all information
                    // for that book
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    // Extract the value from the key called "title"
                    String title = volumeInfo.getString("title");


                    /// Extract "authors" JSONArray associated with the key called "authors"
                    // which may represents a list of authors of the book
                    JSONArray authorsArray;
                    StringBuilder authors = new StringBuilder();
                    if (volumeInfo.has("authors")) {
                        authorsArray = volumeInfo.getJSONArray("authors");
                        // Iterate the JSONArray and print the info of JSONObjects
                        for (int n = 0; n < authorsArray.length(); n++) {
                            authors.append(System.getProperty("line.separator"));
                            authors.append(authorsArray.getString(n));
                        }
                    } else {
                        authors.append("No Author");
                    }

                    /// Extract "categories" JSONArray associated with the key called "categories"
                    // which may represents a list of authors of the book
                    JSONArray categoriesArray;
                    StringBuilder categories = new StringBuilder();
                    if (volumeInfo.has("categories")) {
                        categoriesArray = volumeInfo.getJSONArray("categories");
                        // Iterate the JSONArray and print the info of JSONObjects
                        for (int n = 0; n < categoriesArray.length(); n++) {
                            categories.append(System.getProperty("line.separator"));
                            categories.append(categoriesArray.getString(n));
                        }
                    } else {
                        categories.append("No categories");
                    }


                    // Extract the URL of the book
                    String url = null;
                    if (volumeInfo.has("infoLink")) {
                        url = volumeInfo.getString("infoLink");
                    }

                    // Extract the value from the key called "publishedDate"
                    String publishedDate = "";
                    if (volumeInfo.has("publishedDate")) {
                        publishedDate = volumeInfo.getString("publishedDate");
                    } else {
                        publishedDate = "";
                    }

                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    String thumbnailLink = "";
                    boolean pictureUrlExists = imageLinks.has("smallThumbnail");
                    if (pictureUrlExists) {
                        thumbnailLink = imageLinks.getString("smallThumbnail");
                    }

                    // Extract the value from the key called "description"
                    String description = "";
                    if (volumeInfo.has("description")) {
                        description = volumeInfo.getString("description");
                    } else {
                        description = "";
                    }

                    // Extract the value from the key called "description"
                    String publisher = "";
                    if (volumeInfo.has("publisher")) {
                        publisher = volumeInfo.getString("publisher");
                    } else {
                        publisher = "";
                    }

                    // Create a new {@link Books} object with the title, subtitle and authors
                    // from the JSON response.
                    Book booksObject = new Book(title, authors, publisher, url, publishedDate, categories, description, thumbnailLink);

                    // Add the new {@link Books} to the list of books
                    books.add(booksObject);
                }
            }
        }
        catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the JSON list books", e);
        }

        // Return the list of books
        return books;
    }

    /**
     * Query the Google Books API and return a list of {@link Book} object.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request & receive a JSON respond
        String jsonRespond = null;
        try {
            jsonRespond = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
        }

        //Return the list
        return extractFeaturesFromJson(jsonRespond);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}