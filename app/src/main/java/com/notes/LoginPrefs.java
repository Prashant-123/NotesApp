package com.notes;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class LoginPrefs {

    private static final String PREF_NAME = "prefs";
    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String IS_LOGGED_IN = "user";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public LoginPrefs(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    public String getEmail() {
        return pref.getString(EMAIL, null);
    }

    public String getName() {
        return pref.getString(NAME, null);
    }

    public Boolean isLoggedIn() { return  pref.getBoolean(IS_LOGGED_IN, false); }

    public void logoutUser(GoogleSignInClient googleSignInClient) {
        googleSignInClient.signOut();
        editor.clear();
        editor.commit();
    }

    public void createUser(String email, String name) {
        editor.putString(EMAIL, email);
        editor.putString(NAME, name);
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.commit();
    }
}
