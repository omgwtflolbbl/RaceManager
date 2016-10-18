package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.fragments.HangarFragment;
import com.example.peter.racemanager.fragments.RaceFragment;
import com.example.peter.racemanager.fragments.RaceInfoFragment;
import com.example.peter.racemanager.fragments.RaceRacersFragment;
import com.example.peter.racemanager.models.Race;
import com.joanzapata.iconify.Iconify;

/**
 * Created by peterlee on 8/12/16.
 */
public class OverviewViewpagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;
    private Race race;
    private Context context;

    public OverviewViewpagerAdapter(FragmentManager fragmentManager, Race race, Context context) {
        super(fragmentManager);
        this.race = race;
        this.context = context;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RaceInfoFragment.newInstance(race);
            case 1: // Fragment # 0 - This will show FirstFragment
                return HangarFragment.newInstance("hello", "Page # 1");
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return RaceFragment.newInstance(race);
            case 3: // Fragment # 1 - This will show SecondFragment
                return RaceRacersFragment.newInstance(race);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return Iconify.compute(context, context.getResources().getString(R.string.tab_info));
            case 1:
                return Iconify.compute(context, context.getResources().getString(R.string.tab_hangar));
            case 2:
                return Iconify.compute(context, context.getResources().getString(R.string.tab_race));
            case 3:
                return Iconify.compute(context, context.getResources().getString(R.string.tab_results));
            default:
                return "???";
        }
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView textView = (TextView) v.findViewById(R.id.tab_text);
        textView.setTransformationMethod(null);
        switch (position) {
            case 0:
                textView.setText(Iconify.compute(context, context.getResources().getString(R.string.tab_info)));
                break;
            case 1:
                textView.setText(Iconify.compute(context, context.getResources().getString(R.string.tab_hangar)));
                break;
            case 2:
                textView.setText(Iconify.compute(context, context.getResources().getString(R.string.tab_race)));
                break;
            case 3:
                textView.setText(Iconify.compute(context, context.getResources().getString(R.string.tab_results)));
                break;
            default:
                break;
        }

        return v;
    }

}

