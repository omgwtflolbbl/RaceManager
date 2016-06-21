package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.AddFrequencySlot;

import java.util.ArrayList;

/**
 * Created by Peter on 6/20/2016.
 */
public class FrequencyBuilderAdapter extends ArrayAdapter<AddFrequencySlot> {

    public FrequencyBuilderAdapter (Context context, ArrayList<AddFrequencySlot> freqSlots) {
        super(context, 0, freqSlots);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        final AddFrequencySlot freqSlot = getItem(position);
        // Check if the existing view is being used, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.add_frequency_label, parent, false);
        }

        Spinner bandSpinner = (Spinner) view.findViewById(R.id.build_race_b_spinner_band);
        ArrayAdapter<String> bandAdapter = new ArrayAdapter<String>(parent.getContext(), R.layout.spinner_item, freqSlot.getAvailableBands());
        bandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bandSpinner.setOnItemSelectedListener(null);
        bandSpinner.setAdapter(bandAdapter);
        bandSpinner.setSelection(freqSlot.getAvailableBands().indexOf(freqSlot.getCurrentBand()), false);

        Spinner freqSpinner = (Spinner) view.findViewById(R.id.build_race_b_spinner_frequency);
        final ArrayAdapter<String> freqAdapter = new ArrayAdapter<String>(parent.getContext(), R.layout.spinner_item, freqSlot.getAvailableFrequencies());
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(freqAdapter);
        freqSpinner.setSelection(freqSlot.getAvailableFrequencies().indexOf(freqSlot.getCurrentFrequency()), false);

        bandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                freqSlot.setCurrentBand(parent.getItemAtPosition(position).toString());
                freqSlot.setAvailableFrequencies();
                freqAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
}
