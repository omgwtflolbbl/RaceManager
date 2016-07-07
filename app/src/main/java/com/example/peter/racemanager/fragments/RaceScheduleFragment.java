package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.ScheduleViewPager;
import com.example.peter.racemanager.adapters.RoundAdapter;
import com.example.peter.racemanager.adapters.RoundAdapter2;
import com.example.peter.racemanager.adapters.RoundAdapter3;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Round;
import com.example.peter.racemanager.models.Slot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RaceScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RaceScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaceScheduleFragment extends Fragment implements ChangeSlotDialogFragment.ChangeSlotDialogListener, RoundAdapter3.onSlotSelectListener {
    private static final String RACE_KEY = "RACE_KEY";
    private static final String ROTATED_KEY = "ROTATED_KEY";

    private RoundAdapter2 roundAdapter2;
    private Race race;
    private Boolean rotated = false;

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
        if (savedInstanceState != null) {
            rotated = savedInstanceState.getBoolean(ROTATED_KEY);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_add_event).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.schedule_viewpager);
        roundAdapter2 = new RoundAdapter2(getChildFragmentManager(), race.getRounds(), race.getStatus());
        viewPager.setAdapter(roundAdapter2);
        viewPager.setPageMargin(12);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.schedule_viewpager);
        viewPager.getLayoutParams().height = ViewPager.LayoutParams.WRAP_CONTENT;

        if (!rotated) {
            mListener.refreshRaceScheduleFragment(race);
        }
        else {
            rotated = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getActivity().isChangingConfigurations()) {
            rotated = true;
        }

        outState.putBoolean(ROTATED_KEY, rotated);
    }

    public void onButtonPressed(Race race) {
        if (mListener != null) {
            mListener.refreshRaceScheduleFragment(race);
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

    public interface OnFragmentInteractionListener {
        void refreshRaceScheduleFragment(Race race);
        void onUpdateSlotOnServer(Race race, Slot slot, String tag);
    }

    public Race getRace() {
        return race;
    }

    public void updateRoundAdapter(final Race race) {
        this.race = race;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                roundAdapter2.update(race.getRounds(), race.getStatus());
            }
        });
    }

    public void showChangeSlotDialog(View view) {
        FragmentManager fm = getChildFragmentManager();
        String[] tag = view.getTag().toString().split(" ");
        ChangeSlotDialogFragment dialog = ChangeSlotDialogFragment.newInstance(race.getRounds().get(Integer.parseInt(tag[0])).getHeat(Integer.parseInt(tag[1])).getSlot(tag[2]), view.getTag().toString(), race);
        dialog.setTargetFragment(RaceScheduleFragment.this, 300);
        dialog.show(fm, "some_unknown_text");
    }

    public void onFinishChangeSlotDialog(int points, boolean remove, Slot slot, String tag, String newUser) {
        if (remove) {
            if (newUser.equals("Empty slot")) {
                slot.setUsername("EMPTY SLOT");
            }
            else {
                slot.setUsername(newUser);
            }
        }
        else {
            slot.setPoints(points);
        }

        mListener.onUpdateSlotOnServer(race, slot, tag);
    }

    // Check what this user can actually see. Probably need to break this up into two parts so that
    // one part checks it, and the other actually does something based on that (in case I need to
    // check permissions elsewhere like if a user can open a dialog or something).
    public Boolean checkPermissions() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = sharedPreferences.getString("username", null);
        if (race.getAdmins().contains(username)) {
            // YER A WIZARD HARRY
            return true;
        }
        else {
            // FILTHY COMMONER
            return false;
        }
    }
}
