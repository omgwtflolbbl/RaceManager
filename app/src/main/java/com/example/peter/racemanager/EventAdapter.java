package com.example.peter.racemanager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Peter on 5/27/2016.
 */
public class EventAdapter extends ArrayAdapter<Race> {
    public EventAdapter(Context context, ArrayList<Race> races) {
        super(context, 0, races);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        Race race = getItem(position);
        // Check if the existing view is being used, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_label, parent, false);
        }
        // Get views that need to be populated
        RelativeLayout labelLayout = (RelativeLayout) view.findViewById(R.id.event_label_layout);
        TextView raceName = (TextView) view.findViewById(R.id.event_label_race_name);
        TextView raceDateTime = (TextView) view.findViewById(R.id.event_label_race_datetime);

        // SimpleDateFormat for formatting Date from race into proper format
        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, yyyy @ hh:mm a");

        // Populate views with data
        raceName.setText(race.getTitle());
        raceDateTime.setText(sdf.format(race.getDateAndTime()));

        // Make sure the race name/title textview can scroll if the name is too long
        raceName.setSelected(true);

        // Some stuff to see if the race is still open or not
        Date date = new Date();
        if (race.getDateAndTime().before(date)) {
            labelLayout.setBackgroundColor(Color.RED);
        }
        else {
            labelLayout.setBackgroundColor(Color.GREEN);
        }

        // Return the view to be displayed
        return view;
    }

    // Override notifyDataSetChanged to sort the events by date BEFORE the normal notifyDataSetChanged event occurs
    @Override
    public void notifyDataSetChanged() {
        this.setNotifyOnChange(false);
        this.sort(new RaceDateComparator());
        //this.setNotifyOnChange(true);
        super.notifyDataSetChanged();
    }
}
