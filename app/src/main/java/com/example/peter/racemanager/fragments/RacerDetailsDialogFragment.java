package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Racer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RacerDetailsDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RacerDetailsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RacerDetailsDialogFragment extends DialogFragment {
    private static final String RACER_KEY = "RACER_KEY";

    private Racer racer;

    private OnFragmentInteractionListener mListener;

    public RacerDetailsDialogFragment() {
        // Required empty public constructor
    }

    public static RacerDetailsDialogFragment newInstance(Racer racer) {
        RacerDetailsDialogFragment fragment = new RacerDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(RACER_KEY, racer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            racer = getArguments().getParcelable(RACER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_racer_details_dialog, container, false);



        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

    }
}
