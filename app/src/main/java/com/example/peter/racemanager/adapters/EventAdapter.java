package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.RaceDateComparator;
import com.example.peter.racemanager.models.Race;
import com.squareup.picasso.Picasso;

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

        // Set the race's image
        ImageView raceImage = (ImageView) view.findViewById(R.id.event_label_race_picture);
        Picasso.with(getContext())
                .load(race.getRaceImage())
                .fit()
                .centerInside()
                .error(R.drawable.profile)
                .into(raceImage);

        // Set the race's title
        TextView raceName = (TextView) view.findViewById(R.id.event_label_race_name);
        raceName.setText(race.getTitle());

        // Set the race's date and time data
        TextView raceDateTime = (TextView) view.findViewById(R.id.event_label_race_datetime);

        SimpleDateFormat format1 = new SimpleDateFormat("E, MMM dd, yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("hh:mm a");
        String dateText = format1.format(race.getDateAndTime()) + " \u2022 " + format2.format(race.getDateAndTime());

        raceDateTime.setText(dateText);

        // TODO: Set the race's description
        // Set the race's description
        TextView raceDescription = (TextView) view.findViewById(R.id.event_label_race_description);
        // TODO: raceDescription.setText("");
        if (race.getDescription().equals("")) {
            raceDescription.setText(race.getBlockquote());
        }
        else {
            raceDescription.setText(Html.fromHtml(race.getDescription().replaceFirst("////n", "").trim()));
        }

        // TODO: Decide on scrolling behavior of text
        raceName.setSelected(true);
        // TODO: raceDescription.setSelected(true);

        // Set joined status
        ImageButton statusImage = (ImageButton) view.findViewById(R.id.event_label_race_status);
        String username = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", "AnonymousSpectator159753");
        // Check if user has joined race
        Boolean joined = false;
        for (int i = 0, size = race.getRacers().size(); i < size; i++) {
            if (race.getRacers().get(i).getUsername().equals(username)) {
                statusImage.setImageResource(R.drawable.circled_check);
                joined = true;
            }
        }
        // If not, then put empty circle
        if (!joined) {
            if (race.getStatus().equals("Open")) {
                statusImage.setImageResource(R.drawable.circle_empty);
            }
            else {
                statusImage.setImageResource(R.drawable.circle_crossed);
            }
        }

        // TODO: Set onclick listener to go to new page
        statusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Secondary Action Placeholder", Toast.LENGTH_SHORT).show();
            }
        });

        // Set opacity based on date
        Date date = new Date();
        if (race.getDateAndTime().before(date)) {
            //labelLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.redA100));
            labelLayout.setAlpha((float) .3);
        }
        else {
            //labelLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greenA100));
            labelLayout.setAlpha(1);
        }

        // Return the view to be displayed
        return view;
    }

    // Override notifyDataSetChanged to sort the events by date BEFORE the normal notifyDataSetChanged event occurs
    @Override
    public void notifyDataSetChanged() {
        this.setNotifyOnChange(false);
        //this.sort(new RaceDateComparator());
        //this.setNotifyOnChange(true);
        super.notifyDataSetChanged();
    }
}
