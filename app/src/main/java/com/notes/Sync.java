package com.notes;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sync {

    Context context;
    DBHelper db;
    LoginPrefs sharedPrefs;

    public Sync(Context context) {
        this.context = context;
        db = new DBHelper(context);
        sharedPrefs = new LoginPrefs(context);
    }

    public void syncNotes() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(sharedPrefs.getUID());
        Cursor cursor = db.getAllNotes();

//        Save Local DB to Server
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int DB_ID = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Note.ID)));
                String DB_NOTE_TEXT = cursor.getString(cursor.getColumnIndex(Note.NOTE));
                String DB_TIMESTAMP = cursor.getString(cursor.getColumnIndex(Note.TIMESTAMP));

                Note note = new Note(DB_ID, DB_NOTE_TEXT, DB_TIMESTAMP);
                ref.child(String.valueOf(DB_ID)).setValue(note);

                cursor.moveToNext();
            }
        }


//        Sync from Server
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    Note note = dataSnapshot.getValue(Note.class);
                    db.insertNote(note.id,note.note);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}
