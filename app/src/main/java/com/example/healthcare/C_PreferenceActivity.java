package com.example.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

/**
 * Activity to handle the "Preferences" command
 */
public class C_PreferenceActivity extends C_AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private C_PeriodicalDatabase dbMain;

    /**
     * Called when activity starts
     */
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();
        assert context != null;

        // We get/store preferences in the database
        dbMain = new C_PeriodicalDatabase(context);

        addPreferencesFromResource(R.xml.c_preferences);
        initSummary(getPreferenceScreen());

        // Add validation for period length
        findPreference("period_length").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int value;
                        try {
                            value = Integer.parseInt(newValue.toString());
                        } catch (NumberFormatException e) {
                            value = 0;
                        }

                        if (value < 5) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.invalid_def_length),
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        return true;
                    }
                });

        // Add validation for luteal length
        findPreference("luteal_length").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int value;
                        try {
                            value = Integer.parseInt(newValue.toString());
                        } catch (NumberFormatException e) {
                            value = 0;
                        }

                        if (value < 7) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.invalid_home_length),
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        return true;
                    }
                });

        // Add validation for cycle length filter
//        findPreference("maximum_cycle_length").setOnPreferenceChangeListener(
//                new Preference.OnPreferenceChangeListener() {
//
//                    @Override
//                    public boolean onPreferenceChange(Preference preference, Object newValue) {
//                        int value;
//                        try {
//                            value = Integer.parseInt(newValue.toString());
//                        } catch (NumberFormatException e) {
//                            value = 0;
//                        }
//
//                        if (value < 60) {
//                            Toast.makeText(context,
//                                    getResources().getString(R.string.invalid_maximum_cycle_length),
//                                    Toast.LENGTH_SHORT).show();
//                            return false;
//                        }
//
//                        return true;
//                    }
//                });

        // Activate "back button" in Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Called when the activity starts interacting with the user
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called when activity pauses
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Handle preference changes
     */
    @SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        C_PreferenceUtils preferenceUtils = new C_PreferenceUtils(sharedPreferences);
        Preference pref = findPreference(key);

        updatePrefSummary(pref);

        // Store setting to database
        switch (key) {
            case "period_length":
                dbMain.setOption(key, preferenceUtils.getInt(key, dbMain.DEFAULT_PERIOD_LENGTH));
                break;
            case "luteal_length":
                dbMain.setOption(key, preferenceUtils.getInt(key, dbMain.DEFAULT_LUTEAL_LENGTH));
                break;
            case "startofweek":
                dbMain.setOption(key, preferenceUtils.getInt(key, dbMain.DEFAULT_START_OF_WEEK));
                break;
            case "maximum_cycle_length":
                dbMain.setOption(key, preferenceUtils.getInt(key, dbMain.DEFAULT_CYCLE_LENGTH));
                break;
            case "direct_details":
                dbMain.setOption(key, preferenceUtils.getBoolean(key, dbMain.DEFAULT_DIRECT_DETAILS));
                break;
            case "show_cycle":
                dbMain.setOption(key, preferenceUtils.getBoolean(key, dbMain.DEFAULT_SHOW_CYCLE));
                break;
        }
    }

    /**
     * Set initial summary texts
     */
    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    /**
     * Update summary text for a preference
     */
    private static void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().contains("assword")) {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
    }

    /**
     * Handler for ICS "home" button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Home icon in action bar clicked, then close activity
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}