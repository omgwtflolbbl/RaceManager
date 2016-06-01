package com.example.peter.racemanager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RaceInfoFragment.OnRaceInfoListener} interface
 * to handle interaction events.
 * Use the {@link RaceInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaceInfoFragment extends Fragment {
    private static final String RACE_KEY = "race_key";

    private Race race;

    private OnRaceInfoListener mListener;

    public RaceInfoFragment() {
        // Required empty public constructor
    }

    public static RaceInfoFragment newInstance(Race race) {
        RaceInfoFragment fragment = new RaceInfoFragment();
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
        return inflater.inflate(R.layout.fragment_race_info, container, false);
    }

    public void onButtonPressed(Race race) {
        if (mListener != null) {
            mListener.onRaceInfo(race);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRaceInfoListener) {
            mListener = (OnRaceInfoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRaceInfoListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        this.updateTitle(race.getTitle());
        this.updateDate(race.getDate());
        this.updateTime(race.getTime());
        this.updateBlockquote(race.getBlockquote());
        this.updateDescription(race.getDescription());
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRaceInfoListener {
        void onRaceInfo(Race race);
    }

    public void updateTitle(String title) {
        TextView titleText = (TextView) getView().findViewById(R.id.race_info_title);
        titleText.setText(title);
    }

    public void updateDate(String date) {
        TextView dateText = (TextView) getView().findViewById(R.id.race_info_date);
        dateText.setText(date);
    }

    public void updateTime(String time) {
        TextView timeText = (TextView) getView().findViewById(R.id.race_info_time);
        timeText.setText(time);
    }

    public void updateBlockquote(String blockquote) {
        TextView blockquoteText = (TextView) getView().findViewById(R.id.race_info_blockquote);
        blockquoteText.setText(blockquote);
    }

    public void updateDescription(String description) {
        TextView descriptionText = (TextView) getView().findViewById(R.id.race_info_description);
        descriptionText.setText(description);
    }
}
