package com.example.smartreader;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

public class EditNote extends AppCompatActivity {

    private String action;
    private EditText notes;
    private String noteFilter;

    //old text if editing....
    private String oldText;
    private mTextSpeaker mSpeaker;

    //external text if called by external sources with in the app....
    String externalText;

    private ImageButton speakOut;
    private SeekBar speedSeekBarEditNote;

    private final int maxProgress = 5;
    private int pro_gress = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        notes = findViewById(R.id.notes);

        Intent intent = getIntent();

        //for getting the old text....
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        externalText = intent.getStringExtra("StringfromExternal");
        if (externalText != null) {
            notes.setText(externalText);
            notes.requestFocus();
        }

        if (uri == null) {
            action = Intent.ACTION_INSERT;
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DatabaseHelper.NOTES_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DatabaseHelper.ALL_COLLUMS, noteFilter, null, null);

            cursor.moveToFirst();

            oldText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTES_TEXT));

            notes.setText(oldText);
            notes.requestFocus();

            mSpeaker = new mTextSpeaker(EditNote.this);
        }

        speakOut = findViewById(R.id.speakOut);

        speedSeekBarEditNote = findViewById(R.id.speedSeekBarEditNote);

        speakOut.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                speedSeekBarEditNote.setVisibility(View.VISIBLE);
                Toast.makeText(EditNote.this, "Speed = " + String.valueOf(getConvertableValue(pro_gress))
                        , Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        speedSeekBarEditNote.setMax(maxProgress);
        speedSeekBarEditNote.setProgress(pro_gress);
        speedSeekBarEditNote.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                Toast.makeText(EditNote.this, "Speed = " + String.valueOf(getConvertableValue(pro_gress))
                        , Toast.LENGTH_SHORT).show();
            }
        });

    }

    private float getConvertableValue(int progress) {
        float floatVal = 0.0f;
        floatVal = .25f * progress + 0.75f;
        return floatVal;
    }

    //when editing finished....
    public void finishedEditing() {

        //new text after insertion.....
        String newText = notes.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;

            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                } else if (oldText == newText) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }

        }

        //ends the activity...
        finish();
    }

    //when deleting a note.....
    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        setResult(RESULT_OK);
        finish();
    }

    //when updating a note.....
    private void updateNote(String noteText) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTES_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        setResult(RESULT_OK);
    }

    //on insertion of a new note and addition to the database
    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTES_TEXT, noteText);

        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishedEditing();
        super.onBackPressed();
        //if from external source....
        if (externalText != null) {
            startActivity(new Intent(EditNote.this, NotesList.class));
            finish();
        }
    }

    //deleteNote Overloaded...
    public void deleteNote(View view) {
        hidingKeyboard();
        String inEditText = notes.getText().toString().trim();

        if (inEditText.length() > 0) {
            notes.setText("");
            finishedEditing();
        }

    }

    //for hiding the keyboard....
    private void hidingKeyboard() {

        View check = this.getCurrentFocus();
        if (check != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert im != null;
            im.hideSoftInputFromWindow(check.getWindowToken(), 0);
        }
    }

    //applying text to speech
    public void speakText(View view) {

        speedSeekBarEditNote.setVisibility(View.GONE);

        String textToSpeak = notes.getText().toString().trim();
        try {
            if (textToSpeak.length() > 0) {
                mSpeaker.speakThis(textToSpeak);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Save your note before this option", Toast.LENGTH_LONG).show();
        }
    }

    //for saving text
    public void saveText(View view) {
        hidingKeyboard();
        onBackPressed();
    }

    //for sharing the note to external source outside of the app
    public void shareText(View view) {
        hidingKeyboard();

        String textToShare = notes.getText().toString().trim();
        try {
            if (textToShare.length() > 0) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, textToShare);
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Save your note before this option", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSpeaker.nowStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeaker.nowDestroy();
    }
}
