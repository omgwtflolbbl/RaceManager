package com.example.peter.racemanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
        TextView raceName = (TextView) view.findViewById(R.id.event_label_race_name);
        TextView raceDate = (TextView) view.findViewById(R.id.event_label_race_date);
        TextView raceTime = (TextView) view.findViewById(R.id.event_label_race_time);
        // Populate views with data
        Log.d("Title is ", race.getTitle());
        raceName.setText(race.getTitle());
        raceDate.setText(race.getDate());
        raceTime.setText(race.getTime());

        // Return the view to be displayed
        return view;
    }
}
