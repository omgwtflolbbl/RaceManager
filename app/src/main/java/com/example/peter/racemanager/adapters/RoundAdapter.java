package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Heat;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Round;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Peter on 6/3/2016.
 */
public class RoundAdapter extends ArrayAdapter<Round> {
    private ArrayList<Round> rounds;

    public RoundAdapter(Context context, ArrayList<Round> rounds) {
        super(context, 0, rounds);

        this.rounds = rounds;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        Round round = getItem(position);
        // Check if the existing view is being used, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.round_card, parent, false);
        }

        // Get GridLayout in which everything on the card will be laid out
        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.race_schedule_round_grid);
        gridLayout.setColumnCount(3);

        // Create TextView for the header (which is just the round number)
        TextView roundNumber = new TextView(getContext());
        roundNumber.setTag(Integer.toString(position + 1));
        roundNumber.setText(String.format("ROUND %d / %d", position + 1, rounds.size()));
        roundNumber.setTextSize(26);
        roundNumber.setGravity(Gravity.CENTER);
        roundNumber.setBackgroundColor(Color.BLUE);
        GridLayout.LayoutParams roundNumberParams = new GridLayout.LayoutParams();
        roundNumberParams.rowSpec = GridLayout.spec(0);
        roundNumberParams.columnSpec = GridLayout.spec(0, 3);
        roundNumberParams.setGravity(Gravity.FILL);
        roundNumber.setLayoutParams(roundNumberParams);
        gridLayout.addView(roundNumber);

        // Counter to keep track of current row
        int currentRow = 1;

        // Go through round and get every heat
        for (int i = 0; i < round.length(); i++) {
            Heat heat = round.getHeat(i);

            int rowSpan = ((heat.numSlots() - 1) / 2) * 2 + 2;

            // Create TextView for the heat number
            TextView heatNumber = new TextView(getContext());
            heatNumber.setTag(String.format("%d %d", position + 1, i + 1));
            heatNumber.setText(String.format("HEAT %d", i + 1));
            heatNumber.setTextSize(22);
            heatNumber.setGravity(Gravity.CENTER);
            heatNumber.setBackgroundColor(i % 2 == 0 ? Color.BLUE : Color.CYAN);
            heatNumber.setRotation(270);
            GridLayout.LayoutParams heatNumberParams = new GridLayout.LayoutParams();
            heatNumberParams.rowSpec = GridLayout.spec(currentRow, rowSpan);
            heatNumberParams.columnSpec = GridLayout.spec(0);
            heatNumberParams.setGravity(Gravity.FILL_VERTICAL);
            heatNumber.setLayoutParams(heatNumberParams);
            gridLayout.addView(heatNumber);

            Iterator<String> slots = heat.getKeys().iterator();
            int j = 0;

            // Create TextView to contain each slot
            while (slots.hasNext()) {
                String slot = slots.next();
                TextView slotText = new TextView(getContext());
                slotText.setTag(String.format("%d %d %d", position + 1, i + 1, j + 1));
                slotText.setText(String.format("%s\n%s", heat.getUsername(slot), heat.getFrequency(slot)));
                slotText.setGravity(Gravity.CENTER_VERTICAL);
                slotText.setBackgroundResource(i % 2 == 0 ? R.drawable.border_blue : R.drawable.border_cyan);
                GridLayout.LayoutParams slotTextParams = new GridLayout.LayoutParams();
                slotTextParams.rowSpec = GridLayout.spec((j / 2) * 2 + currentRow, 2);
                slotTextParams.columnSpec = GridLayout.spec(j % 2 + 1, (float) 1);
                slotTextParams.setGravity(Gravity.FILL);
                slotText.setLayoutParams(slotTextParams);
                gridLayout.addView(slotText);

                j++;
            }

            // Update current row
            currentRow = currentRow + rowSpan;
        }


        return view;
    }
}
