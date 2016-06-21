package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.AddFrequencySlot;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * First fragment dealing with the recreation of a race from pretty much scratch.
 */
public class BuildRaceStructureAFragment extends Fragment {

    private final static String NUM_KEY = "NUM_KEY";
    private final static String FS_KEY = "FS_KEY";
    private final static String BE_KEY = "BE_KEY";
    private final static String BA_KEY = "BA_KEY";
    private final static String R_KEY = "R_KEY";
    private final static String B_KEY = "B_KEY";
    private final static String B13_KEY = "B13_KEY";
    private final static String TEMP_KEY = "TEMP_KEY";

    // Logic stuff
    private int numRacers;
    private boolean fatshark;
    private boolean boscamE;
    private boolean boscamA;
    private boolean raceband;
    private boolean betaband;
    private boolean b13;
    private boolean temp;

    // UI stuff
    private EditText numRacersText;
    private CheckedTextView fatsharkCheckbox;
    private CheckedTextView boscamECheckbox;
    private CheckedTextView boscamACheckbox;
    private CheckedTextView racebandCheckbox;
    private CheckedTextView betabandCheckbox;
    private CheckedTextView b13Checkbox;
    private CheckedTextView templateCheckbox;
    private TextView continueButton;
    private TextView cancelButton;

    private OnFragmentInteractionListener mListener;

    public BuildRaceStructureAFragment() {
        // Required empty public constructor
    }

    public static BuildRaceStructureAFragment newInstance(int numRacers, boolean fatshark, boolean boscamE, boolean boscamA, boolean raceband, boolean betaband, boolean b13, boolean temp) {
        BuildRaceStructureAFragment fragment = new BuildRaceStructureAFragment();
        Bundle args = new Bundle();
        args.putInt(NUM_KEY, numRacers);
        args.putBoolean(FS_KEY, fatshark);
        args.putBoolean(BE_KEY, boscamE);
        args.putBoolean(BA_KEY, boscamA);
        args.putBoolean(R_KEY, raceband);
        args.putBoolean(B_KEY, betaband);
        args.putBoolean(B13_KEY, b13);
        args.putBoolean(TEMP_KEY, temp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numRacers = getArguments().getInt(NUM_KEY);
            fatshark = getArguments().getBoolean(FS_KEY);
            boscamE = getArguments().getBoolean(BE_KEY);
            boscamA = getArguments().getBoolean(BA_KEY);
            raceband = getArguments().getBoolean(R_KEY);
            betaband = getArguments().getBoolean(B_KEY);
            b13 = getArguments().getBoolean(B13_KEY);
            temp = getArguments().getBoolean(TEMP_KEY);
        }
        Log.i("BUILDER", "ONCREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_race_structure_a, container, false);

        numRacersText = (EditText) view.findViewById(R.id.build_race_a_num_racers);
        numRacersText.setText(Integer.toString(numRacers));

        fatsharkCheckbox = (CheckedTextView) view.findViewById(R.id.build_race_a_checkbox_fatshark);
        fatsharkCheckbox.setChecked(fatshark);
        fatsharkCheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        boscamECheckbox = (CheckedTextView) view.findViewById(R.id.build_race_a_checkbox_boscam_e);
        boscamECheckbox.setChecked(boscamE);
        boscamECheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        boscamACheckbox = (CheckedTextView) view.findViewById(R.id.build_race_a_checkbox_boscam_a);
        boscamACheckbox.setChecked(boscamA);
        boscamACheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        racebandCheckbox = (CheckedTextView) view.findViewById(R.id.build_race_a_checkbox_raceband);
        racebandCheckbox.setChecked(raceband);
        racebandCheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        betabandCheckbox = (CheckedTextView) view.findViewById(R.id.build_race_a_checkbox_betaband);
        betabandCheckbox.setChecked(betaband);
        betabandCheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        b13Checkbox = (CheckedTextView) view.findViewById(R.id.build_race_a_checkbox_1_3ghz);
        b13Checkbox.setChecked(b13);
        b13Checkbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        templateCheckbox = (CheckedTextView) view.findViewById(R.id.build_race_a_checkbox_template);
        templateCheckbox.setChecked(temp);
        templateCheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        continueButton = (TextView) view.findViewById(R.id.build_race_a_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContinue();
            }
        });

