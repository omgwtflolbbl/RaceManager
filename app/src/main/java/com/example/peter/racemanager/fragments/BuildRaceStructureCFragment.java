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
import android.widget.TextView;

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
    private List<TextView> headers;

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

        // Assign logic to auto assign button
        TextView autoButton = (TextView) view.findViewById(R.id.build_race_c_auto_button);
        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoAssign();
            }
        });

        // Assign logic to reset button
        TextView resetButton = (TextView) view.findViewById(R.id.build_race_c_reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
            }
        });

        // Make arraylist of header texts for updates
        headers = new ArrayList<>();
        headers.add((TextView) view.findViewById(R.id.build_race_c_slot_a_title));
        headers.add((TextView) view.findViewById(R.id.build_race_c_slot_b_title));
        headers.add((TextView) view.findViewById(R.id.build_race_c_slot_c_title));
        headers.add((TextView) view.findViewById(R.id.build_race_c_slot_d_title));
        headers.add((TextView) view.findViewById(R.id.build_race_c_slot_e_title));
        headers.add((TextView) view.findViewById(R.id.build_race_c_slot_f_title));
        headers.add((TextView) view.findViewById(R.id.build_race_c_slot_g_title));
        headers.add((TextView) view.findViewById(R.id.build_race_c_slot_h_title));

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

        for (int i = numSlots(); i < cardViews.size(); i++) {
            cardViews.get(i).setVisibility(View.GONE);
        }

        TextView continueButton = (TextView) view.findViewById(R.id.build_race_c_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContinue();
            }
        });

        TextView cancelButton = (TextView) view.findViewById(R.id.build_race_c_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        updateHeaders();

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

    // Returns the number of slots allowed for this race
    public int numSlots() {
        for (int i = 0; i < freqChart.size(); i++) {
            if (freqChart.get(i).get(0).equals("")) {
                return i;
            }
        }
        return 8;
    }

    // Updates the headers to reflect how many users are assigned to that slot
    public void updateHeaders() {
        for (int i = 0, size = numSlots(); i < size; i++) {
            headers.get(i).setText(String.format("Slot %s Racers (%d)", (char) ('A' + i), slots.get(i).size()));
        }
    }

    // This function should find everyone who has yet to be assigned a frequency and assign them to a
    // frequency, while making sure to keep the number of people assigned to each frequency as even
    // as possible.
    public void autoAssign() {
        ArrayList<String> usernames = availableUsers();
        // Loop over every username still unassigned
        while (usernames.size() > 0) {
            // Find out which allowed slot has the least number of assigned users
            int lowestIndex = 0;
            int lowestSize = Integer.MAX_VALUE;
            for (int i = 0; i < numSlots(); i++) {
                if (slots.get(i).size() < lowestSize) {
                    lowestIndex = i;
                    lowestSize = slots.get(i).size();
                }
            }

            // TODO: Refactor this and OnFinishDialog to use the same method for inserting user into adapters and marking as done
            // Now that we have the most empty slot comparatively, add the first user to that slot
            // using the first frequency option available in that slot
            String username = usernames.get(0);
            String frequency = freqChart.get(lowestIndex).get(0);

            // Mark racer as having been added
            racersMap.put(username, true);

            // Update the listviews
            Racer match = null;
            for (Racer racer : racers) {
                if (racer.getUsername().equals(username)) {
                    match = new Racer(racer.getUsername(), racer.getRacerUrl(), racer.getRacerPhoto(), racer.getDroneName(), racer.getdroneURL(), frequency, racer.getPoints());
                }
            }
            slots.get(lowestIndex).add(match);
            adapters.get(lowestIndex).notifyDataSetChanged();

            // Remove this user now that they should be handled
            usernames.remove(0);
        }
        updateHeaders();
    }

    // Resets the list of who is available and removes everyone from their assigned slots
    public void resetAll() {
        // Reset the frequencies of each Racer object and the hashmap of available racers
        for (Racer racer : racers) {
            racer.setFrequency("");
            racersMap.put(racer.getUsername(), false);
        }
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).clear();
            adapters.get(i).notifyDataSetChanged();
        }
        updateHeaders();
    }

    // Returns an arraylist that finds everyone who has yet to be assigned a frequency
    public ArrayList<String> availableUsers() {
        ArrayList<String> usernames = new ArrayList<>();
        for (String username : racersMap.keySet()) {
            if (!racersMap.get(username)) {
                usernames.add(username);
            }
        }

        return usernames;
    }

    // Opens up dialog box to add a new frequency/racer pairing
    public void showAddRacerFrequencyPairDialog(int i) {
        FragmentManager fm = getChildFragmentManager();

        // Generate a list of frequencies allowed by this slot
        ArrayList<String> frequencies = (ArrayList<String>) freqChart.get(i);

        // Get available people and sort them for easier picking
        ArrayList<String> usernames = availableUsers();
        Collections.sort(usernames, String.CASE_INSENSITIVE_ORDER);

        // Display the dialog fragment
        AddRacerFrequencyPairDialogFragment dialog = AddRacerFrequencyPairDialogFragment.newInstance(frequencies, usernames, i, racersMap.size());
        //dialog.setTargetFragment(BuildRaceStructureCFragment.this, 300);
        dialog.show(fm, "some other unknown text");

    }

    public void OnFinishAddRacerFrequencyPairDialog(String frequency, String username, int i) {
        // Mark racer as having been added
        racersMap.put(username, true);

        // Update the listviews
        Racer match = null;
        for (Racer racer : racers) {
            if (racer.getUsername().equals(username)) {
                match = new Racer(racer.getUsername(), racer.getRacerUrl(), racer.getRacerPhoto(), racer.getDroneName(), racer.getdroneURL(), frequency, racer.getPoints());
            }
        }
        slots.get(i).add(match);
        adapters.get(i).notifyDataSetChanged();
        updateHeaders();
    }

    public void setFalseOnMap(String username) {
        racersMap.put(username, false);
        updateHeaders();
    }

    public void onContinue() {
        mListener.BuildRaceCToD(slots, race);
    }

    public interface OnFragmentInteractionListener {
        void BuildRaceCToD(List<List<Racer>> racersInSlots, Race race);
    }
}
