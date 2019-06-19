package com.notes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.notes.MainActivity;

public class LoginPrefs extends AppCompatActivity {

    private static final String PREF_NAME = "prefs";
    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String IS_LOGGED_IN = "user";
    private static final String UID = "uid";

    Context context;

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public LoginPrefs(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    public String getEmail() {
        return pref.getString(EMAIL, null);
    }

    public String getName() {
        return pref.getString(NAME, null);
    }

    public String getUID() {
        return pref.getString(UID, null);
    }

    public Boolean isLoggedIn() { return  pref.getBoolean(IS_LOGGED_IN, false); }

    public void logoutUser(GoogleSignInClient googleSignInClient, DBHelper db) {
        googleSignInClient.signOut();
        googleSignInClient.signOut();
        editor.clear();
        editor.commit();
        db.clearDB();

        context.startActivity(new Intent(context, MainActivity.class));
        ((Activity) context).finish();

    }

    public void createUser(String email, String name, String uid) {
        editor.putString(EMAIL, email);
        editor.putString(NAME, name);
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(UID, uid);
        editor.commit();
    }
}
