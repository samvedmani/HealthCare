package com.example.healthcare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static java.lang.String.format;

/**
 * Custom adapter to populate calendar entry list items
 */
class C_DayEntryAdapter extends ArrayAdapter<C_PeriodicalDatabase.DayEntry> {
    private final Context context;
    private final List<C_PeriodicalDatabase.DayEntry> entryList;
    private final String packageName;
    private final Resources resources;

    /**
     * Constructor
     *
     * @param context     Application content
     * @param list        List with all calendar entries including details
     * @param packageName Application package from getPackageName()
     * @param resources   Global resources from getResources()
     */
    public C_DayEntryAdapter(Context context, List<C_PeriodicalDatabase.DayEntry> list, String packageName, Resources resources) {
        super(context, 0, list);

        this.context = context;
        this.packageName = packageName;
        this.resources = resources;
        entryList = list;
    }

    /**
     * Constructs a single item view
     *
     * @param position    Position of the item in the list
     * @param convertView Existing view to use (if null, a new one will be created)
     * @param parent      Group in which this view is inserted
     * @return View to be used for the item
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.c_listdetailsitem, parent, false);

        C_PeriodicalDatabase.DayEntry currentEntry = entryList.get(position);

        String textEvents = "";
        String textSymptoms = "";

        // Elements 0-2 are events, 2-17 are symptoms
        int eventIds[] = {
                1,  // Intercourse
                18, // Contraceptive pill
//                27,
//                28,
                20, // Tired
                21, // Energized
                19, // Spotting
                9,  // Intense bleeding
                2,  // Cramps
                17, // Headeache/migraine
                3,  // Back pain
                4,  // Middle pain left
                5,  // Middle pain right
                6,  // Breast pain/dragging pain
                7,  // Thrush/candida
                8,  // Discharge
                10, // Temperature fluctuations
                11, // Pimples
                12, // Bloating
                13, // Fainting
                14, // Grumpiness
                15, // Nausea
                16, // Cravings
        };
        int num = 0;
        for (int eventId : eventIds) {
            String resName = format("label_ev%d", eventId);
            int resId = resources.getIdentifier(resName, "string", packageName);
            if (resId != 0) {
                if (currentEntry.symptoms.contains(eventId)) {
                    if (num < 2) {
                        if (!textEvents.isEmpty()) textEvents += "\n";
                        textEvents += "\u2022 " + resources.getString(resId);
                    } else {
                        if (!textSymptoms.isEmpty()) textSymptoms += "\n";
                        textSymptoms += "\u2022 " + resources.getString(resId);
                    }
                }
            }
            num++;
        }

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

        TextView view;

        view = listItem.findViewById(R.id.item_date);
        switch (currentEntry.type) {
            case C_PeriodicalDatabase.DayEntry.PERIOD_START:
                view.setText(
                        dateFormat.format(currentEntry.date.getTime()) + " \u2014 " +
                                resources.getString(R.string.event_periodstart));
                break;
            case C_PeriodicalDatabase.DayEntry.PERIOD_CONFIRMED:
                view.setText(
                        dateFormat.format(currentEntry.date.getTime()) + " \u2014 " +
                                format(
                                        resources.getString(R.string.label_covidperiod_day),
                                        currentEntry.dayofcycle));
                break;
            default:
                view.setText(dateFormat.format(currentEntry.date.getTime()));
                break;
        }

        view = listItem.findViewById(R.id.item_intensity);
        if (currentEntry.type == C_PeriodicalDatabase.DayEntry.PERIOD_START ||
                currentEntry.type == C_PeriodicalDatabase.DayEntry.PERIOD_CONFIRMED) {
            String intensity = "?";
            switch (currentEntry.intensity) {
                case 1:
                    intensity = resources.getString(R.string.label_details_intensity1);
                    break;
                case 2:
                    intensity = resources.getString(R.string.label_details_intensity2);
                    break;
                case 3:
                    intensity = resources.getString(R.string.label_details_intensity3);
                    break;
                case 4:
                    intensity = resources.getString(R.string.label_details_intensity4);
                    break;
            }
            view.setText(intensity);
        } else {
            view.setText("\u2014");
        }

        view = listItem.findViewById(R.id.item_notes);
        if (currentEntry.notes.isEmpty()) view.setText("\u2014");
        else view.setText(currentEntry.notes);

        view = listItem.findViewById(R.id.item_event);
        if (textEvents.isEmpty()) view.setText("\u2014");
        else view.setText(textEvents);

        view = listItem.findViewById(R.id.item_symptom);
        if (textSymptoms.isEmpty()) view.setText("\u2014");
        else view.setText(textSymptoms);

        return listItem;
    }
}
