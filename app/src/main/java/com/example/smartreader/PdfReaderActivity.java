package com.example.smartreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;
import java.util.Objects;

public class PdfReaderActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    public static final String TAG = "PDF_READER_ACTIVITY";

    private PDFView pdfView;
    private Integer pageNumber = 0;
    private String pdfFileName;
    private String book_name;
    private TextView headingText;

    private String parsedText;

    mTextSpeaker mSpeaker;
    SearchingWord wordSearch;

    private LinearLayout searchLayout, tv_layout, showSearchTextResultsLayout, searchResultLayout, buttonLayout;
    private EditText searchEditText;
    private String toFindString;
    private TextView searchTextView;
    private int currentPos;
    private int countOfSearch;

    private ImageButton play;
    private SeekBar speedSeekBar;

    private final int maxProgress = 5;
    private int pro_gress = 1;

    public static final int EDITOR_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        book_name = Objects.requireNonNull(getIntent().getExtras()).getString("extraString");

        //setting header...
        headingText = findViewById(R.id.tv_header);
        headingText.setText(book_name);

        //reference of pdf from api...
        pdfView = (PDFView) findViewById(R.id.pdfView);
        displayFromAsset(book_name);

        mSpeaker = new mTextSpeaker(this);

        searchLayout = findViewById(R.id.searchLayout);
        tv_layout = findViewById(R.id.tv_layout);
        searchEditText = findViewById(R.id.searchEditText);
        showSearchTextResultsLayout = findViewById(R.id.showSearchResultsLayout);
        searchTextView = findViewById(R.id.searchTextView);
        searchResultLayout = findViewById(R.id.showSearchResultsLayout);
        buttonLayout = findViewById(R.id.buttonLinearLayout);

        wordSearch = new SearchingWord();

        play = findViewById(R.id.play);

        speedSeekBar = findViewById(R.id.speedSeekBar);

        play.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                speedSeekBar.setVisibility(View.VISIBLE);

                Toast.makeText(PdfReaderActivity.this, "Speed = " + String.valueOf(getConvertableValue(pro_gress) + "x")
                        , Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        speedSeekBar.setMax(maxProgress);
        speedSeekBar.setProgress(pro_gress);
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro_gress = progress;
                mSpeaker.setSpeedOfSpeaking(getConvertableValue(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(PdfReaderActivity.this, "Speed = " + String.valueOf(getConvertableValue(pro_gress) + "x")
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float getConvertableValue(int progress) {
        float floatVal = 0.0f;
        floatVal = .25f * progress + 0.75f;
        return floatVal;
    }

    //for pdf..
    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(pdfFileName)
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    //from pdf..
    @Override
    public void onPageChanged(int page, int pageCount) {

        //checking for the display
        if (buttonLayout.getVisibility() == View.GONE) {
            searchResultLayout.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);
        }
        //setting previous check null
        if (!(wordSearch.position.isEmpty()))
            wordSearch.setItNull();

        pageNumber = page;
        reader(page + 1);  //extracting

        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    //from i-read
    private void reader(int curr_page) {
        try {
            parsedText = "";
            com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(getAssets().open(pdfFileName));
            parsedText = " " + parsedText + " " + PdfTextExtractor.getTextFromPage(reader, curr_page).trim() + " \n ";
            //Log.i("info", parsedText);
            //Extracting the content from the different pages
            reader.close();
        } catch (Exception e) {
            Log.i("info", e.toString());
        }

    }

    //from pdf..
    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    //from pdf..
    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    //play pause button.....
    public void playResults(View view) {
        speedSeekBar.setVisibility(View.GONE);
        mSpeaker.speakThis(parsedText);
    }

    public void pauseResults(View view) {
        mSpeaker.nowStop();
    }


    //adding note from pdf.....
    public void addingNote(View view) {
        Intent intent = new Intent(PdfReaderActivity.this, EditNote.class);

        intent.putExtra("StringfromExternal", book_name + "- Page: " + (pageNumber + 1) + "\n\n");

        //showing keyboard..
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    //Shutting down the tts.....
    @Override
    public void onDestroy() {
        mSpeaker.nowDestroy();
        super.onDestroy();
    }

    //searching browser.....
    public void searchBrowser(View view) {
        Intent searchIntent = new Intent(PdfReaderActivity.this, WebViewActivity.class);
        startActivity(searchIntent);
    }

    //showing search options....
    public void showSearchOptions(View view) {
        searchLayout.setVisibility(View.VISIBLE);
        tv_layout.setVisibility(View.GONE);

        searchEditText.requestFocus();

        //showing keyboard..
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    public void searchProcess(View view) {

        reader(pageNumber + 1);
        wordSearch.setItNull();

        //hiding keyboard....
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        searchLayout.setVisibility(View.GONE);
        toFindString = searchEditText.getText().toString().trim().toLowerCase();
        searchEditText.setText("");
        tv_layout.setVisibility(View.VISIBLE);

        showSearchTextResultsLayout.setVisibility(View.VISIBLE);

        searchResultLayout.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.GONE);

        countOfSearch = wordSearch.afterSearch(parsedText, toFindString);
        currentPos = 1 % (countOfSearch + 1);

        searchTextView.setText(toFindString + ", at - " + String.valueOf(currentPos) + "(" + String.valueOf(countOfSearch) + ")");
    }

    public void nextPosition(View view) {
        if (currentPos < countOfSearch) {
            currentPos += 1;
            searchTextView.setText(toFindString + ", at - " + String.valueOf(currentPos) + "(" + String.valueOf(countOfSearch) + ")");
        }
    }

    public void selectionString(View view) {

        if (currentPos > 0) {
            parsedText = wordSearch.getSelectedString(currentPos);
        }
        searchResultLayout.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.VISIBLE);
    }
}
