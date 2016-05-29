package com.example.peter.racemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements EventsFragment.OnEventSelectedListener, RaceFragment.OnRaceListener, RaceInfoFragment.OnRaceInfoListener, TaskFragment.TaskCallbacks {

    public final static String EXTRA_MESSAGE = "com.example.peter.myfirstapp.MESSAGE";

    String strJson=""+
    "{"+
        "\"Employee\" :["+
            "{"+
            "\"id\":\"01\","+
            "\"name\":\"Gopal Varma\","+
            "\"salary\":\"500000\""+
        "},"+
        "{"+
            "\"id\":\"02\","+
            "\"name\":\"Sairamkrishna\","+
            "\"salary\":\"500000\""+
        "},"+
        "{"+
            "\"id\":\"03\","+
            "\"name\":\"Sathish kallakuri\","+
            "\"salary\":\"600000\""+
        "}"+
        "]"+
    "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create taskfragment for handling all the things
        TaskFragment taskFragment = new TaskFragment();

        // Create instance of eventsfragment
        EventsFragment eventsFragment = new EventsFragment();
        Intent intent = getIntent();
        intent.putExtra(EXTRA_MESSAGE, strJson);
        eventsFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the "fragment_container" LinearLayout
        getSupportFragmentManager().beginTransaction()
                .add(taskFragment, "TASK_FRAGMENT")
                .add(R.id.fragment_container, eventsFragment)
                .commit();
    }

    public void onEventSelected(Race race) {
        RaceFragment raceFragment = RaceFragment.newInstance(race);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.fragment_container, raceFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onRaceButton(View view, Race race) {
        switch (view.getId()) {
            case R.id.race_info_button:
                RaceInfoFragment raceInfoFragment = RaceInfoFragment.newInstance(race);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.fragment_container, raceInfoFragment)
                        .addToBackStack(null)
                        .commit();
                break;/*
            case R.id.race_schedule_button:
                RaceScheduleFragment raceScheduleFragment = RaceScheduleFragment.newInstance(race);
                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.fragment_container, raceScheduleFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.race_racers_button:
                RaceInfoFragment raceInfoFragment = RaceInfoFragment.newInstance(race);
                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.fragment_container, raceInfoFragment)
                        .addToBackStack(null)
                        .commit();
                break;*/
        }
    }

    public void onRaceInfo(Race race) {

    }

    // TaskFragment callbacks
    public void onPreExecute() {

    }

    public void onProgressUpdate(int percent) {

    }

    public void onCancelled() {

    }

    public void onPostExecute() {

    }

    public void onFragmentInteraction(Uri uri) {

    }


}
