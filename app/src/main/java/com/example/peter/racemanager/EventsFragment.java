package com.example.peter.racemanager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsFragment extends Fragment {

    String jsonStr = "";

    public EventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            jsonStr = args.getString(MainActivity.EXTRA_MESSAGE);
        }

        JSONObject json = new JSONObject();
        String name = "";

        try {
            json = new JSONObject(jsonStr);
            name = json.getJSONArray("Employee").getJSONObject(1).getString("name");
        } catch (Exception x) {
            Log.d("HEYMAN ", x.toString());
        }


        TextView textView = new TextView(getActivity());
        textView.setText(name);

        TextView textView2 = new TextView(getActivity());
        textView2.setTextSize(40);
        textView2.setText("AT LEAST THIS IS HERE");

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.event_layout);
        layout.addView(textView);
        layout.addView(textView2);
    }
}
