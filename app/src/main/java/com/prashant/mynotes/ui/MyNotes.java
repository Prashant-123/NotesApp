package com.prashant.mynotes.ui;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prashant.mynotes.MainActivity;
import com.prashant.mynotes.R;
import com.prashant.mynotes.adapter.NotesAdapter;
import com.prashant.mynotes.utils.DBHelper;
import com.prashant.mynotes.utils.Sync;

public class MyNotes extends Fragment {
    private DBHelper db;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private Animation fab_open;
    private NotesAdapter adapter;
    private MaterialButton logout_btn;
    private LottieAnimationView sync;
    private LinearLayout empty_list;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    public MyNotes() {
    }

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

                sync();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(ItemClickListener());

        sync();

        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        RecyclerView.AdapterDataObserver emptyObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (Sync.notes.size() <= 0) empty_list.setVisibility(View.VISIBLE);
                else empty_list.setVisibility(View.INVISIBLE);
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

    public void sync() {
        try {
            Sync sync = new Sync(getContext(), adapter);
            sync.syncNotes();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Sync Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void _AddNewNote(final View view) {
        fab.startAnimation(fab_open);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_note, null);
        dialogBuilder.setView(dialogView);

        final EditText addNoteEditText = dialogView.findViewById(R.id.add_note_text);
        final AlertDialog alertDialog = dialogBuilder.create();

        dialogView.findViewById(R.id.add_note_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String note = addNoteEditText.getText().toString().trim();
                    if (!note.isEmpty()) {
                        db.insertNote(note);
                        alertDialog.dismiss();
                        sync();
                    } else
                        Toast.makeText(getContext(), "Enter some text", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Insert Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
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
                        db.clearDB();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public View.OnClickListener ItemClickListener() {
        View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                final int position = viewHolder.getAdapterPosition();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.add_note, null);
                dialogBuilder.setView(dialogView);


                final AlertDialog alertDialog = dialogBuilder.create();
                ImageButton delete_btn = dialogView.findViewById(R.id.delete_btn);
                TextView title = dialogView.findViewById(R.id.add_note_title);
                EditText noteText = dialogView.findViewById(R.id.add_note_text);
                delete_btn.setVisibility(View.VISIBLE);
                noteText.setText(Sync.notes.get(position).note);

                sync();

                MaterialButton udpateButton = dialogView.findViewById(R.id.add_note_btn);
                udpateButton.setText("Update Note");
                title.setText("Update Note");

                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

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
                                try {
                                    db.deleteNote(String.valueOf(Sync.notes.get(position).id));
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Delete Error", Toast.LENGTH_SHORT).show();
                                }

                                sync();
                            }
                        });
                    }
                });

                udpateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText etNote = dialogView.findViewById(R.id.add_note_text);
                        String note = String.valueOf(etNote.getText());
                        if (!note.isEmpty()) {

                            if (Sync.notes.get(position).getNote().equals(note)) {
                                alertDialog.dismiss();
                                Toast.makeText(getContext(), "No changes made", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                db.insertNote(Sync.notes.get(position).id, etNote.getText().toString(), Sync.notes.get(position).timestamp);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Update Error", Toast.LENGTH_SHORT).show();
                            }

                            alertDialog.dismiss();
                            sync();

                        } else
                            Toast.makeText(getContext(), "Enter some text", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialog.show();
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
