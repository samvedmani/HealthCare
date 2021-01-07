package com.example.healthcare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.C_PeriodicalDatabase.DayEntry;

import java.util.Calendar;
import java.util.Iterator;

/**
 * Activity to handle the "List" command
 */
public class C_ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    /**
     * Database for calendar data
     */
    private C_PeriodicalDatabase dbMain;

    /**
     * Called when activity starts
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final Context context = getApplicationContext();
        assert context != null;
        super.onCreate(savedInstanceState);

        int maximumcyclelength;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            maximumcyclelength = Integer.parseInt(preferences.getString("maximum_cycle_length", "183"));
        } catch (NumberFormatException e) {
            maximumcyclelength = 183;
        }

        // Set up database and string array for the list
        dbMain = new C_PeriodicalDatabase(context);
        dbMain.loadRawData();

        String[] entries = new String[dbMain.dayEntries.size()];
        java.text.DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(context);
        Iterator<DayEntry> dayIterator = dbMain.dayEntries.iterator();
        int pos = 0;
        DayEntry dayPrevious = null;
        DayEntry day = null;
        boolean isFirst = true;
        while (dayIterator.hasNext()) {
            if (isFirst) {
                isFirst = false;
            } else {
                dayPrevious = day;
            }
            day = dayIterator.next();

            entries[pos] = dateFormat.format(day.date.getTime());
            switch (day.type) {
                case DayEntry.PERIOD_START:
                    entries[pos] = entries[pos] + " \u2014 " + getString(R.string.event_covidperiodstart);
                    if (dayPrevious != null) {
                        // If we have a previous day, then update the previous
                        // days length description
                        Integer length = day.date.diffDayPeriods(dayPrevious.date);
                        if (length <= maximumcyclelength) {
                            entries[pos - 1] += "\n"
                                    + String.format(
                                    getString(R.string.event_covidperiodlength),
                                    length.toString());
                        } else {
                            entries[pos - 1] +=
                                    String.format("\n%s", getString(R.string.event_ignored));
                        }
                    }
                    break;
            }
            pos++;
        }
        // If we have at least one entry, update the last days length
        // description to "first entry"
        if (pos > 0) {
            entries[pos - 1] += "\n" + getString(R.string.event_periodfirst);
        }


        // Set custom view
        setContentView(R.layout.c_activity_list);

        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.c_listitem,
                entries));
        listView.setOnItemClickListener(this);

        // Activate "back button" in Action Bar if possible
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Called when the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        // Close database
        dbMain.close();

        super.onDestroy();
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

    /**
     * Handler for opening a list item which will return to the main view
     *
     * @param adapterView The ListView where the click happened
     * @param v           The view that was clicked within the ListView
     * @param position    The position of the view in the list
     * @param id          The row id of the item that was clicked
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        // Determine date of clicked item
        if (dbMain != null && position >= 0
                && position < dbMain.dayEntries.size()) {
            DayEntry selectedEntry = dbMain.dayEntries.get(position);

            Integer month = selectedEntry.date.get(Calendar.MONTH);
            Integer year = selectedEntry.date.get(Calendar.YEAR);

            Intent intent = getIntent();
            intent.putExtra("month", month.toString());
            intent.putExtra("year", year.toString());

            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
