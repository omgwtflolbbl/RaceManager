package com.example.peter.racemanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.activities.LoginActivity;
import com.example.peter.racemanager.fragments.ChangeSlotDialogFragment;
import com.example.peter.racemanager.fragments.RaceFragment;
import com.example.peter.racemanager.fragments.RaceScheduleFragment;
import com.example.peter.racemanager.models.Heat;
import com.example.peter.racemanager.models.Slot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by Peter on 6/5/2016.
 */
public class RoundAdapter3 extends ArrayAdapter<Heat> {
    private int roundIndex;
    private int heatIndex;
    private onSlotSelectListener mListener;
    private String status;

    public RoundAdapter3 (Context context, ArrayList<Heat> heats, int roundIndex, String status, onSlotSelectListener mListener) {
        super(context, 0, heats);
        this.roundIndex = roundIndex;
        this.status = status;
        this.mListener = mListener;
        this.heatIndex = -1;
    }

    public RoundAdapter3 (Context context, ArrayList<Heat> heats, int roundIndex, int heatIndex, String status, onSlotSelectListener mListener) {
        super(context, 0, heats);
        this.roundIndex = roundIndex;
        this.status = status;
        this.mListener = mListener;
        this.heatIndex = heatIndex;
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

        if (heatIndex != -1) {
            position = heatIndex;
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
        /*cardView.setCardBackgroundColor(color.equals("grey") ? ContextCompat.getColor(getContext(), R.color.bluegrey500) :
                color.equals("blue") ? ContextCompat.getColor(getContext(), R.color.blue500) :
                        color.equals("orange") ? ContextCompat.getColor(getContext(), R.color.orangeA400) :
                                ContextCompat.getColor(getContext(), R.color.greenA400));*/

        // Get views that need to be populated
        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.race_schedule_round_grid);

        // Heat title
        final TextView heatText = new TextView(getContext());
        heatText.setTag(Integer.toString(position));
        SpannableString roundSpan = new SpannableString(String.format(Locale.US, "R%d", roundIndex + 1));
        roundSpan.setSpan(new AbsoluteSizeSpan(16, true), 0, roundSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString heatSpan = new SpannableString(String.format(Locale.US, "%d" , position + 1));
        heatSpan.setSpan(new AbsoluteSizeSpan(32, true), 0, heatSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannedString heatMessage = (SpannedString) TextUtils.concat(roundSpan, "\n", heatSpan);
        heatText.setIncludeFontPadding(false);
        heatText.setText(heatMessage);
        switch (color) {
            case "blue":
                heatText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPBlue));
                break;
            case "green":
                heatText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPGreen));
                break;
            case "orange":
                heatText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPOrange));
                break;
            default:
                heatText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPGray));
                break;
        }
        heatText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        heatText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        heatText.setHeight(GridLayout.LayoutParams.WRAP_CONTENT);
        GridLayout.LayoutParams heatParams = new GridLayout.LayoutParams();
        heatParams.setMargins(0, dpToPx(-6), 0, dpToPx(-6));
        heatParams.rowSpec = GridLayout.spec(0, (heat.numSlots()+2)/3);
        heatParams.columnSpec = GridLayout.spec(0);
        heatParams.setGravity(Gravity.FILL_VERTICAL);
        int pad = dpToPx(3);
        heatText.setLayoutParams(heatParams);
        heatText.setPadding(pad, pad, pad, 0);
        gridLayout.addView(heatText);

        // Add in a "padding" for the slots
        View separator = new View(getContext());separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        GridLayout.LayoutParams separatorParams = new GridLayout.LayoutParams();
        separatorParams.width = dpToPx(8);
        separatorParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        separatorParams.rowSpec = GridLayout.spec(0, (heat.numSlots()+2)/3);
        separatorParams.columnSpec = GridLayout.spec(1);
        separatorParams.setGravity(Gravity.FILL_VERTICAL);
        separator.setLayoutParams(separatorParams);
        gridLayout.addView(separator);

        Iterator<String> slots = heat.getHeatMap().keySet().iterator();

        float gridWidth = calculateGridSpace(roundIndex + 1, position + 1);

        int j = 0;
        Paint paint = new Paint();
        paint.setTextSize(11f);

        // Create TextView for each slot
        while (slots.hasNext()) {
            String slot = slots.next();
            final TextView slotText = new TextView(getContext());
            slotText.setTag(String.format("%d %d %s", roundIndex, position, slot));

            // Set up name
            String name = heat.getSlot(slot).getUsername();
            name = truncateName(paint, name, gridWidth);
            SpannableString nameSpan = new SpannableString(name);
            nameSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.MultiGPBlack)), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set up frequency and point information
            SpannableString infoSpan = new SpannableString(String.format(Locale.US, "%s - %dpts", heat.getSlot(slot).getFrequency(), heat.getSlot(slot).getPoints()));
            infoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.MultiGPGray)), 0, infoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannedString slotMessage = (SpannedString) TextUtils.concat(nameSpan, "\n", infoSpan);

            // Attach information
            slotText.setMaxLines(2);
            slotText.setText(slotMessage);

            slotText.setWidth(0);
            slotText.setTextSize(11f);

            // Ripple background?
            //slotText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            slotText.setBackgroundResource(R.drawable.ripple_background);

            slotText.setAllCaps(false);
            slotText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            // Set padding for individual slot views
            if (heat.getHeatMap().size() <= 3) {
                slotText.setPadding(dpToPx(4), dpToPx(12), dpToPx(2), dpToPx(12));
            }
            else {
                int slotPadding = dpToPx(2);
                slotText.setPadding(dpToPx(4), slotPadding, slotPadding, slotPadding);
            }

            slotText.setClickable(true);
            slotText.setIncludeFontPadding(false);
            slotText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener instanceof RaceScheduleFragment) {
                        if (((RaceScheduleFragment) mListener).checkPermissions()) {
                            mListener.showChangeSlotDialog(view);
                        }
                    }
                    else if (mListener instanceof RaceFragment) {
                        if (((RaceFragment) mListener).checkAdminPermissions()) {
                            mListener.showChangeSlotDialog(view);
                        }
                    }
                    else {
                        //Toast.makeText(getContext(), "WONK WONK WONK", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            GridLayout.LayoutParams slotTextParams = new GridLayout.LayoutParams();
            slotTextParams.rowSpec = GridLayout.spec((j / 3));
            slotTextParams.columnSpec = GridLayout.spec(j % 3 + 2, (float) 1);
            slotTextParams.setMargins(0, j > 2 ? dpToPx(1) : 0, j % 3 != 2 ? dpToPx(1) : 0, 0);
            slotText.setLayoutParams(slotTextParams);
            gridLayout.addView(slotText);

            j++;
        }

        // Add background white to empty spaces
        while ((j) % 3 != 0) {
            TextView placeholder = new TextView(getContext());
            placeholder.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            GridLayout.LayoutParams placeholderParams = new GridLayout.LayoutParams();
            placeholderParams.rowSpec = GridLayout.spec((j / 3));
            placeholderParams.columnSpec = GridLayout.spec(j % 3 + 2, (float) 1);
            placeholderParams.setGravity(Gravity.FILL);
            placeholderParams.setMargins(0, j > 2 ? dpToPx(1) : 0, j % 3 != 2 ? dpToPx(1) : 0, 0);
            placeholder.setLayoutParams(placeholderParams);
            gridLayout.addView(placeholder);

            j++;
        }

        // For highlighting user in cards
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = sharedPreferences.getString("username","AnonymousSpectator");
        if (!username.equals(LoginActivity.GUEST) && heat.findRacerInHeat(username) != null) {
            String racerLoc = String.format("%d %d %s", roundIndex, position, heat.findRacerInHeat(username));
                view.findViewWithTag(racerLoc).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPLightGray));
        }

        return view;
    }

    private int dpToPx(int dp) {
        Resources r = getContext().getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return px;
    }

    // Calculate the width of a grid containing a slot (dp)
    private float calculateGridSpace(int round, int heat) {
        // Get screen dp width
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        // Create paint to try and figure out width of the left hand round/heat TextView
        Paint paint = new Paint();
        paint.setTextSize(16);
        float roundWidth = paint.measureText(Integer.toString(round));
        paint.setTextSize(32);
        float heatWidth = paint.measureText(Integer.toString(heat));

        float leftWidth = roundWidth > heatWidth ? roundWidth : heatWidth;

        // Add up all known paddings and etc to get the space allotted for the three slots
        float nonSlotWidth = 0;
        if (mListener instanceof RaceFragment) {
            nonSlotWidth = 8 + 2 + 5 + leftWidth + 5 + 8 + 1 + 1 + 8 + 2 + 8;
        }
        else if (mListener instanceof RaceScheduleFragment) {
            nonSlotWidth = 18 + 2 + 5 + leftWidth + 5 + 8 + 1 + 1 + 8 + 2 + 18;
        }

        return ((dpWidth - nonSlotWidth) / 3) - 4;
    }

    // Cut the name down so that it fits into slot
    private String truncateName(Paint paint, String name, float gridWidth) {
        float nameWidth = paint.measureText(name);

        // Trim down string until it should fit
        while (nameWidth >= gridWidth) {
            float ratio = gridWidth / nameWidth;
            name = name.substring(0, ((int) (name.length() * ratio)) - 3) + "...";
            nameWidth = paint.measureText(name);
        }
        return name;
    }

    public void setRoundIndex(int index) {
        this.roundIndex = index;
    }

    public void setHeatIndex(int index) {
        this.heatIndex = index;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public interface onSlotSelectListener {
        void showChangeSlotDialog(View view);
    }
}
