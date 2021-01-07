package com.example.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Preference utilities
 */
class PreferenceUtils {

    /**
     * Private reference to shared preferences
     */
    private final SharedPreferences preferences;

    /**
     * Constructor, will try to create/open a writable database
     *
     * @param context Application context
     */
    PreferenceUtils(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Constructor, will use an existing shared preference object
     *
     * @param sharedPreferences Shared preferences to be used
     */
    PreferenceUtils(SharedPreferences sharedPreferences) {
        this.preferences = sharedPreferences;
    }

    /**
     * Get integer preference
     *
     * @param key      Name of the preference
     * @param defValue Default value
     * @return The preference
     */
    public int getInt(String key, Integer defValue) {
        int result;

        try {
            result = Integer.parseInt(preferences.getString(key, defValue.toString()));
        } catch (NumberFormatException | ClassCastException e) {
            result = defValue;
        }

        return result;
    }

    /**
     * Get string preference
     *
     * @param key      Name of the preference
     * @param defValue Default value
     * @return The preference
     */
    public String getString(String key, String defValue) {
        String result;

        try {
            result = preferences.getString(key, defValue);
        } catch (ClassCastException e) {
            result = defValue;
        }

        return result;
    }

    /**
     * Get bool preference
     *
     * @param key      Name of the preference
     * @param defValue Default value
     * @return The preference
     */
    public boolean getBoolean(String key, boolean defValue) {
        boolean result;

        try {
            result = preferences.getBoolean(key, defValue);
        } catch (ClassCastException e) {
            result = defValue;
        }

        return result;
    }

    /**
     * Get an editor for the shared preferences
     *
     * @return Editor for the shared preferences
     */
    public SharedPreferences.Editor edit() {
        return preferences.edit();
    }
}