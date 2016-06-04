package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.RoundAdapter;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Round;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RaceScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RaceScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaceScheduleFragment extends Fragment {
    private static final String RACE_KEY = "RACE_KEY";

    private RoundAdapter roundAdapter;
    private Race race;

    private OnFragmentInteractionListener mListener;

    public RaceScheduleFragment() {
        // Required empty public constructor
    }

    public static RaceScheduleFragment newInstance(Race race) {
        RaceScheduleFragment fragment = new RaceScheduleFragment();
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
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        ListView listView = (ListView) view.findViewById(R.id.schedule_listview);
        roundAdapter = new RoundAdapter(getActivity(), new ArrayList<Round>());
        listView.setAdapter(roundAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        roundAdapter.addAll(race.getRounds());
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
