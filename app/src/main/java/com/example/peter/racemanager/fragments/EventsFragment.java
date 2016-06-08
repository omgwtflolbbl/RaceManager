package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.EventAdapter;
import com.example.peter.racemanager.models.Race;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsFragment extends Fragment {
    private static final String RACES_KEY = "RACES_KEY";

    private EventAdapter eventAdapter;
    OnEventSelectedListener mListener;
    private boolean refreshing;

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
            refreshing = savedInstanceState.getBoolean("REFRESHING");
        }
        else {
            refreshing = false;
        }

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
            Log.i("THE ACTIVITYCREATED HAS", savedInstanceState.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args.size() > 0) {
            eventAdapter.clear();
            ArrayList<Race> races = args.getParcelableArrayList("EVENT_LIST");
            eventAdapter.addAll(races);
        }
        if (refreshing) {
            startRefreshing();
        }
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
        outState.putBoolean("REFRESHING", refreshing);
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

    public void startRefreshing() {
        refreshing = true;
        ListView listView = (ListView) getView().findViewById(R.id.event_listview);
        listView.setVisibility(View.GONE);
        RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.loadingPanel);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public void finishRefreshing() {
        refreshing = false;
        RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.loadingPanel);
        relativeLayout.setVisibility(View.GONE);
        ListView listView = (ListView) getView().findViewById(R.id.event_listview);
        listView.setVisibility(View.VISIBLE);
    }
}
