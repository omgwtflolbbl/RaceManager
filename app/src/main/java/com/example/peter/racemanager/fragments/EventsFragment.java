package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.racemanager.ListUtils;
import com.example.peter.racemanager.R;
import com.example.peter.racemanager.RaceDateComparator;
import com.example.peter.racemanager.adapters.EventAdapter;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.services.StatusService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsFragment extends Fragment implements AddRaceDialogFragment.AddRaceDialogListener {
    private static final String REFRESHING_KEY = "REFRESHING_KEY";
    private static final String ROTATED_KEY = "ROTATED_KEY";

    private ArrayList<Race> raceList;

    private ListView listView1;
    private ListView oldList;

    private EventAdapter eventAdapter;
    private EventAdapter eventAdapter2;
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
            //TODO: Remove this?
            /*case R.id.action_add_event:
                showAddRaceDialog();
                return true;*/
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

        // Instantiate listview
        listView1 = (ListView) view.findViewById(R.id.event_listview_upcoming);
        eventAdapter = new EventAdapter(getActivity(), new ArrayList<Race>());
        listView1.setAdapter(eventAdapter);

        // On click, get the race from that position and open a racefragment
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Race race = (Race) adapterView.getItemAtPosition(i);
                mListener.onEventSelected(race);
            }
        });

        // Instantiate listview for completed races
        oldList = (ListView) view.findViewById(R.id.event_listview_completed);
        eventAdapter2 = new EventAdapter(getActivity(), new ArrayList<Race>());
        oldList.setAdapter(eventAdapter2);

        // On click, get the race from that position and open a racefragment
        oldList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            eventAdapter2.clear();
            raceList = args.getParcelableArrayList("EVENT_LIST");
            if (raceList != null) {
                updateEventAdapter(raceList);
            }
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

        /*ArrayList<Race> races = new ArrayList<Race>();
        for (int i = 0; i < eventAdapter.getCount(); i++) {
            races.add(eventAdapter.getItem(i));
        }
        for (int i = 0; i < eventAdapter2.getCount(); i++) {
            races.add(eventAdapter2.getItem(i));
        }*/
        getArguments().putParcelableArrayList("EVENT_LIST", (ArrayList<Race>) raceList);
    }

    // Utility/helper class for sizing multiple listviews in a single container
    /*public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }*/

    public interface OnEventSelectedListener {
        void onEventSelected(Race race);
        void refreshEventsFragment(EventsFragment fragment);
        void addNewEvent(String URL);
    }

    public void clearEventAdapter() {
        eventAdapter.clear();
        eventAdapter2.clear();
        eventAdapter.notifyDataSetChanged();
        eventAdapter2.notifyDataSetChanged();
    }

    public void updateEventAdapter(ArrayList<Race> races) {
        // Check initialization
        if (raceList == null) {
            raceList = new ArrayList<Race>();
        }

        // Check if there are any events currently in the list that no longer need to be there
        for (Race newRace : races) {
            boolean found = false;
            for (Race oldRace : raceList) {
                if (newRace.getId() == oldRace.getId()) {
                    // Update old race information as best as possible
                    oldRace.updateAll(newRace);
                    found = true;
                }
            }
            if (!found) {
                // Add new race into list
                raceList.add(newRace);

                // Add new race into service for listening
                Intent intent = new Intent(getContext(), StatusService.class);
                intent.putExtra("EVENT_ID", newRace.getId());
                intent.putExtra("Race", newRace);
                getContext().startService(intent);
            }
        }

        // Check if event was already loaded before
        for (Race newRace : races) {
            boolean found = false;
            for (Race oldRace : raceList) {
                if (newRace.getId() == oldRace.getId()) {
                    // Update old race information as best as possible
                    oldRace.updateAll(newRace);
                    found = true;
                }
            }
            if (!found) {
                // Add new race into list
                raceList.add(newRace);
            }
        }

        // Sort all the events again
        Collections.sort(raceList, new RaceDateComparator());

        // Sort into two adapters based on updated raceList
        Date tomorrow = new Date();
        tomorrow.setTime(tomorrow.getTime() - 24 * 60 * 60 * 1000);
        for (int i = 0, size = races.size(); i < size; i++) {
            if (races.get(i).getDateAndTime().before(tomorrow)) {
                eventAdapter2.add(raceList.get(i));
            }
            else {
                eventAdapter.add(raceList.get(i));
            }
        }
        eventAdapter.notifyDataSetChanged();
        eventAdapter2.notifyDataSetChanged();
        ListUtils.setDynamicHeight(listView1);
        ListUtils.setDynamicHeight(oldList);
        emptyCheck();
    }

    public void repopulateEventAdapter(ArrayList<Race> races) {
        Collections.sort(races, new RaceDateComparator());
        Date tomorrow = new Date();
        tomorrow.setTime(tomorrow.getTime() - 24 * 60 * 60 * 1000);
        for (int i = 0, size = races.size(); i < size; i++) {
            if (races.get(i).getDateAndTime().before(tomorrow)) {
                eventAdapter2.add(races.get(i));
            }
            else {
                eventAdapter.add(races.get(i));
            }
        }
        //Collections.sort(races, new RaceDateComparator());
        //eventAdapter.addAll(races);
        eventAdapter.notifyDataSetChanged();
        eventAdapter2.notifyDataSetChanged();
        ListUtils.setDynamicHeight(listView1);
        ListUtils.setDynamicHeight(oldList);
        emptyCheck();
    }

    public void emptyCheck() {
        if (getView() != null) {
            TextView emptyText = (TextView) getView().findViewById(R.id.event_empty_text);
            if (eventAdapter.getCount() == 0 && eventAdapter2.getCount() == 0) {
                emptyText.setVisibility(View.VISIBLE);
            }
            else {
                emptyText.setVisibility(View.GONE);
            }
        }
    }

    public void addToEventAdapter(Race race) {
        eventAdapter.add(race);
        eventAdapter.notifyDataSetChanged();
    }

    // Set visibility while refreshing
    public void startRefreshing() {
        if (getView() != null) {
            refreshing = true;
            TextView emptyText = (TextView) getView().findViewById(R.id.event_empty_text);
            emptyText.setVisibility(View.GONE);
            ListView listView = (ListView) getView().findViewById(R.id.event_listview_upcoming);
            listView.setVisibility(View.GONE);
            ListView oldList = (ListView) getView().findViewById(R.id.event_listview_completed);
            oldList.setVisibility(View.GONE);
            TextView header1 = (TextView) getView().findViewById(R.id.event_header_1);
            header1.setVisibility(View.GONE);
            TextView header2 = (TextView) getView().findViewById(R.id.event_header_2);
            header2.setVisibility(View.GONE);
            ScrollView scrollView = (ScrollView) getView().findViewById(R.id.event_layout);
            scrollView.setVisibility(View.GONE);
            RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.loadingPanel);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    // Set visibility when refreshing is done
    public void finishRefreshing() {
        if (getView() != null) {
            refreshing = false;
            RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.loadingPanel);
            relativeLayout.setVisibility(View.GONE);
            ScrollView scrollView = (ScrollView) getView().findViewById(R.id.event_layout);
            scrollView.setVisibility(View.VISIBLE);
            ListView listView = (ListView) getView().findViewById(R.id.event_listview_upcoming);
            listView.setVisibility(View.VISIBLE);
            ListView oldList = (ListView) getView().findViewById(R.id.event_listview_completed);
            oldList.setVisibility(View.VISIBLE);
            TextView header1 = (TextView) getView().findViewById(R.id.event_header_1);
            header1.setVisibility(View.VISIBLE);
            TextView header2 = (TextView) getView().findViewById(R.id.event_header_2);
            header2.setVisibility(View.VISIBLE);
        }
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
