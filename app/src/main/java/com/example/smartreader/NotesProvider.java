package com.example.smartreader;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NotesProvider extends ContentProvider {

    //globally unique string...
    public static final String AUTHORITY = "com.example.smartreader.notesprovider";

    //represents the entire data set...
    public static final String BASE_PATH = "notes";

    //identifies the content provider...
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    //asking for the data...
    public static final int NOTES = 1;
    public static final int NOTES_ID = 2;

    //checking which operation is to be executed
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        //requesting particular note
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID);
    }

    SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DatabaseHelper helper = new DatabaseHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        if (uriMatcher.match(uri) == NOTES_ID) {
            selection = DatabaseHelper.NOTES_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DatabaseHelper.TABLE_NOTES, DatabaseHelper.ALL_COLLUMS, selection, null
                , null, null, DatabaseHelper.NOTES_CREATED + " DESC");

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long id = database.insert(DatabaseHelper.TABLE_NOTES, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DatabaseHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DatabaseHelper.TABLE_NOTES, values, selection, selectionArgs);
    }
}
