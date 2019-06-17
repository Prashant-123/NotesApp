package com.notes.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.notes.MainActivity;
import com.notes.R;
import com.notes.adapter.NotesAdapter;
import com.notes.utils.DBHelper;
import com.notes.utils.Sync;

public class MyNotes extends Fragment {
    public MyNotes() {}

    private DBHelper db;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private Animation fab_open;
    private NotesAdapter adapter;
    private MaterialButton logout_btn;
    private LottieAnimationView sync;
    private LinearLayout empty_list;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.mynotes_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        _AuthListener();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        db = new DBHelper(view.getContext());
        recyclerView = view.findViewById(R.id.rv);
        fab = view.findViewById(R.id.add_not_fab);
        logout_btn = view.findViewById(R.id.logout_btn);
        sync = view.findViewById(R.id.sync);
        empty_list = view.findViewById(R.id.empty_list);
        fab_open = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_open);
        adapter = new NotesAdapter(view.getContext(), Sync.notes);

        empty_list.setVisibility(View.VISIBLE);

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sync.playAnimation();
                Sync sync = new Sync(getContext(), adapter);
                sync.syncNotes();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(ItemClickListener());

        Sync sync = new Sync(getContext(), adapter);
        sync.syncNotes();

        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        RecyclerView.AdapterDataObserver emptyObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (Sync.notes.size() <= 0) empty_list.setVisibility(View.VISIBLE); else empty_list.setVisibility(View.INVISIBLE);
            }
        };
        adapter.registerAdapterDataObserver(emptyObserver);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _AddNewNote(view);
            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout(view);
            }
        });

        return view;
    }

    public void _AddNewNote(final View view) {
        fab.startAnimation(fab_open);
        final View bottom_sheet = getLayoutInflater().inflate(R.layout.add_note, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext(),R.style.AppBottomSheetDialogTheme); // Style here
        dialog.setContentView(bottom_sheet);
        dialog.show();


        bottom_sheet.findViewById(R.id.add_note_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etNote = bottom_sheet.findViewById(R.id.add_note_text);
                String note = String.valueOf(etNote.getText());
                if (!note.isEmpty()) {
                    db.insertNote(etNote.getText().toString());
                    dialog.dismiss();
                    Sync sync = new Sync(view.getContext(), adapter);
                    sync.syncNotes();

                } else Toast.makeText(getContext(), "Enter some text", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Logout(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(true);
        builder.setTitle("Are you sure to Log Out?");
        builder.setPositiveButton("Log-Out",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public View.OnClickListener ItemClickListener(){
        View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                final int position = viewHolder.getAdapterPosition();

                final View edit_bottom_sheet = getLayoutInflater().inflate(R.layout.add_note, null);
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.AppBottomSheetDialogTheme); // Style here
                bottomSheetDialog.setContentView(edit_bottom_sheet);

                ImageButton delete_btn = edit_bottom_sheet.findViewById(R.id.delete_btn);
                TextView title = edit_bottom_sheet.findViewById(R.id.add_note_title);
                EditText noteText = edit_bottom_sheet.findViewById(R.id.add_note_text);
                delete_btn.setVisibility(View.VISIBLE);
                noteText.setText(Sync.notes.get(position).note);

                Sync sync = new Sync(getContext(), adapter);
                sync.syncNotes();

                MaterialButton submit = edit_bottom_sheet.findViewById(R.id.add_note_btn);
                submit.setText("Update Note");
                title.setText("Update Note");

                bottomSheetDialog.show();

                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setCancelable(false);
                        builder.setView(LayoutInflater.from(view.getContext()).inflate(R.layout.delete_note, null));
                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        TextView note;
                        MaterialButton delete, cancel;
                        note = dialog.findViewById(R.id.preview_text);
                        delete = dialog.findViewById(R.id.delete_btn);
                        cancel = dialog.findViewById(R.id.cancel_btn);
                        note.setText(Sync.notes.get(position).note);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.deleteNote(String.valueOf(Sync.notes.get(position).id));
                                Sync sync = new Sync(view.getContext(), adapter);
                                sync.syncNotes();
                                dialog.dismiss();
                                bottomSheetDialog.dismiss();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText etNote = edit_bottom_sheet.findViewById(R.id.add_note_text);
                        String note = String.valueOf(etNote.getText());
                        if (!note.isEmpty()) {
                            db.insertNote(Sync.notes.get(position).id, etNote.getText().toString(), Sync.notes.get(position).timestamp);
                            bottomSheetDialog.dismiss();
                            Sync sync = new Sync(view.getContext(), adapter);
                            sync.syncNotes();

                        } else Toast.makeText(getContext(), "Enter some text", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        return onItemClickListener;
    }

    public void _AuthListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                } else {
                    Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                    getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            }
        };
    }
}
