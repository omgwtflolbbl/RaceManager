package com.example.peter.racemanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.activities.LoginActivity;
import com.example.peter.racemanager.fragments.ChangeSlotDialogFragment;
import com.example.peter.racemanager.fragments.RaceScheduleFragment;
import com.example.peter.racemanager.models.Heat;
import com.example.peter.racemanager.models.Slot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by Peter on 6/5/2016.
 */
public class RoundAdapter3 extends ArrayAdapter<Heat> {
    private int roundIndex;
    private onSlotSelectListener mListener;
    private String status;

    public RoundAdapter3 (Context context, ArrayList<Heat> heats, int roundIndex, String status, onSlotSelectListener mListener) {
        super(context, 0, heats);
        this.roundIndex = roundIndex;
        this.status = status;
        this.mListener = mListener;
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

        // Figure out what color to make everything
        String[] current = status.split(" ");
        String color = "grey";
        if (current[0].equals("NS")) {
            // Race not started yet, all are "pending"
            color = "blue";
        }
        else if (current[0].equals("F")) {
            // Race is over, all are "done"
            color = "grey";
        }
        else if (roundIndex < Integer.parseInt(current[1])) {
            // This is an old round
            color = "grey";
        }
        else if (roundIndex == Integer.parseInt(current[1])) {
            // This is the current round
            if (position < Integer.parseInt(current[2])) {
                // This is an old heat
                color = "grey";
            }
            else if (position == Integer.parseInt(current[2])) {
                // This is the current heat
                if (current[0].equals("W")) {
                    // Currently readying
                    color = "orange";
                }
                else {
                    // This group is currently racing
                    color = "green";
                }
            }
            else {
                // This is a future heat in the round
                color = "blue";
            }
        }
        else {
            // This is a future round
            color = "blue";
        }



        CardView cardView = (CardView) view.findViewById(R.id.race_schedule_heat_card);
        //cardView.setCardBackgroundColor(position % 2 == 0 ? ContextCompat.getColor(getContext(), R.color.lightblue) : ContextCompat.getColor(getContext(), R.color.cyan));
        cardView.setCardBackgroundColor(color.equals("grey") ? ContextCompat.getColor(getContext(), R.color.bluegrey500) :
                color.equals("blue") ? ContextCompat.getColor(getContext(), R.color.blue500) :
                        color.equals("orange") ? ContextCompat.getColor(getContext(), R.color.orangeA400) :
                                ContextCompat.getColor(getContext(), R.color.greenA400));

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

        Iterator<String> slots = heat.getHeatMap().keySet().iterator();
        //Iterator<String> slots = heat.getKeys().iterator();

        int j = 0;

        // Create TextView for each slot
        while (slots.hasNext()) {
            String slot = slots.next();
            TextView slotText = new TextView(getContext());
            slotText.setTag(String.format("%d %d %s", roundIndex, position, slot));
            slotText.setText(String.format("%s\n%s (%d Pt.)", heat.getSlot(slot).getUsername(), heat.getSlot(slot).getFrequency(), heat.getSlot(slot).getPoints()));
            slotText.setWidth(0);
            slotText.setGravity(Gravity.CENTER);
            if (j == 0) {
                slotText.setBackgroundResource(pickColorTL(color));
            }
            else if (j == 1) {
                slotText.setBackgroundResource(pickColorTR(color));
            }
            else if (j == heat.numSlots() - 2) {
                if (j % 2 == 0) {
                    slotText.setBackgroundResource(pickColorBL(color));
                }
                else {
                    slotText.setBackgroundResource(pickColorBR(color));
                }
            }
            else if (j == heat.numSlots() - 1) {
                if (j % 2 == 0) {
                    slotText.setBackgroundResource(pickColorBL(color));
                }
                else {
                    slotText.setBackgroundResource(pickColorBR(color));
                }
            }
            else {
                slotText.setBackgroundResource(pickColor(color));
            }
            slotText.setClickable(true);
            slotText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((RaceScheduleFragment) mListener).checkPermissions()) {
                        mListener.showChangeSlotDialog(view);
                    }
                    else {
                        //Toast.makeText(getContext(), "WONK WONK WONK", Toast.LENGTH_SHORT).show();
                    }
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
        if (!username.equals(LoginActivity.GUEST) && heat.findRacerInHeat(username) != null) {
            String racerLoc = String.format("%d %d %s", roundIndex, position, heat.findRacerInHeat(username));
                view.findViewWithTag(racerLoc).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pinkA200));
        }

        return view;
    }

    private int pickColor(String color) {
        switch (color) {
            case "grey": return R.drawable.slot_text_grey;
            case "blue": return R.drawable.slot_text_blue;
            case "orange": return R.drawable.slot_text_orange;
            case "green": return R.drawable.slot_text_green;
            default: return R.drawable.slot_text_grey;
        }
    }

    private int pickColorTL(String color) {
        switch (color) {
            case "grey": return R.drawable.slot_text_grey_tl;
            case "blue": return R.drawable.slot_text_blue_tl;
            case "orange": return R.drawable.slot_text_orange_tl;
            case "green": return R.drawable.slot_text_green_tl;
            default: return R.drawable.slot_text_grey_tl;
        }
    }

    private int pickColorTR(String color) {
        switch (color) {
            case "grey": return R.drawable.slot_text_grey_tr;
            case "blue": return R.drawable.slot_text_blue_tr;
            case "orange": return R.drawable.slot_text_orange_tr;
            case "green": return R.drawable.slot_text_green_tr;
            default: return R.drawable.slot_text_grey_tr;
        }
    }

    private int pickColorBL(String color) {
        switch (color) {
            case "grey": return R.drawable.slot_text_grey_bl;
            case "blue": return R.drawable.slot_text_blue_bl;
            case "orange": return R.drawable.slot_text_orange_bl;
            case "green": return R.drawable.slot_text_green_bl;
            default: return R.drawable.slot_text_grey_bl;
        }
    }

    private int pickColorBR(String color) {
        switch (color) {
            case "grey": return R.drawable.slot_text_grey_br;
            case "blue": return R.drawable.slot_text_blue_br;
            case "orange": return R.drawable.slot_text_orange_br;
            case "green": return R.drawable.slot_text_green_br;
            default: return R.drawable.slot_text_grey_br;
        }
    }

    public interface onSlotSelectListener {
        void showChangeSlotDialog(View view);
    }
}
