package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.example.peter.racemanager.ExpandableHeightListView;
import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.RacerFrequencyAdapter;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuildRaceStructureCFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuildRaceStructureCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildRaceStructureCFragment extends Fragment implements AddRacerFrequencyPairDialogFragment.AddRacerFrequencyPairDialogListener, RacerFrequencyAdapter.RacerFrequencyAdapterListener {
    private static final String RACERS_KEY = "RACERS_KEY";
    private static final String FREQ_KEY = "FREQ_KEY";
    private static final String MAP_KEY = "MAP_KEY";
    private static final String RACERS_A_KEY = "RACERS_A_KEY";
    private static final String RACERS_B_KEY = "RACERS_B_KEY";
    private static final String RACERS_C_KEY = "RACERS_C_KEY";
    private static final String RACERS_D_KEY = "RACERS_D_KEY";
    private static final String RACERS_E_KEY = "RACERS_E_KEY";
    private static final String RACERS_F_KEY = "RACERS_F_KEY";
    private static final String RACERS_G_KEY = "RACERS_G_KEY";
    private static final String RACERS_H_KEY = "RACERS_H_KEY";
    private static final String RACE_KEY = "RACE_KEY";

    // Model stuff
    private Map<String, Boolean> racersMap;
    private List<Racer> racers;
    private List<List<Racer>> slots;
    private List<Racer> racersInA;
    private List<Racer> racersInB;
    private List<Racer> racersInC;
    private List<Racer> racersInD;
    private List<Racer> racersInE;
    private List<Racer> racersInF;
    private List<Racer> racersInG;
    private List<Racer> racersInH;
    private List<List<String>> freqChart;
    private Race race;

    // UI stuff
    private List<CardView> cardViews;
    private List<ExpandableHeightListView> listViews;
    private List<RacerFrequencyAdapter> adapters;
    private List<LinearLayout> addButtons;

    private OnFragmentInteractionListener mListener;

    public BuildRaceStructureCFragment() {
        // Required empty public constructor
    }
    public static BuildRaceStructureCFragment newInstance(ArrayList<String> freqChart, Race race) {
        BuildRaceStructureCFragment fragment = new BuildRaceStructureCFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(FREQ_KEY, freqChart);
        args.putParcelable(RACE_KEY, race);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(RACERS_A_KEY)) {
                racersInA = getArguments().getParcelableArrayList(RACERS_A_KEY);
                racersInB = getArguments().getParcelableArrayList(RACERS_B_KEY);
                racersInC = getArguments().getParcelableArrayList(RACERS_C_KEY);
                racersInD = getArguments().getParcelableArrayList(RACERS_D_KEY);
                racersInE = getArguments().getParcelableArrayList(RACERS_E_KEY);
                racersInF = getArguments().getParcelableArrayList(RACERS_F_KEY);
                racersInG = getArguments().getParcelableArrayList(RACERS_G_KEY);
                racersInH = getArguments().getParcelableArrayList(RACERS_H_KEY);
            }
            else {
                racersInA = new ArrayList<>();
                racersInB = new ArrayList<>();
                racersInC = new ArrayList<>();
                racersInD = new ArrayList<>();
                racersInE = new ArrayList<>();
                racersInF = new ArrayList<>();
                racersInG = new ArrayList<>();
                racersInH = new ArrayList<>();
            }

            slots = new ArrayList<>();
            slots.add(racersInA);
            slots.add(racersInB);
            slots.add(racersInC);
            slots.add(racersInD);
            slots.add(racersInE);
            slots.add(racersInF);
            slots.add(racersInG);
            slots.add(racersInH);

            if (getArguments().containsKey(RACERS_KEY)) {
                racers = getArguments().getParcelableArrayList(RACERS_KEY);
            }

            ArrayList<String> chart = getArguments().getStringArrayList(FREQ_KEY);
            freqChart = new ArrayList<>();
            for (int i = 0; i < chart.size(); i++) {
                freqChart.add(new ArrayList<String>(Arrays.asList(chart.get(i).split(";"))));
            }

            race = getArguments().getParcelable(RACE_KEY);

            if (getArguments().containsKey(MAP_KEY)) {
                racersMap = (HashMap<String, Boolean>) getArguments().getSerializable(MAP_KEY);
                System.out.println(racersMap.toString());
                racers = new ArrayList<>();
                for (int i = 0; i < race.getRacers().size(); i++) {
                    Racer racer = race.getRacers().get(i);
                    racers.add(new Racer(racer.getUsername(), racer.getRacerUrl(), racer.getRacerPhoto(), racer.getDroneName(), racer.getdroneURL(), "", 0));
                }
            }
            else {
                racersMap = new HashMap<>();
                racers = new ArrayList<>();
                for (int i = 0; i < race.getRacers().size(); i++) {
                    Racer racer = race.getRacers().get(i);
                    racersMap.put(racer.getUsername(), false);
                    racers.add(new Racer(racer.getUsername(), racer.getRacerUrl(), racer.getRacerPhoto(), racer.getDroneName(), racer.getdroneURL(), "", 0));
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_race_structure_c, container, false);

        // Make arraylist of listviews
        listViews = new ArrayList<>();
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_c_slot_a_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_c_slot_b_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_c_slot_c_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_c_slot_d_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_c_slot_e_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_c_slot_f_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_c_slot_g_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_c_slot_h_listview));

        // Make arraylist of adapters
        adapters = new ArrayList<>();
        adapters.add(new RacerFrequencyAdapter(getContext(), (ArrayList<Racer>) racersInA, this));
        adapters.add(new RacerFrequencyAdapter(getContext(), (ArrayList<Racer>) racersInB, this));
        adapters.add(new RacerFrequencyAdapter(getContext(), (ArrayList<Racer>) racersInC, this));
        adapters.add(new RacerFrequencyAdapter(getContext(), (ArrayList<Racer>) racersInD, this));
        adapters.add(new RacerFrequencyAdapter(getContext(), (ArrayList<Racer>) racersInE, this));
        adapters.add(new RacerFrequencyAdapter(getContext(), (ArrayList<Racer>) racersInF, this));
        adapters.add(new RacerFrequencyAdapter(getContext(), (ArrayList<Racer>) racersInG, this));
        adapters.add(new RacerFrequencyAdapter(getContext(), (ArrayList<Racer>) racersInH, this));

        // Set adapters to listviews and make expandable
        for (int i = 0; i < listViews.size(); i++) {
            listViews.get(i).setAdapter(adapters.get(i));
            listViews.get(i).setExpanded(true);
        }

        // Make arraylist of add buttons
        addButtons = new ArrayList<>();
        addButtons.add((LinearLayout) view.findViewById(R.id.build_race_c_slot_a_add_button));
        addButtons.add((LinearLayout) view.findViewById(R.id.build_race_c_slot_b_add_button));
        addButtons.add((LinearLayout) view.findViewById(R.id.build_race_c_slot_c_add_button));
        addButtons.add((LinearLayout) view.findViewById(R.id.build_race_c_slot_d_add_button));
        addButtons.add((LinearLayout) view.findViewById(R.id.build_race_c_slot_e_add_button));
        addButtons.add((LinearLayout) view.findViewById(R.id.build_race_c_slot_f_add_button));
        addButtons.add((LinearLayout) view.findViewById(R.id.build_race_c_slot_g_add_button));
        addButtons.add((LinearLayout) view.findViewById(R.id.build_race_c_slot_h_add_button));

        // Make add buttons do stuff
        for (int i = 0; i < addButtons.size(); i++) {
            final int index = i;
            addButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddRacerFrequencyPairDialog(index);
                }
            });
        }

        // Set visibility of stuff depending on number of allowed slots
        cardViews = new ArrayList<>();
        cardViews.add((CardView) view.findViewById(R.id.build_race_c_card_a));
        cardViews.add((CardView) view.findViewById(R.id.build_race_c_card_b));
        cardViews.add((CardView) view.findViewById(R.id.build_race_c_card_c));
        cardViews.add((CardView) view.findViewById(R.id.build_race_c_card_d));
        cardViews.add((CardView) view.findViewById(R.id.build_race_c_card_e));
        cardViews.add((CardView) view.findViewById(R.id.build_race_c_card_f));
        cardViews.add((CardView) view.findViewById(R.id.build_race_c_card_g));
        cardViews.add((CardView) view.findViewById(R.id.build_race_c_card_h));

        for (int i = 0; i < cardViews.size(); i++) {
            if (freqChart.get(i).get(0).equals("")) {
                cardViews.get(i).setVisibility(View.GONE);
            }
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        getArguments().putParcelableArrayList(RACERS_A_KEY, (ArrayList<Racer>) racersInA);
        getArguments().putParcelableArrayList(RACERS_B_KEY, (ArrayList<Racer>) racersInB);
        getArguments().putParcelableArrayList(RACERS_C_KEY, (ArrayList<Racer>) racersInC);
        getArguments().putParcelableArrayList(RACERS_D_KEY, (ArrayList<Racer>) racersInD);
        getArguments().putParcelableArrayList(RACERS_E_KEY, (ArrayList<Racer>) racersInE);
        getArguments().putParcelableArrayList(RACERS_F_KEY, (ArrayList<Racer>) racersInF);
        getArguments().putParcelableArrayList(RACERS_G_KEY, (ArrayList<Racer>) racersInG);
        getArguments().putParcelableArrayList(RACERS_H_KEY, (ArrayList<Racer>) racersInH);
        getArguments().putSerializable(MAP_KEY, (HashMap<String, Boolean>) racersMap);
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

    public void showAddRacerFrequencyPairDialog(int i) {
        FragmentManager fm = getChildFragmentManager();

        // Generate a list of frequencies allowed by this slot
        ArrayList<String> frequencies = (ArrayList<String>) freqChart.get(i);

        // Create an arraylist that finds everyone who has yet to be assigned a frequency
        ArrayList<String> usernames = new ArrayList<>();
        for (String username : racersMap.keySet()) {
            if (!racersMap.get(username)) {
                usernames.add(username);
            }
        }

        Collections.sort(usernames, String.CASE_INSENSITIVE_ORDER);

        AddRacerFrequencyPairDialogFragment dialog = AddRacerFrequencyPairDialogFragment.newInstance(frequencies, usernames, i, racersMap.size());
        //dialog.setTargetFragment(BuildRaceStructureCFragment.this, 300);
        dialog.show(fm, "some other unknown text");

    }

    public void OnFinishAddRacerFrequencyPairDialog(String frequency, String username, int i) {
        racersMap.put(username, true);
        Racer match = null;
        for (Racer racer : racers) {
            if (racer.getUsername().equals(username)) {
                match = new Racer(racer.getUsername(), racer.getRacerUrl(), racer.getRacerPhoto(), racer.getDroneName(), racer.getdroneURL(), frequency, racer.getPoints());
            }
        }
        slots.get(i).add(match);
        adapters.get(i).notifyDataSetChanged();
    }

    public void setFalseOnMap(String username) {
        racersMap.put(username, false);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
