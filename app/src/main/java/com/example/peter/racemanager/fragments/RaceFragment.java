package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
    private final static String RUNNING_KEY = "running_key";

    private Race race;
    private OnRaceListener mListener;
    private Handler handler = new Handler();

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
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_race, container, false);
        Button countdownButton = (Button) view.findViewById(R.id.race_countdown_button);
        countdownButton.setOnClickListener(this);
        Button raceInfoButton = (Button) view.findViewById(R.id.race_info_button);
        raceInfoButton.setOnClickListener(this);
        Button raceScheduleButton = (Button) view.findViewById(R.id.race_schedule_button);
        raceScheduleButton.setOnClickListener(this);
        Button raceRacersButton = (Button) view.findViewById(R.id.race_racers_button);
        raceRacersButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args.size() > 0) {
            race = args.getParcelable(RACE_KEY);
        }
        if (race.getStatus().split(" ")[0].equals("R")) {
            Log.i("TARGET TIME IS", Long.toString(race.getTargetTime()));
            Log.i("CURRENT TIME IS", Long.toString(System.currentTimeMillis()));
            startTimer(race.getTargetTime());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RACE_KEY, race);
    }

    @Override
    public void onPause() {
        super.onPause();

        getArguments().putParcelable(RACE_KEY, race);
    }

    @Override
    public void onClick(View view) {
        onButtonPressed(view, race);
    }

    public void onButtonPressed(View view, Race race) {
        if (mListener != null && view.getId() != R.id.race_countdown_button) {
            mListener.onRaceButton(view, race);
        }
        else {
            onTimerButton();
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

    public interface OnRaceListener {
        void onRaceButton(View view, Race race);
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    private void onTimerButton() {
        TextView textView = (TextView) getView().findViewById(R.id.race_timer_ticker);
        handler.post(new countdownRunnable(textView, System.currentTimeMillis() + 120*1000));
    }

    public void startTimer(long targetTime) {
        TextView textView = (TextView) getView().findViewById(R.id.race_timer_ticker);
        handler.post(new countdownRunnable(textView, targetTime));
    }

    private class countdownRunnable implements Runnable {

        final private TextView textView;
        final private long targetTime;
        private long currentTime;

        public countdownRunnable(TextView textView, long targetTime) {
            this.textView = textView;
            this.targetTime = targetTime;
        }

        public void run() {
            currentTime = targetTime - System.currentTimeMillis();
            final String text = String.format("%02d:%02d:%03d", (currentTime / 60000) % 60, (currentTime / 1000) % 60, currentTime % 1000);
            if (currentTime > 0) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (textView != null) {
                                textView.setText(text);
                            }
                        }
                    });
                    handler.postDelayed(this, 33);
                }
            }
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("00:00:000");
                    }
                });
            }
        }
    }
}
