package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.fragments.BuildRaceStructureCFragment;
import com.example.peter.racemanager.models.Racer;

import java.util.ArrayList;

/**
 * Created by Peter on 6/22/2016.
 */
public class RacerFrequencyAdapter extends ArrayAdapter<Racer> {

    private ArrayList<Racer> racers;

    RacerFrequencyAdapterListener mListener;

    public RacerFrequencyAdapter(Context context, ArrayList<Racer> racers, BuildRaceStructureCFragment fragment) {
        super(context, 0, racers);
        this.racers = racers;
        mListener = (RacerFrequencyAdapterListener) fragment;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        // Get the data item for this position
        final Racer racer = getItem(position);
        // Check if the existing view is being used, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.add_racer_frequency_label, parent, false);
        }

        TextView username = (TextView) view.findViewById(R.id.build_race_c_text_username);
        username.setText(racer.getUsername());

        TextView frequency = (TextView) view.findViewById(R.id.build_race_c_text_frequency);
        frequency.setText(racer.getFrequency());

        LinearLayout removeButton = (LinearLayout) view.findViewById(R.id.build_race_c_remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If this doesn't work, use an interface/callback method instead from the fragment to remove
                racers.remove(position);
                notifyDataSetChanged();
                mListener.setFalseOnMap(racer.getUsername());
            }
        });

        return view;
    }

    public interface RacerFrequencyAdapterListener {
        void setFalseOnMap(String username);
    }
}