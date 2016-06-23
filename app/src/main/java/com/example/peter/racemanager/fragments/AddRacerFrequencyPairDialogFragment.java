package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.CheckedTextViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This dialog is used to select a racer's username and a desired frequency for a given slot when
 * rebuilding a race in part 3 of the wizard.
 */
public class AddRacerFrequencyPairDialogFragment extends DialogFragment {

    private final static String FREQ_KEY = "FREQ_KEY";
    private final static String RACERS_KEY = "RACERS_KEY";
    private final static String I_KEY = "I_KEY";
    private final static String TOTAL_KEY = "TOTAL_KEY";

    private List<String> frequencies;
    private List<String> usernames;
    private int i;
    private int total;

    private AddRacerFrequencyPairDialogListener mListener;

    public AddRacerFrequencyPairDialogFragment() {
        // Required empty public constructor
    }

    public static AddRacerFrequencyPairDialogFragment newInstance(ArrayList<String> frequencies, ArrayList<String> usernames, int i, int total) {
        AddRacerFrequencyPairDialogFragment fragment = new AddRacerFrequencyPairDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(FREQ_KEY, frequencies);
        args.putStringArrayList(RACERS_KEY, usernames);
        args.putInt(I_KEY, i);
        args.putInt(TOTAL_KEY, total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_racer_frequency_pair_dialog, container);

        try {
            mListener = (AddRacerFrequencyPairDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        frequencies = getArguments().getStringArrayList(FREQ_KEY);
        usernames = getArguments().getStringArrayList(RACERS_KEY);
        i = getArguments().getInt(I_KEY);
        total = getArguments().getInt(TOTAL_KEY);

        final ListView freqListView = (ListView) view.findViewById(R.id.dialog_add_racer_frequency_pair_frequency_listview);
        final CheckedTextViewAdapter freqAdapter = new CheckedTextViewAdapter(getContext(), (ArrayList<String>) frequencies);
        freqListView.setAdapter(freqAdapter);
        freqListView.setItemChecked(0, true);

        final ListView usersListView = (ListView) view.findViewById(R.id.dialog_add_racer_frequency_pair_username_listview);
        final CheckedTextViewAdapter usersAdapter = new CheckedTextViewAdapter(getContext(), (ArrayList<String>) usernames);
        usersListView.setAdapter(usersAdapter);
        usersListView.setItemChecked(0, true);

        TextView userHeader = (TextView) view.findViewById(R.id.dialog_add_racer_frequency_pair_racer_header);
        userHeader.setText(String.format("Select racer (%d/%d left)", usernames.size(), total));

        TextView add = (TextView) view.findViewById(R.id.dialog_add_racer_frequency_pair_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnFinishAddRacerFrequencyPairDialog(freqAdapter.getItem(freqListView.getCheckedItemPosition()), usersAdapter.getItem(usersListView.getCheckedItemPosition()), i);
                dismiss();
            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.dialog_add_racer_frequency_pair_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AddRacerFrequencyPairDialogListener {
        // TODO: Update argument type and name
        //void onFragmentInteraction(Uri uri);
        void OnFinishAddRacerFrequencyPairDialog(String frequency, String username, int i);
    }
}
