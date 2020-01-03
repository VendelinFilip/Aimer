package com.example.android.aimer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;


public class BookAdapter extends ArrayAdapter<book>  {
    public  BookAdapter(Context context, List<book> books){
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    //Setting the design that should be used in every one list item.
                    R.layout.books_activity, parent, false);
        }

        //Getting the position of current item.
        final book currentBook = getItem(position);

        //Finding the right TextView.
        TextView bookNameTextView = (TextView)listItemView.findViewById(R.id.name_text_view);
        //Setting its text to variable gotten from function (of data type book) getBookName called on current item.
        bookNameTextView.setText(currentBook.getBookName());

        //Finding the right TextView.
        TextView authorNameTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
        //Setting its text to variable gotten from function (of data type book) getAuthorsNames called on current item.
        authorNameTextView.setText(currentBook.getAuthorsNames());

        //Finding the right TextView.
        TextView bookDescriptionTextView = (TextView) listItemView.findViewById(R.id.description_text_view);
        //Setting its text to variable gotten from function (of data type book) getBookDescription called on current item.
        bookDescriptionTextView.setText(currentBook.getBookDescription());

        //Finding the right LinearLayout.
        LinearLayout mainLinearLayout = (LinearLayout) listItemView.findViewById(R.id.item_linear_layout);

        //Setting onClickListener. So when clicked it opens new web tab with that book
        mainLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating new web intent with url gotten from function (of data type book) getUrl called on current item.
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.getUrl()));
                //Starting activity with that intent.
                getContext().startActivity(browserIntent);
            }
        });


        // Return the whole list item layout (containing 3 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}
