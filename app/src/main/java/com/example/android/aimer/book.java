package com.example.android.aimer;

public class book {

    private String mBookName;
    private String mAuthorsNames;
    private String mBookDescription;
    private String mBookUrl;

    //This sets what you have to put in when creating new book type variable
    public book(String bookName, String authorsNames, String bookDescription, String bookUrl){
        mBookName = bookName;
        mAuthorsNames = authorsNames;
        mBookDescription = bookDescription;
        mBookUrl = bookUrl;
    }

    /**
     * Method that returns the bookName associated with that item.
     * @return
     */
    public String getBookName(){ return mBookName;}

    /**
     * Method that returns the authorsNames associated with that item.
     * @return
     */
    public String getAuthorsNames() { return mAuthorsNames;}

    /**
     * Method that returns the bookDescription associated with that item.
     * @return
     */
    public String getBookDescription() { return  mBookDescription;}

    /**
     * Method that returns the bookUrl associated with that item.
     * @return
     */
    public String getUrl() { return mBookUrl;}

}
