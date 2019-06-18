package com.prashant.mynotes.utils;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prashant.mynotes.adapter.NotesAdapter;
import com.prashant.mynotes.model.Note;

import java.util.ArrayList;

public class Sync {

    public static ArrayList<Note> notes = new ArrayList<>();
    Context context;
    DBHelper db;
    NotesAdapter adapter;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public Sync(Context context, NotesAdapter adapter) {
        this.context = context;
        db = new DBHelper(context);
        this.adapter = adapter;
    }

    public void syncNotes() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("notes");
        Cursor cursor = db.getAllNotes();

        notes.clear();

//        Save Local DB to Server
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int DB_ID = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Note.ID)));
                String DB_NOTE_TEXT = cursor.getString(cursor.getColumnIndex(Note.NOTE));
                String DB_TIMESTAMP = cursor.getString(cursor.getColumnIndex(Note.TIMESTAMP));
                Note note = new Note(DB_ID, DB_NOTE_TEXT, DB_TIMESTAMP);
                notes.add(note);

                adapter.notifyDataSetChanged();
                ref.child(String.valueOf(DB_ID)).setValue(note);

                cursor.moveToNext();
            }
        }


//        Sync from Server
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notes.clear();
                if (dataSnapshot.getValue() != null)
                for (DataSnapshot dsp: dataSnapshot.getChildren()) {
                    Note note = dsp.getValue(Note.class);
                    notes.add(note);
                    db.insertNote(note.id,note.note, note.timestamp);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
