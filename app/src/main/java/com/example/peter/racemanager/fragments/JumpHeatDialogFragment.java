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
import android.widget.EditText;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Race;

import java.util.Locale;

/**
 * This dialog will pop up whenever an admin presses the button to jump to a target point in the race.
 */
public class JumpHeatDialogFragment extends DialogFragment {

    private final static String RACE_KEY = "RACE_KEY";

    private Race race;

    private JumpHeatDialogListener mListener;

    public JumpHeatDialogFragment() {
        // Required empty public constructor
    }

    public static JumpHeatDialogFragment newInstance(Race race) {
        JumpHeatDialogFragment fragment = new JumpHeatDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(RACE_KEY, race);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jump_heat_dialog, container, false);

        try {
            mListener = (JumpHeatDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }

        race = getArguments().getParcelable(RACE_KEY);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        final EditText roundText = (EditText) view.findViewById(R.id.dialog_jump_heat_round_text);
        final EditText heatText = (EditText) view.findViewById(R.id.dialog_jump_heat_heat_text);

        TextView roundSubtract = (TextView) view.findViewById(R.id.dialog_jump_slot_round_subtract);
        roundSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lower the count by 1 if > 1, otherwise leave at 1
                int current = Integer.parseInt(roundText.getText().toString());
                roundText.setText(current > 1 ? Integer.toString(current - 1) : "1");
            }
        });

        TextView roundAdd = (TextView) view.findViewById(R.id.dialog_jump_slot_round_add);
        roundAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increase count by 1 if there are more rounds than the current number indicates
                int current = Integer.parseInt(roundText.getText().toString());
                roundText.setText(current < race.getRounds().size() ? Integer.toString(current + 1) : Integer.toString(current));

                // Check to make sure that there are enough heats in the current round. If not, lower it to the new max.
                int currentHeat = Integer.parseInt(heatText.getText().toString());
                heatText.setText(currentHeat <= race.getRounds().get(current - 1).length() ? Integer.toString(currentHeat) : Integer.toString(race.getRounds().get(current - 1).length()));
            }
        });


        TextView heatSubtract = (TextView) view.findViewById(R.id.dialog_jump_slot_heat_subtract);
        heatSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lower the count by 1 if > 1, otherwise leave at 1
                int current = Integer.parseInt(heatText.getText().toString());
                heatText.setText(current > 1 ? Integer.toString(current - 1) : "1");
            }
        });

        TextView heatAdd = (TextView) view.findViewById(R.id.dialog_jump_slot_heat_add);
        heatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increase count by 1 if there are more heats in this round than the current number indicates
                int current = Integer.parseInt(roundText.getText().toString());
                int currentHeat = Integer.parseInt(heatText.getText().toString());
                heatText.setText(currentHeat < race.getRounds().get(current-1).length() ? Integer.toString(currentHeat + 1) : Integer.toString(currentHeat));
            }
        });

        TextView jump = (TextView) view.findViewById(R.id.dialog_jump_heat_jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newState = String.format(Locale.US, "W %d %d", Integer.parseInt(roundText.getText().toString()) - 1, Integer.parseInt(heatText.getText().toString()) - 1);
                mListener.onFinishJumpHeatDialog(newState);
                dismiss();
            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.dialog_jump_heat_cancel);
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

    public interface JumpHeatDialogListener {
        void onFinishJumpHeatDialog(String newState);
    }
}
