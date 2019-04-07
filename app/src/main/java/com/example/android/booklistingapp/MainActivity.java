package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Tag for LOG Message
    private static final String LOG_TAG = MainActivity.class.getName();

    // URL from Google API
    private static final String BOOK_URL_BASE =
            "https://www.googleapis.com/books/v1/volumes?q=";

        // Add a maximum results of 10 to the search query
    private static final String MAX_RESULTS = "&maxResults=10";

    // Adapter for the Book list
    private BookAdapter mAdapter;

    // Edit field where the user can search for books
    private EditText searchEditTextView;

    // TextView that is visible when there is a problem with the connection or the query
    private TextView mEmptyView;

    // ProgressBar that is visible when
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a ConnectivityManager and get the NetworkInfo from it
        final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        final boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        //Create a boolean variable for the connectivity status


        // Find a reference to Views
        final ListView bookListView = (ListView) findViewById(R.id.list_view);
        searchEditTextView = (EditText) findViewById(R.id.search_EditTextView);
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        mEmptyView = (TextView) findViewById(R.id.empty_text_view);
        loadingIndicator = findViewById(R.id.progress_bar);

        // Set empty state view on the list view with books, when there is no data.
        bookListView.setEmptyView(mEmptyView);

        // Hide the keyboard when the app starts
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.e(LOG_TAG, "Hide the keyboard.");

        // Create new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView)
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // OnItemClickListener open the website for the current book
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = mAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(webIntent);
                }
            }
        });

        // Set a click listener to the ImageButton Search
        searchButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                final boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

                //If there is a network connection
                if (isConnected) {
                    Log.e(LOG_TAG, "This is called if there is a internet connection.");
                    String searchWord = searchEditTextView.getText().toString().replaceAll("\\s+", "")
                            .toLowerCase();
                    if (searchWord.isEmpty()) {
                        Toast.makeText(MainActivity.this, getString(R.string.hint),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(LOG_TAG, "This is called when there is an Internet connection and a new search word.");
                        // Hide the empty state text
                        mEmptyView.setVisibility(View.GONE);
                        // Start the AsyncTask to fetch the books data
                        new BookListAsyncTask().execute(BOOK_URL_BASE + searchWord + MAX_RESULTS);
                        Log.e(LOG_TAG, "Show the URL with the user input: " + BOOK_URL_BASE
                                + searchWord);
                    }
                } else {
                    Log.e(LOG_TAG, "This is called when there is NO Internet connection.");
                    // First, hide loading indicator so error will be visible
                    loadingIndicator.setVisibility(View.GONE);
                    bookListView.setVisibility(View.GONE);
                    //Show the empty state with no connection error message
                    mEmptyView.setVisibility(View.VISIBLE);
                    //Update empty state with no connection error message
                    mEmptyView.setText(R.string.no_connection);
                }
            }
        });
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and
     * then update the UI with the list of books in the response.
     * <p>
     * We''ll only override two of the methods of AsyncTask doInBackground() and onPostExecute()
     * The doInBackground() runs on a background thread, so it can run long running code
     * (like network activity), without interfering with the responsiveness of the app.
     * The onPostExecute() is passed the result of the doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class BookListAsyncTask extends AsyncTask<String, Void, List<Book>> {

        /**
         * This method runs on the UI thread before doInBackground().
         * It shows the progress bar when the internet connection is delayed or slow.
         */
        @Override
        protected void onPreExecute() {
            Log.e(LOG_TAG, "When the onPreExecute is called?");
            // Show loading indicator if the Internet Connection is delayed or slow.
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Book}s as the result.
         *
         * @return {@link Book}s as the result
         */
        @Override
        protected List<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Book> result = UtilsQuerry.fetchBookData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First clears out the adapter, to get get rid of books data from a previous
         * query to Google Books API. Then updates the adapter with the new list of books,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<Book> books) {
            // First, hide loading indicator so error will be visible
            loadingIndicator.setVisibility(View.GONE);

            // Clear the adapter of previous  data
            mAdapter.clear();

            // If there is a valid list of {@link Book}s, then add to the adapter's
            // data set. This will trigger the ListView to update.
            if (books != null && !books.isEmpty()) {
                mAdapter.addAll(books);
            } else {
                //Show the empty state with no connection error message
                mEmptyView.setVisibility(View.VISIBLE);
                //Update empty state text
                mEmptyView.setText(R.string.no_connection);
            }
        }
    }}