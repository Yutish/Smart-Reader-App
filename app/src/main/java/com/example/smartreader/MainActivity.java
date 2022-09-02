package com.example.smartreader;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.barteksc.pdfviewer.PDFView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private ListView bookListView;
    private ArrayAdapter adapter;
    private PDFView pdfView;
    private Integer pageNumber = 0;
    private String pdfFileName;

    private ImageButton cameraButton, notesButton, searchButton;

    //Hardcoded due to time shortage :P...
    private String[] my_books = {"Computer Networking A Top-Down Approach.pdf","Operating System Concepts.pdf", "The Hitchhiker’s Guide.pdf"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: " + getAssets().toString());
        bookListView = findViewById(R.id.bookListView);
        adapter = new ArrayAdapter(this, R.layout.item_book_list, R.id.textView, my_books);
        bookListView.setAdapter(adapter);

        cameraButton = findViewById(R.id.cameraButton);
        notesButton = findViewById(R.id.notesButton);
        searchButton = findViewById(R.id.searchButton);

        pdfView = findViewById(R.id.pdfView);

        //on item click listener.....
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, PdfReaderActivity.class);
                intent.putExtra("extraString", my_books[position]);
                startActivity(intent);

            }
        });

        //on click listeners for image buttons.....
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MainActivity.this, ScanningActivity.class);
                startActivity(cameraIntent);
            }
        });

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notesIntent = new Intent(MainActivity.this, NotesList.class);
                startActivity(notesIntent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(searchIntent);
            }
        });
    }

}
