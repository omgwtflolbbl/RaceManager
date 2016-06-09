package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;

import java.util.ArrayList;

/**
 * Created by Peter on 6/8/2016.
 */
public class RacerListAdapter extends ArrayAdapter<Racer> {

    public RacerListAdapter (Context context, ArrayList<Racer> racers) {
        super(context, 0, racers);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        Racer racer = getItem(position);
        // Check if the existing view is being used, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_label, parent, false);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.race_racers_photo);

        TextView usernameText = (TextView) view.findViewById(R.id.race_racers_username);
        usernameText.setText(racer.getUsername());

        TextView frequencyText = (TextView) view.findViewById(R.id.race_racers_frequency);
        frequencyText.setText(racer.getFrequency());

        TextView pointsText = (TextView) view.findViewById(R.id.race_racers_points);
        pointsText.setText(racer.getPoints());

        return view;
    }
}
