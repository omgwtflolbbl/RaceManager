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
    private static final String RACES_KEY = "RACES_KEY";

    private EventAdapter eventAdapter;
    OnEventSelectedListener mListener;

    public EventsFragment() {
        setArguments(new Bundle());
    }

    /*public static EventsFragment newInstance(ArrayList<Race> races) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(RACES_KEY, races);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Log.i("THE ONCREATEVIEW HAS", savedInstanceState.toString());
        }

        Log.i("THE ARGUMENTS", getArguments().toString());

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
                mListener.onEventSelected(race);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Log.i("THE ACTIVITYCREATED HAS", savedInstanceState.getBundle("EVENT_FRAGMENT").toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        Log.i("LOGTAG5", "HELLO");

        ArrayList<Race> racess = args.getParcelableArrayList("EVENT_LIST");
        Log.i("LOGTAG5", "RDY");
        if (args.size()>0) {
            String size = Integer.toString(args.size());
            Log.i("LOGTAG10", size);
            Log.i("LOGTAG6", "THIS IS HAPPENING");
            ArrayList<Race> races = args.getParcelableArrayList("EVENT_LIST");
            Log.i("LOGTAG6", "IT REALLY IS");
            eventAdapter.addAll(races);
        }


        /*// FIGURE OUT STUFF TO BUILD THE ARRAYLIST<RACE> YO
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
        eventAdapter.add(race2);
        eventAdapter.add(race1);
        eventAdapter.add(race2);
        eventAdapter.add(race2);
        eventAdapter.add(race2);
        eventAdapter.add(race2);*/
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle bundle = new Bundle();
        bundle.putString("STRING", "HELLO NURSE");
        outState.putBundle("EVENT_FRAGMENT", bundle);
        outState.putString("EVENTS_FRAGMENT", "HELLO WORLLLLLD");
    }

    @Override
    public void onPause() {
        super.onPause();

        ArrayList<Race> races = new ArrayList<Race>();
        for (int i = 0; i < eventAdapter.getCount(); i++) {
            races.add(eventAdapter.getItem(i));
        }
        getArguments().putParcelableArrayList("EVENT_LIST", races);
    }

    public interface OnEventSelectedListener {
        public void onEventSelected(Race race);
    }

    public void clearEventAdapter() {
        eventAdapter.clear();
        eventAdapter.notifyDataSetChanged();
    }

    public void repopulateEventAdapter(ArrayList<Race> races) {
        eventAdapter.addAll(races);
        eventAdapter.notifyDataSetChanged();
    }

    public void addToEventAdapter(Race race) {
        eventAdapter.add(race);
        eventAdapter.notifyDataSetChanged();
    }
}
