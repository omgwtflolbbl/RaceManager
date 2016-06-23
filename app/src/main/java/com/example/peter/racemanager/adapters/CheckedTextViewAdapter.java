package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.example.peter.racemanager.R;

import java.util.ArrayList;

/**
 * Created by Peter on 6/22/2016.
 */
public class CheckedTextViewAdapter extends ArrayAdapter<String> {

    public CheckedTextViewAdapter (Context context, ArrayList<String> strings) {
        super(context, 0, strings);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        final String string = getItem(position);
        // Check if the existing view is being used, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.checked_textview_adapter_label, parent, false);
        }

        CheckedTextView textView = (CheckedTextView) view.findViewById(R.id.adapter_checked_textview);
        textView.setText(string);

        return view;
    }
}
