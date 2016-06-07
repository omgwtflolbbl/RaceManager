package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.RoundAdapter3;
import com.example.peter.racemanager.models.Heat;
import com.example.peter.racemanager.models.Round;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RaceScheduleCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RaceScheduleCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaceScheduleCardFragment extends Fragment {
    private static final String ROUND_KEY = "ROUND_KEY";
    private static final String INDEX_KEY = "INDEX_KEY";

    private Round round;
    private int index;
    private RoundAdapter3 roundAdapter3;

    private OnFragmentInteractionListener mListener;

    public RaceScheduleCardFragment() {
        // Required empty public constructor
    }

    public static RaceScheduleCardFragment newInstance(Round round, int index) {
        RaceScheduleCardFragment fragment = new RaceScheduleCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ROUND_KEY, round);
        args.putInt(INDEX_KEY, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            round = getArguments().getParcelable(ROUND_KEY);
            index = getArguments().getInt(INDEX_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_race_schedule_card, container, false);

        // For use with roundadapter3?
        TextView roundText = (TextView) view.findViewById(R.id.race_schedule_round_title);
        roundText.setText(String.format("ROUND %d", index + 1));



        ListView listView = (ListView) view.findViewById(R.id.race_schedule_heat_listview);
        roundAdapter3 = new RoundAdapter3(getActivity(), (ArrayList<Heat>) round.getHeats(), index);
        listView.setAdapter(roundAdapter3);
        listView.setPadding(0, 100, 0, 20);
        listView.setClipToPadding(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();




        /* For use with roundadapter2

        // Get GridLayout in which everything on the card will be laid out
        final GridLayout gridLayout = (GridLayout) getView().findViewById(R.id.race_schedule_round_grid);
        gridLayout.setColumnCount(3);

        // Create TextView for the header (which is just the round number)
        TextView roundNumber = new TextView(getContext());
        roundNumber.setTag(Integer.toString(index + 1));
        roundNumber.setText(String.format("ROUND %d", index + 1));
        roundNumber.setTextSize(26);
        roundNumber.setGravity(Gravity.CENTER);
        roundNumber.setBackgroundColor(Color.RED);
        GridLayout.LayoutParams roundNumberParams = new GridLayout.LayoutParams();
        roundNumberParams.rowSpec = GridLayout.spec(0);
        roundNumberParams.columnSpec = GridLayout.spec(0, 3);
        roundNumberParams.setGravity(Gravity.FILL);
        roundNumber.setLayoutParams(roundNumberParams);
        gridLayout.addView(roundNumber);

        // Counter to keep track of current row
        int currentRow = 1;

        // Go through round and get every heat
        for (int i = 0; i < round.length(); i++) {
            Heat heat = round.getHeat(i);

            int rowSpan = ((heat.numSlots() - 1) / 2) * 2 + 2;

            // Create TextView for the heat number
            TextView heatNumber = new TextView(getContext());
            heatNumber.setTag(String.format("%d %d", index + 1, i + 1));
            heatNumber.setText(String.format("HEAT %d", i + 1));
            heatNumber.setTextSize(22);
            heatNumber.setGravity(Gravity.CENTER);
            heatNumber.setBackgroundColor(i % 2 == 0 ? Color.BLUE : Color.CYAN);
            heatNumber.setRotation(270);
            GridLayout.LayoutParams heatNumberParams = new GridLayout.LayoutParams();
            heatNumberParams.rowSpec = GridLayout.spec(currentRow, rowSpan);
            heatNumberParams.columnSpec = GridLayout.spec(0);
            heatNumberParams.setGravity(Gravity.FILL_VERTICAL);
            heatNumber.setLayoutParams(heatNumberParams);
            gridLayout.addView(heatNumber);

            Iterator<String> slots = heat.getKeys().iterator();

            // Used to keep track of slot for layout purposes
            int j = 0;

            // Create TextView to contain each slot
            while (slots.hasNext()) {
                String slot = slots.next();
                TextView slotText = new TextView(getContext());
                slotText.setTag(String.format("%d %d %s", index + 1, i + 1, slot));
                Log.i("HEY", String.format("%d %d %s", index + 1, i + 1, slot));
                slotText.setText(String.format("%s\n%s", heat.getSlot(slot).getUsername(), heat.getSlot(slot).getFrequency()));
                slotText.setGravity(Gravity.CENTER_VERTICAL);
                slotText.setBackgroundResource(i % 2 == 0 ? R.drawable.border_blue : R.drawable.border_cyan);
                slotText.setClickable(true);
                slotText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("TEXTVIEW PRESSED", view.getTag().toString());
                    }
                });
                GridLayout.LayoutParams slotTextParams = new GridLayout.LayoutParams();
                slotTextParams.rowSpec = GridLayout.spec((j / 2) * 2 + currentRow, 2);
                slotTextParams.columnSpec = GridLayout.spec(j % 2 + 1, (float) 1);
                slotTextParams.setGravity(Gravity.FILL);
                slotText.setLayoutParams(slotTextParams);
                gridLayout.addView(slotText);

                j++;
            }

            // Update current row
            currentRow = currentRow + rowSpan;
        }

        gridLayout.post(new Runnable() {
            @Override
            public void run() {
                Log.i("GRID LAYOUT", Integer.toString(gridLayout.getHeight()));
                CardView cardView = (CardView) getView().findViewById(R.id.race_schedule_round_card);
                cardView.getLayoutParams().height = gridLayout.getHeight();
                cardView.setLayoutParams(cardView.getLayoutParams());
                Log.i("CARDVIEW", Integer.toString(cardView.getHeight()));
            }
        });

        // For highlighting user in cards
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","AnonymousSpectator");
        if (!username.equals("AnonymousSpectator") && round.findRacerInRound(username) != null) {
            String racerLoc = Integer.toString(index + 1) + " " + round.findRacerInRound(username);
            if (racerLoc != null) {
                Log.i("RACER IS AT", racerLoc);
                getView().findViewWithTag(racerLoc).setBackgroundColor(Color.parseColor("#FF69B4"));
            }
        }*/
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