        cancelButton = (TextView) view.findViewById(R.id.build_race_a_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        return view;
    }

    public void onContinue() {
        Toast.makeText(getContext(), "CONTINUE", Toast.LENGTH_SHORT).show();
        boolean[] bands = calculateBands();
        ArrayList<ArrayList<AddFrequencySlot>> freqSlots = getPackage();

        mListener.BuildRaceAToB(Integer.parseInt(numRacersText.getText().toString()), bands, freqSlots);
    }

    public ArrayList<ArrayList<AddFrequencySlot>> getPackage() {
        ArrayList<ArrayList<AddFrequencySlot>> freqSlots = emptySlotFreqPackage();

        boolean[] availableBands = calculateBands();

        if (templateCheckbox.isChecked()) {
            freqSlots.get(0).add(new AddFrequencySlot(availableBands, "Boscam E", "(E4) 5645"));
            freqSlots.get(1).add(new AddFrequencySlot(availableBands, "Boscam E", "(E2) 5685"));
            freqSlots.get(2).add(new AddFrequencySlot(availableBands, "Fatshark", "(F2) 5760"));
            freqSlots.get(3).add(new AddFrequencySlot(availableBands, "Fatshark", "(F4) 5800"));
            freqSlots.get(4).add(new AddFrequencySlot(availableBands, "Fatshark", "(F7) 5860"));
            freqSlots.get(5).add(new AddFrequencySlot(availableBands, "Boscam E", "(E6) 5905"));
            freqSlots.get(6).add(new AddFrequencySlot(availableBands, "Boscam E", "(E8) 5945"));
            freqSlots.get(7).add(new AddFrequencySlot(availableBands, "CUSTOM", "CUSTOM"));
        }
        else {
            for (int i = 0; i < freqSlots.size(); i++) {
                freqSlots.get(i).add(new AddFrequencySlot(availableBands));
            }
        }

        return freqSlots;
    }

    public void onCancel() {
        Toast.makeText(getContext(), "CANCEL", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();

        getArguments().putInt(NUM_KEY, Integer.parseInt(numRacersText.getText().toString()));
        getArguments().putBoolean(FS_KEY, fatsharkCheckbox.isChecked());
        getArguments().putBoolean(BE_KEY, boscamECheckbox.isChecked());
        getArguments().putBoolean(BA_KEY, boscamACheckbox.isChecked());
        getArguments().putBoolean(R_KEY, racebandCheckbox.isChecked());
        getArguments().putBoolean(B_KEY, betabandCheckbox.isChecked());
        getArguments().putBoolean(B13_KEY, b13Checkbox.isChecked());
        getArguments().putBoolean(TEMP_KEY, templateCheckbox.isChecked());
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

    // Create boolean matrix for determining what bands are allowed in the next portion
    public boolean[] calculateBands() {
        boolean[] bands = new boolean[8];
        bands[0] = fatsharkCheckbox.isChecked();
        bands[1] = boscamECheckbox.isChecked();
        bands[2] = boscamACheckbox.isChecked();
        bands[3] = racebandCheckbox.isChecked();
        bands[4] = betabandCheckbox.isChecked();
        bands[5] = b13Checkbox.isChecked();

        return bands;
    }

    // Create an empty arraylist of arraylists to prep and send to next step
    public ArrayList<ArrayList<AddFrequencySlot>> emptySlotFreqPackage() {
        ArrayList<ArrayList<AddFrequencySlot>> freqSlots = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            freqSlots.add(new ArrayList<AddFrequencySlot>());
        }

        return freqSlots;
    }

    public interface OnFragmentInteractionListener {
        void BuildRaceAToB(int numSlots, boolean[] bands, ArrayList<ArrayList<AddFrequencySlot>> freqSlots);
    }
}
