package com.prashant.mynotes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prashant.mynotes.model.Note;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private FirebaseUser user;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        user = FirebaseAuth.getInstance().getCurrentUser();
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


    public void insertNote(String text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Note.NOTE, text);
        db.insert(Note.TABLE_NAME, null, contentValues);
    }

    public void insertNote(int _id, String text, String timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("REPLACE INTO " + Note.TABLE_NAME + " ('id', 'note', 'timestamp') VALUES (" + _id + ", '" + text + "', '" + timestamp + "')");
    }


    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + Note.TABLE_NAME, null);
        return res;
    }

    public Cursor getNote(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + Note.TABLE_NAME + " WHERE " +
                Note.ID + "=?", new String[]{id});
        return res;
    }


    public void deleteNote(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.ID + "=?", new String[]{id});

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("notes").child(id);
        ref.setValue(null);
    }
}
