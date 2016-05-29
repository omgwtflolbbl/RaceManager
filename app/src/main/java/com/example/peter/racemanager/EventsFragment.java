package com.example.peter.racemanager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsFragment extends Fragment {

    String jsonStr = "";
    private EventAdapter eventAdapter;
    OnEventSelectedListener mListener;

    public EventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        ListView listView = (ListView) view.findViewById(R.id.event_listview);
        eventAdapter = new EventAdapter(getActivity(), new ArrayList<Race>());
        listView.setAdapter(eventAdapter);

        // On click, get the race from that position and open a racefragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Toast.makeText(getActivity(), eventAdapter.getItem(i).getTitle(), Toast.LENGTH_SHORT).show();
                Race race = (Race) adapterView.getItemAtPosition(i);
                mListener.onEventSelected(race);/*
                RaceFragment raceFragment = RaceFragment.newInstance(race);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, raceFragment)
                        .addToBackStack(null)
                        .commit();*/
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

        // FIGURE OUT STUFF TO BUILD THE ARRAYLIST<RACE> YO
        // ACTUALLY PROBABLY BUILD THE ARRAYLIST<RACE> IN THE MAIN ACTIVITY AND PASS IT IN VIA INTENT. YO.
        ArrayList<Race> races = new ArrayList<Race>();

        String race1Name = "Race 1 name is really insanely long and like I don't know what kind of idiot would make a name like this for a race";
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
        eventAdapter.add(race2);
        //eventAdapter.add(race2);
        //eventAdapter.add(race1);
        //eventAdapter.add(race2);
        eventAdapter.add(race2);
        eventAdapter.add(race2);
        eventAdapter.add(race2);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnEventSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnEventSelectedListener {
        public void onEventSelected(Race race);
    }
}
