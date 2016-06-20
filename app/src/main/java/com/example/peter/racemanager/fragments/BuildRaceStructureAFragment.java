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

    // Logic stuff
    private int numRacers;
    private boolean fatshark;
    private boolean boscamE;
    private boolean boscamA;
    private boolean raceband;
    private boolean betaband;
    private boolean b13;

    // UI stuff
    private EditText numRacersText;
    private CheckedTextView fatsharkCheckbox;
    private CheckedTextView boscamECheckbox;
    private CheckedTextView boscamACheckbox;
    private CheckedTextView racebandCheckbox;
    private CheckedTextView betabandCheckbox;
    private CheckedTextView b13Checkbox;
    private TextView continueButton;
    private TextView cancelButton;

    private OnFragmentInteractionListener mListener;

    public BuildRaceStructureAFragment() {
        // Required empty public constructor
    }

    public static BuildRaceStructureAFragment newInstance(int numRacers, boolean fatshark, boolean boscamE, boolean boscamA, boolean raceband, boolean betaband, boolean b13) {
        BuildRaceStructureAFragment fragment = new BuildRaceStructureAFragment();
        Bundle args = new Bundle();
        args.putInt(NUM_KEY, numRacers);
        args.putBoolean(FS_KEY, fatshark);
        args.putBoolean(BE_KEY, boscamE);
        args.putBoolean(BA_KEY, boscamA);
        args.putBoolean(R_KEY, raceband);
        args.putBoolean(B_KEY, betaband);
        args.putBoolean(B13_KEY, b13);
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
            b13 = getArguments().getBoolean(B13_KEY, b13);
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
        void onFragmentInteraction(Uri uri);
    }
}
