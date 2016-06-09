package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.RacerListAdapter;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;

import java.util.ArrayList;

/**
 * Fragment to display list of all racers taking part in the event.
 */
public class RaceRacersFragment extends Fragment {
    private static final String RACE_KEY = "RACE_KEY";
    private static final String ROTATED_KEY = "ROTATED_KEY";

    private RacerListAdapter racerListAdapter;
    private Race race;
    private Boolean rotated;
    private Racer racer;

    private OnFragmentInteractionListener mListener;

    public RaceRacersFragment() {
        // Required empty public constructor
    }

    public static RaceRacersFragment newInstance(Race race) {
        RaceRacersFragment fragment = new RaceRacersFragment();
        Bundle args = new Bundle();
        args.putParcelable(RACE_KEY, race);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            race = getArguments().getParcelable(RACE_KEY);
        }
        if (savedInstanceState != null) {
            rotated = getArguments().getBoolean(ROTATED_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_race_racers, container, false);

        ListView listView = (ListView) view.findViewById(R.id.race_racers_listview);
        racerListAdapter = new RacerListAdapter(getActivity(), new ArrayList<Racer>());
        listView.setAdapter(racerListAdapter);

        // On click, get the race from that position and open a dialog box?
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Toast.makeText(getActivity(), racerListAdapter.getItem(i).getUsername(), Toast.LENGTH_SHORT).show();
                Race race = (Race) adapterView.getItemAtPosition(i);
                mListener.onFragmentInteraction(race);
            }
        });

        return view;
    }

    public void onButtonPressed(Race race) {
        if (mListener != null) {
            mListener.onFragmentInteraction(race);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Race race);
    }
}
