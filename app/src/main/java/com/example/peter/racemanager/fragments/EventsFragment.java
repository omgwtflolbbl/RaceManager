package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.EventAdapter;
import com.example.peter.racemanager.models.Race;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsFragment extends Fragment implements AddRaceDialogFragment.AddRaceDialogListener {
    private static final String REFRESHING_KEY = "REFRESHING_KEY";
    private static final String ROTATED_KEY = "ROTATED_KEY";

    private EventAdapter eventAdapter;
    OnEventSelectedListener mListener;
    private boolean refreshing;
    private boolean rotated = false;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_event:
                showAddRaceDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            refreshing = savedInstanceState.getBoolean(REFRESHING_KEY);
            rotated = savedInstanceState.getBoolean(ROTATED_KEY);
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
                Race race = (Race) adapterView.getItemAtPosition(i);
                mListener.onEventSelected(race);
            }
        });

        return view;
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
        else{
            emptyCheck();
        }
        if (!rotated) {
            mListener.refreshEventsFragment(this);
        }
        else {
            rotated = false;
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

        if (getActivity().isChangingConfigurations()) {
            rotated = true;
        }

        Bundle bundle = new Bundle();
        outState.putBundle("EVENT_FRAGMENT", bundle);
        outState.putBoolean(REFRESHING_KEY, refreshing);
        outState.putBoolean(ROTATED_KEY, rotated);
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
        void onEventSelected(Race race);
        void refreshEventsFragment(EventsFragment fragment);
        void addNewEvent(String URL);
    }

    public void clearEventAdapter() {
        eventAdapter.clear();
        eventAdapter.notifyDataSetChanged();
    }

    public void repopulateEventAdapter(ArrayList<Race> races) {
        eventAdapter.addAll(races);
        eventAdapter.notifyDataSetChanged();
        emptyCheck();
    }

    public void emptyCheck() {
        TextView emptyText = (TextView) getView().findViewById(R.id.event_empty_text);
        if (eventAdapter.getCount() == 0) {
            Log.i("EVENTS", "NO RACES FOUND");
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            emptyText.setVisibility(View.GONE);
        }
    }

    public void addToEventAdapter(Race race) {
        eventAdapter.add(race);
        eventAdapter.notifyDataSetChanged();
    }

    public void startRefreshing() {
        refreshing = true;
        TextView emptyText = (TextView) getView().findViewById(R.id.event_empty_text);
        emptyText.setVisibility(View.GONE);
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

    public void showAddRaceDialog() {
        FragmentManager fm = getChildFragmentManager();
        AddRaceDialogFragment dialog = new AddRaceDialogFragment();
        dialog.show(fm, "add_race");
    }

    public void onFinishAddRaceDialog(String URL) {
        mListener.addNewEvent(URL);
    }
}
