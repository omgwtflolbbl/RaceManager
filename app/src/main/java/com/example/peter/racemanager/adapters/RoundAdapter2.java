package com.example.peter.racemanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.fragments.RaceScheduleCardFragment;
import com.example.peter.racemanager.models.Heat;
import com.example.peter.racemanager.models.Round;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Peter on 6/3/2016.
 */
public class RoundAdapter2 extends FragmentStatePagerAdapter {
    private ArrayList<Round> rounds = new ArrayList<Round>();
    private FragmentManager fm;
    private String status;

    public RoundAdapter2(FragmentManager fm) {
        super(fm);
    }

    public RoundAdapter2(FragmentManager fm, ArrayList<Round> rounds) {
        super(fm);
        this.rounds = rounds;
        this.fm = fm;
    }

    public RoundAdapter2(FragmentManager fm, ArrayList<Round> rounds, String status) {
        super(fm);
        this.rounds = rounds;
        this.fm = fm;
        this.status = status;
    }

    @Override
    public Fragment getItem(int position) {
        RaceScheduleCardFragment raceScheduleCardFragment = RaceScheduleCardFragment.newInstance(rounds.get(position), position, status);
        return raceScheduleCardFragment;
    }

    @Override
    public int getCount() {
        return rounds.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void update(ArrayList<Round> rounds, String status) {
        this.rounds = rounds;
        this.status = status;
        notifyDataSetChanged();
    }
}
