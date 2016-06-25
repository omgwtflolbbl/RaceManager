package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.RacerNameComparator;
import com.example.peter.racemanager.RacerPointComparator;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;

import java.util.ArrayList;

/**
 * Created by Peter on 6/8/2016.
 */
public class RacerListAdapter extends ArrayAdapter<Racer> {

    private String sort;

    public RacerListAdapter (Context context, ArrayList<Racer> racers) {
        super(context, 0, racers);
        sort = "name";
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        Racer racer = getItem(position);
        // Check if the existing view is being used, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.racer_list_label, parent, false);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.race_racers_photo);

        TextView usernameText = (TextView) view.findViewById(R.id.race_racers_username);
        usernameText.setText(racer.getUsername());

        TextView frequencyText = (TextView) view.findViewById(R.id.race_racers_frequency);
        frequencyText.setText(racer.getFrequency());

        TextView pointsText = (TextView) view.findViewById(R.id.race_racers_points);
        pointsText.setText(Integer.toString(racer.getPoints()) + " Pt.");

        return view;
    }

    // Override notifyDataSetChanged to sort the racers based on user preference
    @Override
    public void notifyDataSetChanged() {
        this.setNotifyOnChange(false);
        if (sort.equals("name")) {
            this.sort(new RacerNameComparator());
        }
        else if (sort.equals("points")) {
            this.sort(new RacerPointComparator());
        }
        //this.setNotifyOnChange(true);
        super.notifyDataSetChanged();
    }

    public void setSort(String sort) {
        this.sort = sort;
        notifyDataSetChanged();
    }
}
