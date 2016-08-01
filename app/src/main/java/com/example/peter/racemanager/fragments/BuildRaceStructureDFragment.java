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

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Heat;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;
import com.example.peter.racemanager.models.Round;
import com.example.peter.racemanager.models.Slot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuildRaceStructureDFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuildRaceStructureDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildRaceStructureDFragment extends Fragment {

    private final static String RACE_KEY = "RACE_KEY";
    private final static String[] RACERS_IN_SLOTS_KEY = {"RACERS_A_KEY", "RACERS_B_KEY", "RACERS_C_KEY", "RACERS_D_KEY", "RACERS_E_KEY", "RACERS_F_KEY", "RACERS_G_KEY", "RACERS_H_KEY"};
    private final static String SHUFFLE_KEY = "SHUFFLE_KEY";

    private Race oldRace;
    private Race newRace;
    private List<List<Racer>> racersInSlots;

    // UI stuff
    EditText numRoundsText;
    CheckedTextView[] shuffleBoxes;

    private OnFragmentInteractionListener mListener;

    public BuildRaceStructureDFragment() {
        // Required empty public constructor
    }

    public static BuildRaceStructureDFragment newInstance(List<List<Racer>> racersInSlots, Race race) {
        BuildRaceStructureDFragment fragment = new BuildRaceStructureDFragment();
        Bundle args = new Bundle();
        for (int i = 0; i < RACERS_IN_SLOTS_KEY.length; i++) {
            args.putParcelableArrayList(RACERS_IN_SLOTS_KEY[i], (ArrayList<Racer>) racersInSlots.get(i));
        }
        args.putParcelable(RACE_KEY, race);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            racersInSlots = new ArrayList<>();
            for (int i = 0; i < RACERS_IN_SLOTS_KEY.length; i++) {
                ArrayList<Racer> racers = getArguments().getParcelableArrayList(RACERS_IN_SLOTS_KEY[i]);
                racersInSlots.add(racers);
            }
            oldRace = getArguments().getParcelable(RACE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_race_structure_d, container, false);

        numRoundsText = (EditText) view.findViewById(R.id.build_race_d_number_rounds);

        // Get all choice boxes
        shuffleBoxes = new CheckedTextView[]{
                (CheckedTextView) view.findViewById(R.id.build_race_d_choice_0),
                (CheckedTextView) view.findViewById(R.id.build_race_d_choice_1),
                (CheckedTextView) view.findViewById(R.id.build_race_d_choice_2)};

        // Set on click listeners
        for (CheckedTextView choice : shuffleBoxes) {
            choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setShuffleChoice(v);
                }
            });
        }

        // Set initial selected box choice - no shuffle as default
        if (getArguments().containsKey(SHUFFLE_KEY)) {
            setShuffleChoice(shuffleBoxes[getArguments().getInt(SHUFFLE_KEY)]);
        }
        else {
            setShuffleChoice(shuffleBoxes[0]);
        }

        TextView finishButton = (TextView) view.findViewById(R.id.build_race_d_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!numRoundsText.getText().toString().equals("")) {
                    if (Integer.parseInt(numRoundsText.getText().toString()) > 0) {
                        onFinish();
                    }
                }
            }
        });

        TextView cancelButton = (TextView) view.findViewById(R.id.build_race_d_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        getArguments().putInt(SHUFFLE_KEY, getShuffleChoice());
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

    // Calculates the number of usable slots
    public int calculateNumberOfSlots() {
        int numSlots = 0;

        for (int i = 0; i < racersInSlots.size(); i++) {
            if (racersInSlots.get(i).size() != 0) {
                numSlots = i+1;
            }
        }

        return numSlots;
    }

    // Calculates the number of heats in a round
    public int calculateNumberOfHeats() {
        int numHeats = 0;

        for (int i = 0; i < racersInSlots.size(); i++) {
            if (racersInSlots.get(i).size() > numHeats) {
                numHeats = racersInSlots.get(i).size();
            }
        }

        return numHeats;
    }

    // Method shuffles the order of the racers in each slot
    public void shuffleSlots() {
        for (int i = 0; i < racersInSlots.size(); i++) {
            Collections.shuffle(racersInSlots.get(i));
        }
    }

    // Generates a new structure that can be used to put into a Race object
    public ArrayList<Round> generateNewRoundStructure() {
        ArrayList<Round> rounds = new ArrayList<>();

        int numRounds = Integer.parseInt(numRoundsText.getText().toString());
        int numHeats = calculateNumberOfHeats();
        int numSlots = calculateNumberOfSlots();

        // If the structure will be shuffled only once for randomness, it should to be now
        if (getShuffleChoice() == 1) {
            shuffleSlots();
        }
        for (int i = 0; i < numRounds; i++) {
            Round round = new Round();

            // If the structure will be shuffled each round, it should be checked here each loop
            if (getShuffleChoice() == 2) {
                shuffleSlots();
            }
            for (int j = 0; j < numHeats; j++) {
                // Create a new heat to put slots into
                Heat heat = new Heat();
                for (int k = 0; k < numSlots; k++) {
                    Slot slot;
                    // Check to see if there is actually a corresponding racer here
                    if (j < racersInSlots.get(k).size()) {
                        slot = new Slot(racersInSlots.get(k).get(j).getUsername(), racersInSlots.get(k).get(j).getFrequency(), 0);
                    }
                    // Otherwise, this slot will be empty for this heat
                    else {
                        slot = new Slot();
                    }
                    heat.addSlot(Character.toString((char) (65+k)), slot);
                }
                // Add build up heat to current round
                round.addHeat(heat);
            }
            // Add build up rounds to total structure
            rounds.add(round);
        }

        return rounds;
    }

    // Generates a new list of racers that can be put into a Race object
    public ArrayList<Racer> generateNewRacersList() {
        ArrayList<Racer> racers = new ArrayList<> ();

        for (int i = 0; i < racersInSlots.size(); i++) {
            for (int j = 0; j < racersInSlots.get(i).size(); j++) {
                racers.add(racersInSlots.get(i).get(j));
            }
        }

        return racers;
    }

    public Race generateNewRace() {
        ArrayList<Round> rounds = generateNewRoundStructure();
        ArrayList<Racer> racers = generateNewRacersList();

        Race race = new Race(oldRace.getTitle(), oldRace.getSiteURL(), oldRace.getDate(), oldRace.getTime(), oldRace.getBlockquote(), oldRace.getDescription(), oldRace.getRaceImage(), rounds, racers, oldRace.getAdmins(), "NS", oldRace.getTargetTime());

        return race;
    }

    // Method to emulate radio button group behavior for checkedtextviews
    public void setShuffleChoice(View selected) {
        // Set all to unchecked
        for (CheckedTextView view : shuffleBoxes) {
            view.setChecked(false);
        }

        ((CheckedTextView) selected).setChecked(true);
    }

    // Figures out which shuffle choice is selected
    public int getShuffleChoice() {
        for (int i = 0; i < shuffleBoxes.length; i++) {
            if (shuffleBoxes[i].isChecked()) {
                return i;
            }
        }
        return -1;
    }

    public void onFinish() {
        int shuffleChoice = getShuffleChoice();

        System.out.println("shuffleChoice: " + shuffleChoice);

        Race race = generateNewRace();

        mListener.onFinishWizard(race);
    }

    public interface OnFragmentInteractionListener {
        //void onFragmentInteraction(Uri uri);
        void onFinishWizard(Race race);
    }
}
