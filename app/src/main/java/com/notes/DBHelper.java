package com.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
        onCreate(db);
    }

    public void clearDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Note.TABLE_NAME);
    }


    public long insertNote(String text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Note.NOTE, text);
        return db.insert(Note.TABLE_NAME, null, contentValues);
    }

    public void insertNote(int _id, String text) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("REPLACE INTO " + Note.TABLE_NAME + " ('id', 'note') VALUES (" + _id + "," + "'" + text + "'" + ")");

    }


    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + Note.TABLE_NAME, null );
        return res;
    }


    public Cursor getNote(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + Note.TABLE_NAME + " WHERE " +
                Note.ID + "=?", new String[] { id } );
        return res;
    }


    public void deleteNote(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.ID + "=?", new String[]{id});
    }
}
