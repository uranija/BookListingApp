package com.example.android.booklistingapp;


public class Book {

    // Book title
    private String mBookTitle;

    // Book image
    private String mPublisher;

    // Book author
    private StringBuilder mBookAuthor;

    // URL of the book;
    private String mUrl;

    // Book year
    private String mPublishedDate;

    /** Book Categories */
    private String mCategories;

    /** Description */
    private String mDescription;

    /** Thumbnail Link */
    private String mThumbnailLink;

    /**
     * Create a new constructor for Book object.
     *
     * @param title    is the title of the book
     * @param author   is the names of the authors of the book
     * @param publisher is the publisher of the book
     * @param url      is the url of the book
     */

    public Book(String title, StringBuilder author, String publisher, String url, String publishedDate, String categories, String description, String thumbnailLink) {
        mBookTitle = title;
        mBookAuthor = author;
        mPublisher = publisher;
        mUrl = url;
        mPublishedDate= publishedDate;
        mCategories=categories;
        mDescription=description;
        mThumbnailLink=thumbnailLink;
    }

    //Getter methods
    public String getTitle() {
        return mBookTitle;
    }

    public StringBuilder getBookAuthor() {
        return mBookAuthor;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

    public String getCategories() { return mCategories;
    }
    public String getDescription() { return mDescription;
    }
    public String getmThumbnailLink() { return mThumbnailLink;
    }


}




















