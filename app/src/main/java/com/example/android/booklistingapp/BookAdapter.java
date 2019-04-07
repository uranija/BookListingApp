package com.example.android.booklistingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class BookAdapter extends ArrayAdapter<Book> {


    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param books   A List of Book objects to display in a list
     */

    public BookAdapter(Context context, ArrayList<Book> books) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, books);

    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }


        // Get the {@link Book} object located at this position in the list
        Book currentBook = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.book_title);
        // Get the version name from the current Book object and
        // set this text on the name TextView
        titleTextView.setText(currentBook.getTitle());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView publisherTextView = (TextView) listItemView.findViewById(R.id.publisher);
        // Get the version number from the current Book object and
        // set this text on the number TextView
        publisherTextView.setText(currentBook.getPublisher());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView publishedDateTextView = (TextView) listItemView.findViewById(R.id.published_date);
        // Get the version number from the current Book object and
        // set this text on the number TextView
        publishedDateTextView.setText(currentBook.getPublishedDate());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView categoriesTextView = (TextView) listItemView.findViewById(R.id.categories);
        // Get the version number from the current Book object and
        // set this text on the number TextView
        categoriesTextView.setText(currentBook.getCategories());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView descriptionTextView = (TextView) listItemView.findViewById(R.id.description);
        // Get the version number from the current Book object and
        // set this text on the number TextView
        descriptionTextView.setText(currentBook.getDescription());


        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        // Get the version number from the current Book object and
        // set this text on the number TextView
        authorTextView.setText(currentBook.getBookAuthor());


        // Find the ImageView dedicated to the bookcover link
        ImageView coverImageView = (ImageView) listItemView.findViewById(R.id.cover_ImageView);
        String thumbnailLink = currentBook.getmThumbnailLink();


        AsyncTask<ImageView, Void, Bitmap> imageViewVoidBitmapAsyncTask = new DownloadImagesTask(thumbnailLink)
                .execute(coverImageView);

        // Return the whole list item layout (containing 6 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {
        private ImageView imageView;
        private String url;

        public DownloadImagesTask(String url) {
            this.url = url;
        }


        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image(url);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        private Bitmap download_Image(String urlParam) {

            Bitmap bmp = null;
            try {
                URL url = new URL(urlParam);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;
            } catch (Exception e) {
            }
            return bmp;
        }
    }
}