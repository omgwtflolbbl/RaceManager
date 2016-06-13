package com.example.peter.racemanager.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Slot;

/**
 * This dialog is to appear whenever an admin presses on a slot in the raceschedulefragment or the
 * racefragment. Implementation into raceschedulefragment will come first.
 */
public class ChangeSlotDialogFragment extends DialogFragment {

    private static final String SLOT_KEY = "SLOT_KEY";
    private static final String TAG_KEY = "TAG_KEY";

    private Slot slot;
    private TextView minus;
    private TextView plus;
    private EditText count;
    private CheckBox remove;
    private TextView update;
    private TextView cancel;
    private ChangeSlotDialogListener mListener;
    private String tag;

    public ChangeSlotDialogFragment() {
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
        View view = inflater.inflate(R.layout.fragment_change_slot_dialog, container);

        Log.i("PFRAG", getParentFragment().toString());
        Log.i("CONTEXT", getContext().toString());

        try {
            mListener = (ChangeSlotDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }

        slot = getArguments().getParcelable(SLOT_KEY);
        tag = getArguments().getString(TAG_KEY);

        /*title = (TextView) view.findViewById(R.id.dialog_change_slot_title);
        title.setText(String.format("Update slot (%s)", slot.getUsername()));*/
        getDialog().setTitle(slot.getUsername());

        count = (EditText) view.findViewById(R.id.dialog_change_slot_points);
        count.setText(Integer.toString(slot.getPoints()));

        minus = (TextView) view.findViewById(R.id.dialog_change_slot_subtract);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lower the count by 1 if > 0, otherwise leave at 0
                int current = Integer.parseInt(count.getText().toString());
                count.setText(current > 0 ? Integer.toString(current - 1) : "0");
            }
        });

        plus = (TextView) view.findViewById(R.id.dialog_change_slot_add);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Increase the current count by 1
                count.setText(Integer.toString(Integer.parseInt(count.getText().toString()) + 1));
            }
        });

        remove = (CheckBox) view.findViewById(R.id.dialog_change_empty_slot_checkbox);

        update = (TextView) view.findViewById(R.id.dialog_change_slot_save);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFinishChangeSlotDialog(Integer.parseInt(count.getText().toString()), remove.isChecked(), slot, tag);
                dismiss();
            }
        });

        cancel = (TextView) view.findViewById(R.id.dialog_change_slot_cancel);
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

    // Send data back to parent fragment
    public void sendResult() {

    }

    public interface ChangeSlotDialogListener {
        void onFinishChangeSlotDialog(int points, boolean remove, Slot slot, String tag);
    }
}
