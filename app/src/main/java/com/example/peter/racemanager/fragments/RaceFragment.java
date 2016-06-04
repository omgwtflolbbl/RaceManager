package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Race;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RaceFragment.OnRaceListener} interface
 * to handle interaction events.
 * Use the {@link RaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaceFragment extends Fragment implements View.OnClickListener {
    private final static String RACE_KEY = "race_key";

    private Race race;

    private OnRaceListener mListener;

    public RaceFragment() {
        // Required empty public constructor
    }

    public static RaceFragment newInstance(Race race) {
        RaceFragment fragment = new RaceFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_race, container, false);
        Button raceInfoButton = (Button) view.findViewById(R.id.race_info_button);
        raceInfoButton.setOnClickListener(this);
        Button raceScheduleButton = (Button) view.findViewById(R.id.race_schedule_button);
        raceScheduleButton.setOnClickListener(this);
        Button raceRacersButton = (Button) view.findViewById(R.id.race_racers_button);
        raceRacersButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        onButtonPressed(view, race);
    }

    public void onButtonPressed(View view, Race race) {
        if (mListener != null) {
            mListener.onRaceButton(view, race);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRaceListener) {
            mListener = (OnRaceListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRaceListener {
        void onRaceButton(View view, Race race);
    }
}
