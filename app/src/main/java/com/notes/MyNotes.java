package com.notes;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choota.dev.ctimeago.TimeAgo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.notes.Login.TAG;

public class MyNotes extends Fragment {
    public MyNotes() {}

    DBHelper db;
    LoginPrefs sharedPrefs;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private Animation fab_open;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mynotes_fragment, container, false);
        db = new DBHelper(view.getContext());
        sharedPrefs = new LoginPrefs(view.getContext());
        recyclerView = view.findViewById(R.id.rv);
        fab = view.findViewById(R.id.add_not_fab);;
        fab_open = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_open);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new NotesAdapter(view.getContext()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.startAnimation(fab_open);
                View view = getLayoutInflater().inflate(R.layout.add_note, null);
                BottomSheetDialog dialog = new BottomSheetDialog(getContext(),R.style.AppBottomSheetDialogTheme); // Style here
                dialog.setContentView(view);
                dialog.show();
            }
        });

//        Sync sync = new Sync(getContext());
//        sync.syncNotes();

//        db.insertNote("Note-1");

        return view;
    }
}
