package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Round;
import com.example.peter.racemanager.models.Slot;

import java.util.ArrayList;
import java.util.Iterator;

/**
 */
public class RaceScheduleCardFragment extends Fragment implements ChangeSlotDialogFragment.ChangeSlotDialogListener, RoundAdapter3.onSlotSelectListener {
    private static final String ROUND_KEY = "ROUND_KEY";
    private static final String INDEX_KEY = "INDEX_KEY";
    private static final String STATUS_KEY = "STATUS_KEY";

    private Round round;
    private int index;
    private String status;
    private RoundAdapter3 roundAdapter3;

    private OnRaceScheduleCardFragmentListener mListener;

    public RaceScheduleCardFragment() {
        // Required empty public constructor
    }

    public static RaceScheduleCardFragment newInstance(Round round, int index, String status) {
        RaceScheduleCardFragment fragment = new RaceScheduleCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ROUND_KEY, round);
        args.putInt(INDEX_KEY, index);
        args.putString(STATUS_KEY, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            round = getArguments().getParcelable(ROUND_KEY);
            index = getArguments().getInt(INDEX_KEY);
            status = getArguments().getString(STATUS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_race_schedule_card, container, false);

        String[] current = status.split(" ");

        // For use with roundadapter3?
        // Figure out round title card color depending on state of race
        CardView cardView = (CardView) view.findViewById(R.id.race_schedule_round_card);
        if (current[0].equals("NS")) {
            // Race not started yet, all are "pending"
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue900));
        }
        else if (current[0].equals("F")) {
            // Race is over, all are "done"
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.bluegrey900));
        }
        else if (index < Integer.parseInt(current[1])) {
            // This is an old round
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.bluegrey900));
        }
        else if (index == Integer.parseInt(current[1])) {
            // This is the current round
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.greenA400));
        }
        else {
            // This is a future round
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue900));
        }

        TextView roundText = (TextView) view.findViewById(R.id.race_schedule_round_title);
        roundText.setText(String.format("ROUND %d", index + 1));

        ListView listView = (ListView) view.findViewById(R.id.race_schedule_heat_listview);
        roundAdapter3 = new RoundAdapter3(getActivity(), (ArrayList<Heat>) round.getHeats(), index, status, this);
        listView.setAdapter(roundAdapter3);
        listView.setPadding(0, 100, 0, 20);
        listView.setClipToPadding(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRaceScheduleCardFragmentListener) {
            mListener = (OnRaceScheduleCardFragmentListener) context;
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

    public interface OnRaceScheduleCardFragmentListener {
        void onUpdateSlotOnServer(Race race, Slot slot, String tag);
    }

    public Round getRound() {
        return round;
    }

    public int getIndex() {
        return index;
    }

    public String getStatus() {
        return status;
    }

    // From RoundAdapter3 interface
    @Override
    public void showChangeSlotDialog(View view) {
        Log.i("DIALOG STUFF", "fads");
        FragmentManager fm = getChildFragmentManager();
        String[] tag = view.getTag().toString().split(" ");
        ChangeSlotDialogFragment dialog = ChangeSlotDialogFragment.newInstance(round.getHeat(Integer.parseInt(tag[1])).getSlot(tag[2]), view.getTag().toString());
        dialog.setTargetFragment(RaceScheduleCardFragment.this, 300);
        dialog.show(fm, "some_unknown_text");
    }

    // From ChangeSlotDialogFragment interface
    @Override
    public void onFinishChangeSlotDialog(int points, boolean remove, Slot slot, String tag) {
        if (remove) {
            slot.setUsername("EMPTY SLOT");
        }
        else {
            slot.setPoints(points);
        }

        mListener.onUpdateSlotOnServer(((RaceScheduleFragment) getParentFragment()).getRace(), slot, tag);

        roundAdapter3.notifyDataSetChanged();
    }


}
