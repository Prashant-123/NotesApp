package com.notes;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.choota.dev.ctimeago.TimeAgo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.notes.Login.TAG;

public class MyNotes extends Fragment {
    public MyNotes() {}

    DBHelper db;
    LoginPrefs sharedPrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mynotes_fragment, container, false);
        db = new DBHelper(view.getContext());
        sharedPrefs = new LoginPrefs(view.getContext());

//        Sync sync = new Sync(getContext());
//        sync.syncNotes();

        db.insertNote("Note-1");

        Cursor cursor = db.getAllNotes();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int DB_ID = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Note.ID)));
                String DB_NOTE_TEXT = cursor.getString(cursor.getColumnIndex(Note.NOTE));
                String DB_TIMESTAMP = cursor.getString(cursor.getColumnIndex(Note.TIMESTAMP));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("IST"));
                try {
                    Date d = sdf.parse(DB_TIMESTAMP);
                    String dt = new TimeAgo().getTimeAgo(d);
                    Log.i(TAG, "onCreateView: " + dt);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                cursor.moveToNext();
            }
        }
        return view;
    }
}
