package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Heat;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Peter on 6/5/2016.
 */
public class RoundAdapter3 extends ArrayAdapter<Heat> {
    private int roundIndex;

    public RoundAdapter3 (Context context, ArrayList<Heat> heats, int roundIndex) {
        super(context, 0, heats);
        this.roundIndex = roundIndex;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        Heat heat = getItem(position);
        // This is honestly bad practice and defeats the purpose of reusing views but OH WELL FOR NOW
        // TODO: Actually reuse the views and add/remove views as necessary
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.heat_card, parent, false);
        }
        else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.heat_card, parent, false);
        }

        CardView cardView = (CardView) view.findViewById(R.id.race_schedule_heat_card);
        cardView.setCardBackgroundColor(position % 2 == 0 ? ContextCompat.getColor(getContext(), R.color.lightblue) : ContextCompat.getColor(getContext(), R.color.cyan));

        // Get views that need to be populated
        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.race_schedule_round_grid);

        // Heat title
        final TextView heatText = new TextView(getContext());
        heatText.setTag(Integer.toString(position));
        heatText.setText(String.format(" %d ", position + 1));
        heatText.setTextSize(32);
        heatText.setGravity(Gravity.CENTER);
        heatText.setHeight(GridLayout.LayoutParams.WRAP_CONTENT);
        GridLayout.LayoutParams heatParams = new GridLayout.LayoutParams();
        heatParams.rowSpec = GridLayout.spec(0, (heat.numSlots()+1)/2);
        heatParams.columnSpec = GridLayout.spec(0);
        heatParams.setGravity(Gravity.FILL_VERTICAL);
        heatText.setLayoutParams(heatParams);
        gridLayout.addView(heatText);

        Iterator<String> slots = heat.getKeys().iterator();

        int j = 0;

        while (slots.hasNext()) {
            String slot = slots.next();
            TextView slotText = new TextView(getContext());
            slotText.setTag(String.format("%d %d %s", roundIndex, position, slot));
            slotText.setText(String.format("%s\n%s", heat.getSlot(slot).getUsername(), heat.getSlot(slot).getFrequency()));
            slotText.setWidth(0);
            slotText.setGravity(Gravity.CENTER);
            if (j == 0) {
                slotText.setBackgroundResource(position % 2 == 0 ? R.drawable.border_blue_tl : R.drawable.border_cyan_tl);
            }
            else if (j == 1) {
                slotText.setBackgroundResource(position % 2 == 0 ? R.drawable.border_blue_tr : R.drawable.border_cyan_tr);
            }
            else if (j == heat.numSlots() - 2) {
                slotText.setBackgroundResource(position % 2 == 0 ? R.drawable.border_blue_bl : R.drawable.border_cyan_bl);
            }
            else if (j == heat.numSlots() - 1) {
                slotText.setBackgroundResource(position % 2 == 0 ? R.drawable.border_blue_br : R.drawable.border_cyan_br);
            }
            else {
                slotText.setBackgroundResource(position % 2 == 0 ? R.drawable.border_blue : R.drawable.border_cyan);
            }
            slotText.setClickable(true);
            slotText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TEXTVIEW PRESSED", view.getTag().toString());
                }
            });
            GridLayout.LayoutParams slotTextParams = new GridLayout.LayoutParams();
            slotTextParams.rowSpec = GridLayout.spec((j / 2));
            slotTextParams.columnSpec = GridLayout.spec(j % 2 + 1, (float) 1);
            slotTextParams.setGravity(Gravity.FILL);
            slotText.setLayoutParams(slotTextParams);
            gridLayout.addView(slotText);

            j++;
        }

        // For highlighting user in cards
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = sharedPreferences.getString("username","AnonymousSpectator");
        if (!username.equals("AnonymousSpectator") && heat.findRacerInHeat(username) != null) {
            String racerLoc = String.format("%d %d %s", roundIndex, position, heat.findRacerInHeat(username));
                view.findViewWithTag(racerLoc).setBackgroundColor(Color.parseColor("#FF69B4"));
        }

        return view;
    }
}
