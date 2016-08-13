package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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

import com.example.peter.racemanager.CustomTypefaceSpan;
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
import com.joanzapata.iconify.widget.IconButton;
import com.squareup.picasso.Picasso;

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
    private List<Heat> currentHeatList;
    private List<Heat> spotterHeatList;
    private List<Heat> ondeckHeatList;

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
    private ListView currentHeatView;
    private ListView spotterHeatView;
    private ListView ondeckHeatView;
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

        // Set up fontawesome
        //Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        //FontManager.markAsIconContainer(view.findViewById(R.id.race_card_racer), iconFont);

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

        Button raceBuilderButton = (Button) view.findViewById(R.id.race_builder_button);
        raceBuilderButton.setOnClickListener(this);
        Button raceInfoButton = (Button) view.findViewById(R.id.race_info_button);
        raceInfoButton.setOnClickListener(this);
        Button raceScheduleButton = (Button) view.findViewById(R.id.race_schedule_button);
        raceScheduleButton.setOnClickListener(this);
        Button raceRacersButton = (Button) view.findViewById(R.id.race_racers_button);
        raceRacersButton.setOnClickListener(this);

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


        currentHeatView = (ListView) view.findViewById(R.id.race_current_heat);
        currentHeatList = new ArrayList<>();

        spotterHeatView = (ListView) view.findViewById(R.id.race_spotter_heat);
        spotterHeatList = new ArrayList<>();

        ondeckHeatView = (ListView) view.findViewById(R.id.race_ondeck_heat);
        ondeckHeatList = new ArrayList<>();

        prepareHeatViews();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float) .9);
            }
        });

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
        void onSendStatusUpdate(Race race, String status, String racers, String spotters, String onDeck, Long targetTime);
        void onUpdateSlotOnServer(Race race, Slot slot, String tag);
        void refreshRaceFragment(Race race);
        void getUpdatedAttendance(Race race);
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
        setCurrentStatusText();
        setFlowButtonText();
        setRacerText();
        prepareHeatViews();
        setBasicCardSpacing();
    }

    // Controls the status displayed on top of countdown widget
    public void setCurrentStatusText() {
        String status = race.getStatus();
        if (status.equals("NS")) {
            currentStatusText.setText("The race is still being set up");
            statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPBlue));
        }
        else if (status.charAt(0) == 'F') {
            currentStatusText.setText("The race is finished!");
            statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPGray));
        }
        else {
            String statusVar = String.format(Locale.US, "Round %d - Heat %d", Integer.parseInt(status.split(" ")[1]) + 1, Integer.parseInt(status.split(" ")[2]) + 1);
            SpannableString statusSpan = new SpannableString(statusVar);
            statusSpan.setSpan(new UnderlineSpan(), 0, statusVar.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (status.charAt(0) == 'W') {
                SpannedString statusText = (SpannedString) TextUtils.concat(statusSpan, " is Prepping");
                currentStatusText.setText(statusText);
                statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPOrange));
            }
            else if (status.charAt(0) == 'R') {
                SpannedString statusText = (SpannedString) TextUtils.concat(statusSpan, " is Racing");
                currentStatusText.setText(statusText);
                statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MultiGPGreen));
            }
            else if (status.charAt(0) == 'T') {
                currentStatusText.setText(String.format(Locale.US, "Round %d - Heat %d is tallying results", Integer.parseInt(status.split(" ")[1]) + 1, Integer.parseInt(status.split(" ")[2]) + 1));
                statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.redA100));
            }

        }
    }

    // Manages the flow button for admins
    public void setFlowButtonText() {
        String status = race.getStatus();

        if (status.equals("NS")) {
            flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_start)));
        }
        else if (status.charAt(0) == 'W') {
            flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_heat)));
        }
        else if (status.charAt(0) == 'R') {
            int[] currentIndex = new int[] {Integer.parseInt(status.split(" ")[1]), Integer.parseInt(status.split(" ")[2])};
            currentIndex = race.getNext(currentIndex);
            if (currentIndex[0] == -1 || currentIndex[1] == -1) {
                // No more valid heats or roudns - we're done. Set to "finished"
                flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_finish)));
            }
            else {
                flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_prep)));
            }
        }
        else if (status.charAt(0) == 'T') {
            int[] currentIndex = new int[] {Integer.parseInt(status.split(" ")[1]), Integer.parseInt(status.split(" ")[2])};
            currentIndex = race.getNext(currentIndex);
            if (currentIndex[0] == -1 || currentIndex[1] == -1) {
                // No more valid heats or roudns - we're done. Set to "finished"
                flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_finish)));
            }
            else {
                flowButton.setText(Iconify.compute(flowButton.getContext(), getResources().getString(R.string.button_flow_prep)));
            }
        }
        else if (status.charAt(0) == 'F') {
            flowButton.setText(getResources().getText(R.string.button_flow_done));
        }
    }

    public void setRacerText() {
        if (checkRacerPermissions()) {
            // Setup pilot information
            String username = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", null);
            race.calculatePoints();
            for (Racer racer : race.getRacers()) {
                if (racer.getUsername().equals(username)) {
                    Picasso.with(getContext())
                            .load(racer.getRacerPhoto())
                            .error(R.drawable.profile)
                            .noFade()
                            .into(racerImage);
                    racerName.setText(username);
                    racerFrequency.setText(racer.getFrequency());
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
            racerName.setText("Guest");
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
        int[] spotterIndex = race.getNext(currentIndex);
        String spotters = spotterIndex[0] == -1 || spotterIndex[1] == -1 ? "N/A" :  race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]).getAllRacers();
        int[] onDeckIndex = race.getNext(spotterIndex);
        String onDeck = onDeckIndex[0] == -1 || onDeckIndex[1] == -1 ? "N/A" :  race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]).getAllRacers();

        // Figure out the target time based on the text input
        // Add 15 seconds worth to value just for buffer's sake
        Long targetTime = System.currentTimeMillis() + TaskFragment.SntpOffset;
        mListener.onSendStatusUpdate(race, newState, racers, spotters, onDeck, targetTime);
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
            Long targetTime = System.currentTimeMillis() + TaskFragment.SntpOffset + Long.parseLong(editText.getText().toString()) * 1000 + 5000;
            if (state.charAt(0) == 'R') {
                stopTimer();
                startTimer(targetTime);
            }
            mListener.onSendStatusUpdate(race, state, racers, spotters, onDeck, targetTime);
        }
    }


    // For starting the timer with a synced target time
    public void startTimer(long targetTime) {
        if (getView() != null) {
            TextView adminTicker = (TextView) getView().findViewById(R.id.race_timer_ticker_admin);
            countdownRunnable = new CountdownRunnable(adminTicker, targetTime, -1);
            TextView smallTicker = (TextView) getView().findViewById(R.id.race_timer_ticker_pilot);
            countdownRunnable2 = new CountdownRunnable(smallTicker, targetTime, -1);
            handler.post(countdownRunnable);
            handler.post(countdownRunnable2);
        }
    }

    public void stopTimer() {
        handler.removeCallbacksAndMessages(null);
        Log.i("HANDLER CALLED", "STOP THAT SHIT");
        if (getView() != null) {
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

        private final long[] checkpoints = {1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 15000, 30000, 60000, 90000, 120000, 150000, 180000, 210000, 240000, 270000, 300000};

        public CountdownRunnable(TextView textView, long targetTime, long previousTime) {
            this.textView = textView;
            this.targetTime = targetTime;
            this.previousTime = previousTime;
        }

        public void run() {
            Log.i("SNTP TEST", Long.toString(TaskFragment.SntpOffset));
            currentTime = targetTime - System.currentTimeMillis() - TaskFragment.SntpOffset;
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
                        if (previousTime > checkpoint && currentTime <= checkpoint && ((MainActivity) getActivity()).getActiveFragment() instanceof RaceFragment) {
                            // Start vibrating until up to 5 seconds past the target time
                            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
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
        String status = race.getStatus();

        if (status.equals("NS") || status.equals("F")) {
            currentHeatView.setVisibility(View.GONE);
            spotterHeatView.setVisibility(View.GONE);
            ondeckHeatView.setVisibility(View.GONE);
        }
        else {
            int[] currentIndex = new int[] {Integer.parseInt(status.split(" ")[1]), Integer.parseInt(status.split(" ")[2])};
            currentHeatView.setVisibility(View.VISIBLE);
            currentHeatList.clear();
            currentHeatList.add(race.getRounds().get(currentIndex[0]).getHeat(currentIndex[1]));
            RoundAdapter3 currentHeatAdapter;
            if (currentHeatView.getAdapter() == null) {
                System.out.println("current adapter created");
                currentHeatAdapter = new RoundAdapter3(getContext(), (ArrayList<Heat>) currentHeatList, currentIndex[0], currentIndex[1], status, this);
                currentHeatView.setAdapter(currentHeatAdapter);
            }
            else {
                currentHeatAdapter = (RoundAdapter3) currentHeatView.getAdapter();
                currentHeatAdapter.setRoundIndex(currentIndex[0]);
                currentHeatAdapter.setHeatIndex(currentIndex[1]);
                currentHeatAdapter.setStatus(status);
                currentHeatAdapter.notifyDataSetChanged();
            }

            int[] spotterIndex = race.getNext(currentIndex);
            if (!(spotterIndex[0] == -1 || spotterIndex[1] == -1)) {
                spotterHeatView.setVisibility(View.VISIBLE);
                spotterHeatList.clear();
                spotterHeatList.add(race.getRounds().get(spotterIndex[0]).getHeat(spotterIndex[1]));
                RoundAdapter3 spotterHeatAdapter;
                if (spotterHeatView.getAdapter() == null) {
                    System.out.println("spotter adapter created");
                    spotterHeatAdapter = new RoundAdapter3(getContext(), (ArrayList<Heat>) spotterHeatList, spotterIndex[0], spotterIndex[1], String.format(Locale.US, "W %d %d", spotterIndex[0], spotterIndex[1]), this);
                    spotterHeatView.setAdapter(spotterHeatAdapter);
                } else {
                    spotterHeatAdapter = (RoundAdapter3) spotterHeatView.getAdapter();
                    spotterHeatAdapter.setRoundIndex(spotterIndex[0]);
                    spotterHeatAdapter.setHeatIndex(spotterIndex[1]);
                    spotterHeatAdapter.setStatus(status);
                    spotterHeatAdapter.notifyDataSetChanged();
                }
            }
            else {
                spotterHeatView.setVisibility(View.GONE);
            }

            int[] onDeckIndex = race.getNext(spotterIndex);
            if (!(onDeckIndex[0] == -1 || onDeckIndex[1] == -1)) {
                ondeckHeatView.setVisibility(View.VISIBLE);
                ondeckHeatList.clear();
                ondeckHeatList.add(race.getRounds().get(onDeckIndex[0]).getHeat(onDeckIndex[1]));
                RoundAdapter3 ondeckHeatAdapter;
                if (ondeckHeatView.getAdapter() == null) {
                    System.out.println("ondeckadapter created");
                    ondeckHeatAdapter = new RoundAdapter3(getContext(), (ArrayList<Heat>) ondeckHeatList, onDeckIndex[0], onDeckIndex[1], String.format(Locale.US, "W %d %d", onDeckIndex[0], onDeckIndex[1]), this);
                    ondeckHeatView.setAdapter(ondeckHeatAdapter);
                } else {
                    ondeckHeatAdapter = (RoundAdapter3) ondeckHeatView.getAdapter();
                    ondeckHeatAdapter.setRoundIndex(onDeckIndex[0]);
                    ondeckHeatAdapter.setHeatIndex(onDeckIndex[1]);
                    ondeckHeatAdapter.setStatus(status);
                    ondeckHeatAdapter.notifyDataSetChanged();
                }
            }
            else {
                ondeckHeatView.setVisibility(View.GONE);
            }
        }
    }

    // Adds margin between heat cards and basic navigation card if heat cards are present
    public void setBasicCardSpacing() {
        if (getView() != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getView().findViewById(R.id.race_card_basic).getLayoutParams();
            if (currentHeatView.getVisibility() == View.GONE && spotterHeatView.getVisibility() == View.GONE && ondeckHeatView.getVisibility() == View.GONE) {
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
        dialog.show(fm, "some_unknown_text");
    }

    // On dialog finish
    public void onFinishChangeSlotDialog(int points, boolean remove, Slot slot, String tag, String newUser) {
        if (remove) {
            if (newUser.equals("Empty slot")) {
                slot.setUsername("EMPTY SLOT");
            }
            else {
                slot.setUsername(newUser);
            }
        }
        else {
            slot.setPoints(points);
        }

        mListener.onUpdateSlotOnServer(race, slot, tag);
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
        String username = sharedPreferences.getString("username", null);
        return race.getAdmins().contains(username);
    }

    public Boolean checkRacerPermissions() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = sharedPreferences.getString("username", null);
        boolean racing = false;
        for (Racer racer : race.getRacers()) {
            if (username.equals(racer.getUsername())) {
                racing = true;
            }
        }
        return racing;
    }
}
