package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private final static String ROTATED_KEY = "rotated_key";

    private Race race;
    private OnRaceListener mListener;
    private static Handler handler = new Handler();
    private static CountdownRunnable countdownRunnable;
    private Boolean rotated = false;

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
        if (savedInstanceState != null) {
            rotated = savedInstanceState.getBoolean(ROTATED_KEY);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_add_event).setVisible(false);
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
        checkRaceStatus();
        onChangedViewPermissions();
        if (!rotated) {
            mListener.refreshRaceFragment(race);
        }
        else {
            rotated = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getActivity().isChangingConfigurations()) {
            rotated = true;
        }
        outState.putBoolean(ROTATED_KEY, rotated);
    }

    @Override
    public void onPause() {
        super.onPause();

        getArguments().putParcelable(RACE_KEY, race);
        getArguments().putBoolean(ROTATED_KEY, rotated);
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
        void onSendStatusUpdate(Race race, String status, String racers, String spotters, String onDeck, Long targetTime);
        void refreshRaceFragment(Race race);
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public void checkRaceStatus() {
        if (race.getStatus().split(" ")[0].equals("R")) {
            startTimer(race.getTargetTime());
        }
        else {
            stopTimer();
        }
    }

    // Use this as a "send new status to Firebase" button. Figure out who is racing, spotting,
    // on deck, etc. and build a JSON string. Then send it in a callback to MainActivity which will
    // then be able to send it to TaskFragment for updating server/Firebase.
    private void onTimerButton() {
        EditText editText = (EditText) getView().findViewById(R.id.race_time_input);
        String status = race.getStatus();

        // Logic to figure out which heats are relevant
        // TODO: All of the if statements can obviously be refactored into another single method
        int[] currentIndex = new int[2];
        int[] spotterIndex = new int[2];
        int[] onDeckIndex = new int[2];
        String state = "NS";
        String racers = "";
        String spotters = "";
        String onDeck = "";
        if (status.equals("NS")) {
            // Race "not started", get information from the very start and set "waiting" status
            // Current racers
            currentIndex = new int[] {0, 0};
            racers = currentIndex[0] == -1 || currentIndex[1] == -1 ? "N/A" : race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]).getAllRacers();
            spotterIndex = race.getNext(currentIndex);
            spotters = spotterIndex[0] == -1 || spotterIndex[1] == -1 ? "N/A" :  race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]).getAllRacers();
            onDeckIndex = race.getNext(spotterIndex);
            onDeck = onDeckIndex[0] == -1 || onDeckIndex[1] == -1 ? "N/A" :  race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]).getAllRacers();
            state = String.format("W %d %d", currentIndex[0], currentIndex[1]);
        }
        else if (status.charAt(0) == 'W') {
            // Race was in "waiting" status, set it to "racing" status. We should not need to do anything but change the state.
            currentIndex = new int[] {Integer.parseInt(status.split(" ")[1]), Integer.parseInt(status.split(" ")[2])};
            racers = currentIndex[0] == -1 || currentIndex[1] == -1 ? "N/A" : race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]).getAllRacers();
            spotterIndex = race.getNext(currentIndex);
            spotters = spotterIndex[0] == -1 || spotterIndex[1] == -1 ? "N/A" :  race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]).getAllRacers();
            onDeckIndex = race.getNext(spotterIndex);
            onDeck = onDeckIndex[0] == -1 || onDeckIndex[1] == -1 ? "N/A" :  race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]).getAllRacers();
            state = String.format("R %d %d", currentIndex[0], currentIndex[1]);
        }
        else if (status.charAt(0) == 'R') {
            // Race is finished. We should now move to the next "waiting" status if there are more heats, or to "finished"
            // Get the current index and see if next is valid or not
            currentIndex = new int[] {Integer.parseInt(status.split(" ")[1]), Integer.parseInt(status.split(" ")[2])};
            currentIndex = race.getNext(currentIndex);
            if (currentIndex[0] == -1 || currentIndex[1] == -1) {
                // No more valid heats or roudns - we're done. Set to "finished"
                state = "F";
            }
            else {
                // Otherwise, we still have more stuff to do, go to next "waiting" phase
                racers = race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]).getAllRacers();
                spotterIndex = race.getNext(currentIndex);
                spotters = spotterIndex[0] == -1 || spotterIndex[1] == -1 ? "N/A" :  race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]).getAllRacers();
                onDeckIndex = race.getNext(spotterIndex);
                onDeck = onDeckIndex[0] == -1 || onDeckIndex[1] == -1 ? "N/A" :  race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]).getAllRacers();
                state = String.format("W %d %d", currentIndex[0], currentIndex[1]);
            }
        }

        // If the race is not already finished, start process for sending update to Firebase
        if (!status.equals("F")) {
            // Figure out the target time based on the text input
            // Add 15 seconds worth to value just for buffer's sake
            Long targetTime = System.currentTimeMillis() + Long.parseLong(editText.getText().toString()) * 1000 + 15000;
            mListener.onSendStatusUpdate(race, state, racers, spotters, onDeck, targetTime);
        }
    }


    // For starting the timer with a synced target time
    public void startTimer(long targetTime) {
        TextView textView = (TextView) getView().findViewById(R.id.race_timer_ticker);
        countdownRunnable = new CountdownRunnable(textView, targetTime);
        handler.post(countdownRunnable);
    }

    public void stopTimer() {
        handler.removeCallbacks(countdownRunnable);
    }

    // Runnable to use with handler to update time text widget
    private class CountdownRunnable implements Runnable {

        final private TextView textView;
        final private long targetTime;
        private long currentTime;

        public CountdownRunnable(TextView textView, long targetTime) {
            this.textView = textView;
            this.targetTime = targetTime;
        }

        public void run() {
            currentTime = targetTime - System.currentTimeMillis();
            final String text = String.format("%02d:%02d:%03d", (currentTime / 60000) % 60, (currentTime / 1000) % 60, currentTime % 1000);
            if (currentTime > 0 ) {
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

    // Decide change what can be seen
    public void onChangedViewPermissions() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (checkPermissions()) {
                    getView().findViewById(R.id.race_countdown_button).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.race_time_input).setVisibility(View.VISIBLE);
                }
                else {
                    getView().findViewById(R.id.race_countdown_button).setVisibility(View.GONE);
                    getView().findViewById(R.id.race_time_input).setVisibility(View.GONE);
                }
            }
        });
    }

    // Check what this user can actually see. Probably need to break this up into two parts so that
    // one part checks it, and the other actually does something based on that (in case I need to
    // check permissions elsewhere like if a user can open a dialog or something).
    public Boolean checkPermissions() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = sharedPreferences.getString("username", null);
        return race.getAdmins().contains(username);

    }
}
