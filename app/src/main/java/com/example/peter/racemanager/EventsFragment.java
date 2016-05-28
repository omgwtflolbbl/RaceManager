package com.example.peter.racemanager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsFragment extends Fragment {

    String jsonStr = "";
    private EventAdapter eventAdapter;

    public EventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        ListView listView = (ListView) view.findViewById(R.id.event_listview);
        eventAdapter = new EventAdapter(getActivity(), new ArrayList<Race>());
        listView.setAdapter(eventAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Toast.makeText(getActivity(), eventAdapter.getItem(i).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            jsonStr = args.getString(MainActivity.EXTRA_MESSAGE);
        }

        JSONObject json = new JSONObject();
        String name = "";

        try {
            json = new JSONObject(jsonStr);
            name = json.getJSONArray("Employee").getJSONObject(1).getString("name");
        } catch (Exception x) {
            Log.d("HEYMAN ", x.toString());
        }

        // FIGURE OUT STUFF TO BUILD THE ARRAYLIST<RACE> YO
        // ACTUALLY PROBABLY BUILD THE ARRAYLIST<RACE> IN THE MAIN ACTIVITY AND PASS IT IN VIA INTENT. YO.
        ArrayList<Race> races = new ArrayList<Race>();

        String race1Name = "Race 1 name";
        String race1Date = "May 28, 2016";
        String race1Time = "9:00 AM";
        String race1URL = "https://www.multigp.com/some/url/for/race1";
        String race1Blockquote = "Some important crap in a blockquote";
        String race1Description = "Some other important crap in the description";
        Race race1 = new Race(race1Name, race1URL, race1Date, race1Time, race1Blockquote, race1Description);
        String race2Name = "Race 2 name";
        String race2Date = "May 29, 2016";
        String race2Time = "10:00 AM";
        String race2URL = "https://www.multigp.com/some/url/for/race2";
        String race2Blockquote = "Some useless crap in a blockquote";
        String race2Description = "Some other useless crap in the description";
        Race race2 = new Race(race2Name, race2URL, race2Date, race2Time, race2Blockquote, race2Description);

        eventAdapter.add(race1);
        eventAdapter.add(race2);


        /*TextView textView = new TextView(getActivity());
        textView.setText(name);

        TextView textView2 = new TextView(getActivity());
        textView2.setTextSize(40);
        textView2.setText("AT LEAST THIS IS HERE");

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.event_layout);
        layout.addView(textView);
        layout.addView(textView2);*/
    }
}
