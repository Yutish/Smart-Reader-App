package com.example.smartreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants of DB...
    public static final String DATABASE_NAME = "notes.db";
    public static final int DATABASE_VERSION = 1;

    //Constants of Table...
    public static final String TABLE_NOTES = "notes";
    public static final String NOTES_ID = "_id";
    public static final String NOTES_TEXT = "noteText";
    public static final String NOTES_CREATED = "noteCreated";

    //SQL to create table
    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NOTES + " ("
            + NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NOTES_TEXT + " TEXT, "
            + NOTES_CREATED + " TEXT default CURRENT_TIMESTAMP"
            + ")";


    public static final String[] ALL_COLLUMS = {NOTES_ID, NOTES_TEXT, NOTES_CREATED};

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
}
