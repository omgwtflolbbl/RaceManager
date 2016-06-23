package com.example.peter.racemanager.fragments;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Slot;

/**
 * A dialog box that will pop up whenever the user clicks on "add" from the events menu.
 */
public class AddRaceDialogFragment extends DialogFragment {

    private static final String SLOT_KEY = "SLOT_KEY";
    private static final String TAG_KEY = "TAG_KEY";

    private EditText urlField;
    private AddRaceDialogListener mListener;
    private TextView update;
    private TextView cancel;

    public AddRaceDialogFragment() {
        // Empty constructor
    }

    public static ChangeSlotDialogFragment newInstance(Slot slot, String tag) {
        ChangeSlotDialogFragment fragment = new ChangeSlotDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(SLOT_KEY, slot);
        args.putString(TAG_KEY, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_race_dialog, container);

        getDialog().setTitle("Add a race");

        try {
            mListener = (AddRaceDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }

        urlField = (EditText) view.findViewById(R.id.dialog_add_race_multigp_url);

        update = (TextView) view.findViewById(R.id.dialog_add_race_save);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO do something like mListener.onFinishAddRaceDialog()
                mListener.onFinishAddRaceDialog(urlField.getText().toString());
                dismiss();
            }
        });
        cancel = (TextView) view.findViewById(R.id.dialog_add_race_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public interface AddRaceDialogListener {
        void onFinishAddRaceDialog(String URL);
    }
}
