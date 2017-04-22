package com.example.d062589.partylive.Utils;

import android.content.Context;

import com.example.d062589.partylive.Models.User;

public class PrefUtils {

    private static PrefUtils instance = null;
    private Context context;

    private PrefUtils(Context context) {
        this.context = context;
    }

    public static synchronized PrefUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PrefUtils(context);
        } else {
            instance.context = context;
        }
        return instance;
    }

    public void setCurrentUser(User currentUser) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "user_prefs", 0);
        complexPreferences.putObject("current_user_value", currentUser);
        complexPreferences.commit();
    }

    public User getCurrentUser() {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "user_prefs", 0);
        User currentUser = complexPreferences.getObject("current_user_value", User.class);
        return currentUser;
    }

    public void clearCurrentUser() {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "user_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }
}
