package com.example.android.aimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Finding the right button
        Button searchButton = (Button) findViewById(R.id.searching_button);

        //Setting onClickListener on this button, so when it is clicked it opens new activity
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finding the right Edit Text.
                final EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
                //Getting the text from it.
                String searchedWord = searchEditText.getText().toString();
                Log.d("Wanted ", searchedWord);
                //Creating new intent that will display the booksActivity.class
                Intent bookListIntent = new Intent(getApplicationContext(), booksActivity.class);
                //Adding searchedWord (the text we typed into t hat Edit Text) to that intent
                bookListIntent.putExtra("searchedWord", searchedWord);
                //Starting that intent and sending searchedWord to that activity.
                startActivity(bookListIntent);
            }
        });
    }
}
