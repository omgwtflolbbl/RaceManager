package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.OverviewViewpagerAdapter;
import com.example.peter.racemanager.models.Race;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {
    private static final String RACE_KEY = "RACE_KEY";

    private Race race;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private OverviewViewpagerAdapter overviewAdapter;

    private OnFragmentInteractionListener mListener;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(Race race) {
        OverviewFragment fragment = new OverviewFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.overview_viewpager);
        overviewAdapter = new OverviewViewpagerAdapter(getChildFragmentManager(), race, getActivity());
        viewPager.setAdapter(overviewAdapter);

        // Attach TabLayout
        tabLayout = (TabLayout) view.findViewById(R.id.overview_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(overviewAdapter.getTabView(i));
            }
        }

        // Set up color changes for (un)selected tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tab_text)).setTextColor(ContextCompat.getColor(getContext(), R.color.MultiGPRed));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tab_text)).setTextColor(ContextCompat.getColor(getContext(), R.color.MultiGPGray));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Set up default view to be race view
        viewPager.setCurrentItem(2);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("IN OVERVIEW", getClass().toString());
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

    public ViewPager getViewPager() {
        return viewPager;
    }

    public Race getRace() {
        return race;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
