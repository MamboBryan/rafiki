package com.mambo.rafiki.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.mambo.rafiki.BuildConfig;
import com.mambo.rafiki.data.entities.Location;
import com.mambo.rafiki.data.entities.User;

public class SharedPrefsUtil {

    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    public static final String USER = "user";
    public static final String GENDER = "gender";
    public static final String LOCATION = "location";

    public static final String SETTINGS_SAVING = "saving_online";
    public static final String SETTINGS_LOGIN = "login";
    public static final String SETTINGS_SETUP = "setup";

    private static SharedPrefsUtil prefsUtil;
    private static SharedPreferences sharedPreferences;

    private SharedPrefsUtil(Application application) {
        sharedPreferences = application.getSharedPreferences(PACKAGE_NAME,
                Context.MODE_PRIVATE);
    }

    // singleton pattern for instance of the class
    public static SharedPrefsUtil getInstance(Application application) {
        if (prefsUtil == null) {
            prefsUtil = new SharedPrefsUtil(application);
        }
        return prefsUtil;
    }

    /**
     * Completely clears all user details in the app
     */
    public void clearPreferences() {
        sharedPreferences.edit().clear().apply();
    }

    /**
     * Stores user has seen app features in to shared preferences
     */
    public void saveOffline(boolean isSavedOffline) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SETTINGS_SAVING, isSavedOffline);
        editor.apply();
    }

    /**
     * Stores user has seen app features in to shared preferences
     */
    public void setUpIsCompleted() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SETTINGS_SETUP, true);
        editor.apply();
    }

    public boolean isSetupCompleted() {
        return sharedPreferences.getBoolean(SETTINGS_SETUP, false);
    }

    /**
     * Stores user has seen app features in to shared preferences
     */
    public void setUserIsLoggedIn() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SETTINGS_LOGIN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(SETTINGS_LOGIN, false);
    }

    public boolean isUser(String userID) {

        String currentUser = sharedPreferences.getString(USER, null);
        return userID.equals(currentUser);

    }

    /**
     * Stores user has logged in to shared preferences
     */
    public void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(USER, null);
    }

    /**
     * Stores user has logged in to shared preferences
     */
    public void setUserGender(String gender) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GENDER, gender);
        editor.apply();

    }

    public String getUserGender() {
        return sharedPreferences.getString(GENDER, "N/A");
    }

    /**
     * Stores user has logged in to shared preferences
     */
    public void setUserLocation(Location location) {

        String json = new Gson().toJson(location);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCATION, json);
        editor.apply();

    }

    public Location getUserLocation() {

        String json = sharedPreferences.getString(LOCATION, null);

        if (json != null)
            return new Gson().fromJson(json, Location.class);

        return null;
    }

    public void storeUserData(User user) {

        String json = new Gson().toJson(user);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER, json);
        editor.apply();

    }

    public User getUserData() {

        String json = sharedPreferences.getString(USER, null);

        if (json != null)
            return new Gson().fromJson(json, User.class);

        return null;
    }
}
