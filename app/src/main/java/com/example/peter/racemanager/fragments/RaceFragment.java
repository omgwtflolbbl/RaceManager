package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.peter.racemanager.FontManager;
import com.example.peter.racemanager.R;
import com.example.peter.racemanager.activities.MainActivity;
import com.example.peter.racemanager.adapters.RoundAdapter3;
import com.example.peter.racemanager.models.Heat;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;
import com.example.peter.racemanager.models.Slot;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.squareup.picasso.Picasso;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RaceFragment.OnRaceListener} interface
 * to handle interaction events.
 * Use the {@link RaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaceFragment extends Fragment implements View.OnClickListener, JumpHeatDialogFragment.JumpHeatDialogListener, RoundAdapter3.onSlotSelectListener, ChangeSlotDialogFragment.ChangeSlotDialogListener {

    private final static String RACE_KEY = "race_key";
    private final static String ROTATED_KEY = "rotated_key";

    private Race race;
    private OnRaceListener mListener;
    private final static Handler handler = new Handler();
    private static CountdownRunnable countdownRunnable;
    private static CountdownRunnable countdownRunnable2;
    private Boolean rotated = false;
    private List<Heat> lastHeatList;
    private List<Heat> currentHeatList;
    private List<Heat> nextHeatList;

    // UI stuff
    private CircleImageView racerImage;
    private TextView currentStatusText;
    private Button flowButton;
    private Button jumpButton;
    private Button attendanceButton;
    private TextView racerName;
    private TextView racerFrequency;
    private TextView racerPoints;
    private TextView iconFrequency;
    private TextView iconPoints;
    private TextView lastHeatText;
    private ListView lastHeatView;
    private TextView currentHeatText;
    private ListView currentHeatView;
    private TextView nextHeatText;
    private ListView nextHeatView;
    private RelativeLayout statusBar;

    // Text to Speech
    private static TextToSpeech textToSpeech;

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

        Iconify.with(new FontAwesomeModule());

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float) .9);
            }
        });

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

        racerImage = (CircleImageView) view.findViewById(R.id.race_racer_image);
        racerName = (TextView) view.findViewById(R.id.race_racer_name);
        racerFrequency = (TextView) view.findViewById(R.id.race_racer_frequency);
        racerPoints = (TextView) view.findViewById(R.id.race_racer_points);

        // Set up icons in top bar
        iconFrequency = (TextView) view.findViewById(R.id.race_icon_frequency);
        iconFrequency.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME));
        iconPoints = (TextView) view.findViewById(R.id.race_icon_points);
        iconPoints.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME));

        currentStatusText = (TextView) view.findViewById(R.id.race_current_status_text);
        statusBar = (RelativeLayout) view.findViewById(R.id.race_status_bar);

        Button raceScheduleButton = (Button) view.findViewById(R.id.race_schedule_button);
        raceScheduleButton.setOnClickListener(this);

        attendanceButton = (Button) view.findViewById(R.id.race_attendance_button);
        attendanceButton.setTransformationMethod(null);
        attendanceButton.setText(Iconify.compute(attendanceButton.getContext(), getResources().getString(R.string.button_import_roster)));
        attendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.getUpdatedAttendance(race);
            }
        });

        flowButton = (Button) view.findViewById(R.id.race_admin_flow_button);
        flowButton.setTransformationMethod(null);
        flowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimerButton();
                setFlowButtonText();
            }
        });

        jumpButton = (Button) view.findViewById(R.id.race_admin_jump_button);
        jumpButton.setTransformationMethod(null);
        jumpButton.setText(Iconify.compute(jumpButton.getContext(), getResources().getString(R.string.button_change_heat)));
        jumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJumpHeatDialog();
            }
        });


        lastHeatText = (TextView) view.findViewById(R.id.race_last_heat_text);
        lastHeatView = (ListView) view.findViewById(R.id.race_current_heat);
        lastHeatList = new ArrayList<>();

        currentHeatText = (TextView) view.findViewById(R.id.race_current_heat_text);
        currentHeatView = (ListView) view.findViewById(R.id.race_spotter_heat);
        currentHeatList = new ArrayList<>();

        nextHeatText = (TextView) view.findViewById(R.id.race_next_heat_text);
        nextHeatView = (ListView) view.findViewById(R.id.race_ondeck_heat);
        nextHeatList = new ArrayList<>();

        prepareHeatViews();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        stopTimer();

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
        textToSpeech.shutdown();
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

    public interface OnRaceListener {
        void onRaceButton(View view, Race race);
        void onSendStatusUpdate(Race race, int round, int heat);
        void onUpdateSlotOnServer(Race race, Slot slot, String tag);
        void refreshRaceFragment(Race race);
        void getUpdatedAttendance(Race race);
        void resyncTime();
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public void checkRaceStatus() {
        if (race.getCurrentState().equals("R")) {
            stopTimer();
            startTimer(race.getTargetTime());
        }
        else {
            stopTimer();
        }
        setCurrentStatusText();
        setFlowButtonText();
        setRacerText();
        prepareHeatViews();
        setBasicCardSpacing();
    }

    // Controls the status displayed on top of countdown widget
    public void setCurrentStatusText() {
        if (race.getCurrentState().equals("NS")) {
            currentStatusText.setText(R.string.status_not_started);
            statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPBlue));
        }
        else if (race.getCurrentState().equals("F")) {
            currentStatusText.setText(R.string.status_finished);
            statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPGray));
        }
        else {
            String statusVar = String.format(Locale.US, "Round %d - Heat %d", race.getCurrentRound() + 1, race.getCurrentHeat() + 1);
            SpannableString statusSpan = new SpannableString(statusVar);
            statusSpan.setSpan(new UnderlineSpan(), 0, statusVar.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (race.getCurrentState().equals("W")) {
                SpannedString statusText = (SpannedString) TextUtils.concat(statusSpan, " is Prepping");
                currentStatusText.setText(statusText);
                statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPOrange));
            }
            else  {
                SpannedString statusText = (SpannedString) TextUtils.concat(statusSpan, " is Racing");
                currentStatusText.setText(statusText);
                statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPGreen));
            }

        }
    }

    // Manages the flow button for admins
    public void setFlowButtonText() {

        if (race.getCurrentState().equals("NS")) {
            flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_start)));
        }
        else if (race.getCurrentState().equals("F")) {
            flowButton.setText(getResources().getText(R.string.button_flow_done));
        }
        else if (race.getCurrentState().equals("W")) {
            flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_heat)));
        }
        else {
            int[] nextIndex = race.getNext();
            if (nextIndex[0] == -1 || nextIndex[1] == -1) {
                // No more valid heats or rounds - we're done. Set to "finished"
                flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_finish)));
            }
            else {
                flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_prep)));
            }
        }
    }

    public void setRacerText() {
        if (checkRacerPermissions()) {
            // Setup pilot information
            String username = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", null);
            race.calculatePoints();
            for (Racer racer : race.getRacers()) {
                if (racer.getUsername().equals(username)) {
                    if (racer.getRacerPhoto() != null) {
                        Picasso.with(getContext())
                                .load(racer.getRacerPhoto())
                                .error(R.drawable.profile)
                                .noFade()
                                .into(racerImage);
                    }
                    racerName.setText(username);
                    racerFrequency.setText(racer.getFrequency() != null ? racer.getFrequency() : "-");
                    racerPoints.setText(Integer.toString(racer.getPoints()));
                }
            }
        }
        else {
            // Setup guest view
            Picasso.with(getContext())
                    .load(R.drawable.profile)
                    .noFade()
                    .into(racerImage);
            racerName.setText(R.string.name_guest);
            racerFrequency.setText("-");
            racerPoints.setText("-");
        }
    }

    // Opens up a dialog that allows admin user to jump to a specified heat
    private void showJumpHeatDialog() {
        FragmentManager fm = getChildFragmentManager();
        JumpHeatDialogFragment dialog = JumpHeatDialogFragment.newInstance(race);
        dialog.show(fm, "some_unknown_text");
    }

    // On positive completion of jump heat dialog - moves race to waiting position of selected heat
    public void onFinishJumpHeatDialog(String newState) {
        int[] currentIndex = new int[] {Integer.parseInt(newState.split(" ")[1]), Integer.parseInt(newState.split(" ")[2])};
        String racers = currentIndex[0] == -1 || currentIndex[1] == -1 ? "N/A" : race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]).getAllRacers();
        int[] spotterIndex = race.getNext();
        String spotters = spotterIndex[0] == -1 || spotterIndex[1] == -1 ? "N/A" :  race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]).getAllRacers();
        int[] onDeckIndex = race.getNext();
        String onDeck = onDeckIndex[0] == -1 || onDeckIndex[1] == -1 ? "N/A" :  race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]).getAllRacers();

        race.setTargetTime(1);
        mListener.onSendStatusUpdate(race, currentIndex[0], currentIndex[1]);
    }

    // Use this as a "send new status to Firebase" button. Figure out who is racing, spotting,
    // on deck, etc. and build a JSON string. Then send it in a callback to MainActivity which will
    // then be able to send it to TaskFragment for updating server/Firebase.
    private void onTimerButton() {
        if (getView() != null) {
            EditText editText = (EditText) getView().findViewById(R.id.race_time_input);

            // Logic to figure out which heats are relevant
            // TODO: All of the if statements can obviously be refactored into another single method
            int[] currentIndex = new int[2];
            int[] spotterIndex = new int[2];
            int[] onDeckIndex = new int[2];
            String state = "NS";
            String racers = "";
            String spotters = "";
            String onDeck = "";
            switch (race.getCurrentState()) {
                case "NS":
                    // Race "not started", get information from the very start and set "waiting" status
                    // Current racers
                    currentIndex = new int[]{0, 0};
                    racers = currentIndex[0] == -1 || currentIndex[1] == -1 ? "N/A" : race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]).getAllRacers();
                    spotterIndex = race.getNext();
                    spotters = spotterIndex[0] == -1 || spotterIndex[1] == -1 ? "N/A" : race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]).getAllRacers();
                    onDeckIndex = race.getNext();
                    onDeck = onDeckIndex[0] == -1 || onDeckIndex[1] == -1 ? "N/A" : race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]).getAllRacers();

                    race.setCurrentState("W");
                    race.setTargetTime(1);
                    stopTimer();
                    mListener.onSendStatusUpdate(race, currentIndex[0], currentIndex[1]);
                    break;

                case "W":
                    // Race was in "waiting" status, set it to "racing" status. We should not need to do anything but change the state.
                    currentIndex = new int[]{race.getCurrentRound(), race.getCurrentHeat()};
                    racers = currentIndex[0] == -1 || currentIndex[1] == -1 ? "N/A" : race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]).getAllRacers();
                    spotterIndex = race.getNext();
                    spotters = spotterIndex[0] == -1 || spotterIndex[1] == -1 ? "N/A" : race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]).getAllRacers();
                    onDeckIndex = race.getNext();
                    onDeck = onDeckIndex[0] == -1 || onDeckIndex[1] == -1 ? "N/A" : race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]).getAllRacers();

                    race.setCurrentState("R");
                    long targetTime = System.currentTimeMillis() + TaskFragment.SntpOffset + Long.parseLong(editText.getText().toString()) * 1000 + 5000;
                    race.setTargetTime(targetTime);
                    stopTimer();
                    startTimer(targetTime);
                    mListener.onSendStatusUpdate(race, currentIndex[0], currentIndex[1]);
                    break;

                case "F":
                    break;

                default:
                    // Heat is finished. We should now move to the next "waiting" status if there are more heats, or to "finished"
                    // Get the current index and see if next is valid or not
                    currentIndex = race.getNext();
                    if (currentIndex[0] == -1 || currentIndex[1] == -1) {
                        // No more valid heats or roudns - we're done. Set to "finished"
                        race.setCurrentState("F");
                        race.setTargetTime(-1);
                        stopTimer();
                    } else {
                        // Otherwise, we still have more stuff to do, go to next "waiting" phase
                        racers = race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]).getAllRacers();
                        spotterIndex = race.getNext();
                        spotters = spotterIndex[0] == -1 || spotterIndex[1] == -1 ? "N/A" : race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]).getAllRacers();
                        onDeckIndex = race.getNext();
                        onDeck = onDeckIndex[0] == -1 || onDeckIndex[1] == -1 ? "N/A" : race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]).getAllRacers();

                        race.setCurrentState("W");
                        race.setTargetTime(1);
                        stopTimer();
                    }
                    mListener.onSendStatusUpdate(race, currentIndex[0], currentIndex[1]);
                    break;
            }
        }
    }


    // For starting the timer with a synced target time
    public void startTimer(long targetTime) {
        if (getView() != null) {
            TextView adminTicker = (TextView) getView().findViewById(R.id.race_timer_ticker_admin);
            countdownRunnable = new CountdownRunnable(adminTicker, targetTime, -1, true);
            TextView smallTicker = (TextView) getView().findViewById(R.id.race_timer_ticker_pilot);
            countdownRunnable2 = new CountdownRunnable(smallTicker, targetTime, -1, false);
            handler.post(countdownRunnable);
            handler.post(countdownRunnable2);
        }
    }

    public void stopTimer() {
        handler.removeCallbacksAndMessages(null);
        if (getView() != null && !race.getCurrentState().equals("R")) {
            TextView adminTicker = (TextView) getView().findViewById(R.id.race_timer_ticker_admin);
            adminTicker.setText("00:00:000");
            TextView smallTicker = (TextView) getView().findViewById(R.id.race_timer_ticker_pilot);
            smallTicker.setText("00:00:000");
        }
    }

    // Runnable to use with handler to update time text widget
    private class CountdownRunnable implements Runnable {

        final private TextView textView;
        final private long targetTime;
        private long previousTime;
        private long currentTime;
        private boolean update;

        private final long[] checkpoints = {1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 15000, 30000, 60000, 90000, 120000, 150000, 180000, 210000, 240000, 270000, 300000};

        public CountdownRunnable(TextView textView, long targetTime, long previousTime, boolean update) {
            this.textView = textView;
            this.targetTime = targetTime;
            this.previousTime = previousTime;
            this.update = update;
        }

        public void run() {
            currentTime = targetTime - System.currentTimeMillis() - (TaskFragment.SntpOffset == null? 0 : TaskFragment.SntpOffset);
            final String text = String.format("%02d:%02d:%03d", (currentTime / 60000) % 60, (currentTime / 1000) % 60, currentTime % 1000);
            if (currentTime > 0 ) {
                if (getActivity() != null) {
                    // Update clock
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (textView != null) {
                                textView.setText(text);
                            }
                        }
                    });

                    // Timer readouts/alerts
                    for (long checkpoint : checkpoints) {
                        if (update && previousTime > checkpoint && currentTime <= checkpoint && (((MainActivity) getActivity()).getActiveFragment() instanceof OverviewFragment)) {
                            // Start vibrating until up to 5 seconds past the target time
                            Vibrator v = (Vibrator) getParentFragment().getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(500);

                            // Build string for verbal warning
                            String speech = "";
                            // If checkpoint > 10s, we want to include "minutes" and "seconds" in speech
                            if (checkpoint > 10000) {
                                // Plural or singular minute calls
                                if (checkpoint > 90000) {
                                    speech += String.format(Locale.US, "%d minutes", checkpoint / 1000 / 60);
                                }
                                else if (checkpoint > 30000) {
                                    speech += String.format(Locale.US, "%d minute", checkpoint / 1000 / 60);
                                }
                                // Seconds calls if not a flat 0
                                if ((checkpoint / 1000) % 60 != 0) {
                                    speech += String.format(Locale.US, " %d seconds", (checkpoint / 1000) % 60);
                                }
                            }
                            // Otherwise, we're at 10- seconds on THE FINAL COUNTDOOOOWN DO DO DOO DOOOO DO DO DOOT DOOT DOOOO
                            else {
                                speech = Long.toString(checkpoint / 1000);
                            }

                            textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                            if (checkpoint % 30000 == 0 || checkpoint == 15000) {
                                Log.d("CP", Long.toString(checkpoint));
                                ((MainActivity) getActivity()).resyncTime();
                            }
                        }
                    }
                    previousTime = currentTime;
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

                if (previousTime > 0 && currentTime <= 0) {
                    // Start vibrating until up to 5 seconds past the target time
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(2000);

                    textToSpeech.speak("Time's up", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

    public void prepareHeatViews() {
        String status = race.getCurrentState() + " " + race.getCurrentRound() + " " + race.getCurrentHeat();
        Log.d("current status", status);

        if (race.getCurrentState().equals("NS") || race.getCurrentState().equals("F")) {
            lastHeatView.setVisibility(View.GONE);
            lastHeatText.setVisibility(View.GONE);
            currentHeatView.setVisibility(View.GONE);
            currentHeatText.setVisibility(View.GONE);
            nextHeatView.setVisibility(View.GONE);
            nextHeatText.setVisibility(View.GONE);
        }
        else {
            int[] currentIndex = race.getCurrent();
            int[] lastIndex = race.getPrevious();
            int[] nextIndex = race.getNext();

            if (!(lastIndex[0] == -1 || lastIndex[1] == -1)) {
                lastHeatView.setVisibility(View.VISIBLE);
                lastHeatText.setVisibility(View.VISIBLE);
                lastHeatList.clear();
                lastHeatList.add(race.getRounds().get(lastIndex[0]).getHeat(lastIndex[1]));
                RoundAdapter3 lastHeatAdapter;
                if (lastHeatView.getAdapter() == null) {
                    lastHeatAdapter = new RoundAdapter3(getContext(), (ArrayList<Heat>) lastHeatList, lastIndex[0], lastIndex[1], status, this);
                    lastHeatView.setAdapter(lastHeatAdapter);
                } else {
                    lastHeatAdapter = (RoundAdapter3) lastHeatView.getAdapter();
                    lastHeatAdapter.setRoundIndex(lastIndex[0]);
                    lastHeatAdapter.setHeatIndex(lastIndex[1]);
                    lastHeatAdapter.setStatus(status);
                    lastHeatAdapter.notifyDataSetChanged();
                }
            }
            else {
                lastHeatView.setVisibility(View.GONE);
                lastHeatText.setVisibility(View.GONE);
            }

            if (!(currentIndex[0] == -1 || currentIndex[1] == -1)) {
                currentHeatView.setVisibility(View.VISIBLE);
                currentHeatText.setVisibility(View.VISIBLE);
                currentHeatList.clear();
                currentHeatList.add(race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]));
                RoundAdapter3 currentHeatAdapter;
                if (currentHeatView.getAdapter() == null) {
                    currentHeatAdapter = new RoundAdapter3(getContext(), (ArrayList<Heat>) currentHeatList, currentIndex[0], currentIndex[1], status, this);
                    currentHeatView.setAdapter(currentHeatAdapter);
                } else {
                    currentHeatAdapter = (RoundAdapter3) currentHeatView.getAdapter();
                    currentHeatAdapter.setRoundIndex(currentIndex[0]);
                    currentHeatAdapter.setHeatIndex(currentIndex[1]);
                    currentHeatAdapter.setStatus(status);
                    currentHeatAdapter.notifyDataSetChanged();
                }
            }
            else {
                currentHeatView.setVisibility(View.GONE);
                currentHeatText.setVisibility(View.GONE);
            }

            if (!(nextIndex[0] == -1 || nextIndex[1] == -1)) {
                nextHeatView.setVisibility(View.VISIBLE);
                nextHeatText.setVisibility(View.VISIBLE);
                nextHeatList.clear();
                nextHeatList.add(race.getRounds().get(nextIndex[0]).getHeat(nextIndex[1]));
                RoundAdapter3 ondeckHeatAdapter;
                if (nextHeatView.getAdapter() == null) {
                    ondeckHeatAdapter = new RoundAdapter3(getContext(), (ArrayList<Heat>) nextHeatList, nextIndex[0], nextIndex[1], status, this);
                    nextHeatView.setAdapter(ondeckHeatAdapter);
                } else {
                    ondeckHeatAdapter = (RoundAdapter3) nextHeatView.getAdapter();
                    ondeckHeatAdapter.setRoundIndex(nextIndex[0]);
                    ondeckHeatAdapter.setHeatIndex(nextIndex[1]);
                    ondeckHeatAdapter.setStatus(status);
                    ondeckHeatAdapter.notifyDataSetChanged();
                }
            }
            else {
                nextHeatView.setVisibility(View.GONE);
                nextHeatText.setVisibility(View.GONE);
            }
        }
    }

    // Adds margin between heat cards and basic navigation card if heat cards are present
    public void setBasicCardSpacing() {
        if (getView() != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getView().findViewById(R.id.race_card_basic).getLayoutParams();
            if (lastHeatView.getVisibility() == View.GONE && currentHeatView.getVisibility() == View.GONE && nextHeatView.getVisibility() == View.GONE) {
                params.topMargin = 0;
            }
            else {
                params.topMargin = (int) (8 * Resources.getSystem().getDisplayMetrics().density);
            }
        }
    }

    // For when the heat cards have their slots touched
    public void showChangeSlotDialog(View view) {
        FragmentManager fm = getChildFragmentManager();
        String[] tag = view.getTag().toString().split(" ");
        ChangeSlotDialogFragment dialog = ChangeSlotDialogFragment.newInstance(race.getRounds().get(Integer.parseInt(tag[0])).getHeat(Integer.parseInt(tag[1])).getSlot(tag[2]), view.getTag().toString(), race);
        dialog.show(fm, "change slot dialog");
    }

    // On dialog finish
    public void onFinishChangeSlotDialog(int points, boolean remove, Slot slot, String tag, String newUser) {
        if (remove) {
            if (newUser.equals("Empty slot")) {
                slot.setUsername("EMPTY SLOT");
            }
            else {
                for (int i = 0, size = race.getRacers().size(); i < size; i++) {
                    if (race.getRacers().get(i).getUsername().equals(newUser)) {
                        slot.setRacer(race.getRacers().get(i));
                        slot.setPoints(points);
                    }
                }
            }
        }
        else {
            slot.setPoints(points);
        }

        mListener.onUpdateSlotOnServer(race, slot, tag);
    }

    // If the user performed a forbidden action
    public void showPermissionError() {
        FragmentManager fm = getChildFragmentManager();
        PermissionErrorDialogFragment dialog = new PermissionErrorDialogFragment();
        dialog.show(fm, "permission_error");
    }

    // Decide change what can be seen
    public void onChangedViewPermissions() {
        if (getView() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (checkAdminPermissions()) {
                        getView().findViewById(R.id.race_card_admin).setVisibility(View.VISIBLE);
                    }
                    else {
                        getView().findViewById(R.id.race_card_admin).setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    // Check what this user can actually see. Probably need to break this up into two parts so that
    // one part checks it, and the other actually does something based on that (in case I need to
    // check permissions elsewhere like if a user can open a dialog or something).
    public Boolean checkAdminPermissions() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String authorization = sharedPreferences.getString("authorization", "user");
        //return authorization.equals("Administrator");
        return true;
    }

    public Boolean checkRacerPermissions() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = sharedPreferences.getString("username", null);
        boolean racing = false;
        Log.d("null", race.toString());
        for (Racer racer : race.getRacers()) {
            if (username.equals(racer.getUsername())) {
                racing = true;
            }
        }
        return racing;
    }
}
