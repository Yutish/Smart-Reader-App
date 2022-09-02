package com.example.smartreader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class NotesList extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = NotesList.class.getSimpleName();

    private ListView notesList;
    private SimpleCursorAdapter cursorAdapter;
    private FloatingActionButton addingNotesFab;

    //for checking the intent result....
    public static final int EDITOR_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        //cursor adapter.....
        String[] from = {DatabaseHelper.NOTES_TEXT};
        int[] to = {R.id.noteTitle};

        cursorAdapter = new SimpleCursorAdapter(this, R.layout.item_notes_list, null
                , from, to, 0);

        notesList = findViewById(R.id.notesList);
        notesList.setAdapter(cursorAdapter);

        //on clicking the list.....
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotesList.this, EditNote.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);

                //showing keyboard..
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);

        addingNotesFab = findViewById(R.id.addingNotesFab);

        //fab on click....new note addition....
        addingNotesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesList.this, EditNote.class);

                //showing keyboard..
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

}
