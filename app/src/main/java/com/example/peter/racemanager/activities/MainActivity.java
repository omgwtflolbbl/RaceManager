package com.example.peter.racemanager.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.peter.racemanager.fragments.EventsFragment;
import com.example.peter.racemanager.R;
import com.example.peter.racemanager.fragments.RaceFragment;
import com.example.peter.racemanager.fragments.RaceInfoFragment;
import com.example.peter.racemanager.fragments.RaceScheduleCardFragment;
import com.example.peter.racemanager.fragments.RaceScheduleFragment;
import com.example.peter.racemanager.fragments.SettingsFragment;
import com.example.peter.racemanager.fragments.TaskFragment;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.services.StatusService;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements EventsFragment.OnEventSelectedListener, RaceFragment.OnRaceListener, RaceInfoFragment.OnRaceInfoListener, RaceScheduleFragment.OnFragmentInteractionListener, TaskFragment.TaskCallbacks, RaceScheduleCardFragment.OnFragmentInteractionListener {

    public final static String EXTRA_MESSAGE = "com.example.peter.racemanager.MESSAGE";

    private TaskFragment taskFragment;
    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("STATUS RECEIVER", "RECEIVED MESSAGE");
            startUpdating();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: FIGURE OUT WHAT THIS ACTUALLY DOES?!?
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                // fix our issues for static variables
                //isActive = false;

                // fix our issues for sharedpreferences
                SharedPreferences sp = getSharedPreferences("OURINFO", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("active", false);
                ed.commit();

                // Handle everthing else
                defaultHandler.uncaughtException(thread, throwable);
            }
        });

        // Set up broadcaster receiver so that we know when to we are getting important status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver, new IntentFilter("RaceManager-Update-Info"));

        setContentView(R.layout.activity_main);

        EventsFragment eventsFragment = null;

        if (savedInstanceState != null) {
            eventsFragment = (EventsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "EVENTS_FRAGMENT");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (taskFragment == null) {
            taskFragment = new TaskFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(taskFragment, "TASK_FRAGMENT")
                    .commit();
        }

        if (eventsFragment == null) {
            eventsFragment = new EventsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, eventsFragment, "EVENTS_FRAGMENT")
                    .addToBackStack("EVENTS_FRAGMENT")
                    .commit();
        }

        // Add the event fragment to the "fragment_container" LinearLayout

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Fragment fragment = getActiveFragment();

                if (fragment instanceof EventsFragment) {
                    /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    String username = sharedPreferences.getString("username","PKLee");
                    Log.i("USERNAME", username);
                    String URL = String.format("http://effbb36d.ngrok.io/users/%s/events", username);
                    taskFragment.getEvents(URL);
                    EventsFragment eventsFragment = (EventsFragment) fragment;
                    eventsFragment.startRefreshing();*/
                    refreshEventsFragment((EventsFragment) fragment);
                }
                else if (fragment instanceof RaceScheduleFragment) {
                    /*Log.i("FRAGMENTNAME", fragment.getClass().toString());
                    RaceScheduleFragment raceScheduleFragment = (RaceScheduleFragment) fragment;
                    Race race = raceScheduleFragment.getRace();
                    String URL = String.format("http://effbb36d.ngrok.io/getEventIdFromURL/%s", race.getSiteURL().split(".com/")[1]);
                    taskFragment.getUpdatedRaceSchedule(URL);*/
                    refreshRaceScheduleFragment(((RaceScheduleFragment) fragment).getRace());
                }
                else if (fragment instanceof RaceFragment) {
                    /*Log.i("FRAGMENTNAME", fragment.getClass().toString());
                    RaceFragment raceFragment = (RaceFragment) fragment;
                    Race race = raceFragment.getRace();
                    String URL = String.format("http://effbb36d.ngrok.io/getEventIdFromURL/%s", race.getSiteURL().split(".com/")[1]);
                    taskFragment.getUpdatedRace(URL);*/
                    refreshRaceFragment(((RaceFragment) fragment).getRace());
                }
                else {
                    Log.i("FRAGMENTNAME", fragment.getClass().toString());
                }


                return true;
            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("OURINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("OURINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.commit();

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(statusReceiver);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "EVENTS_FRAGMENT", getSupportFragmentManager().findFragmentByTag("EVENTS_FRAGMENT"));
    }

    public Fragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    // ALL REFRESH STUFF
    public void startUpdating() {
        Fragment fragment = getActiveFragment();

        if (fragment instanceof EventsFragment) {
            refreshEventsFragment((EventsFragment) fragment);
        }
        else if (fragment instanceof RaceScheduleFragment) {
            refreshRaceScheduleFragment(((RaceScheduleFragment) fragment).getRace());
        }
        else if (fragment instanceof RaceFragment) {
            refreshRaceFragment(((RaceFragment) fragment).getRace());
        }
        else {
            Log.i("FRAGMENTNAME", fragment.getClass().toString());
        }
    }

    public void refreshEventsFragment(EventsFragment fragment) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString("username","PKLee");
        Log.i("USERNAME", username);
        String URL = String.format("http://effbb36d.ngrok.io/users/%s/events", username);
        taskFragment.getEvents(URL);
        fragment.startRefreshing();
    }

    public void refreshRaceFragment(Race race) {
        String URL = String.format("http://effbb36d.ngrok.io/getEventIdFromURL/%s", race.getSiteURL().split(".com/")[1]);
        taskFragment.getUpdatedRace(URL);
    }

    public void refreshRaceScheduleFragment(Race race) {
        String URL = String.format("http://effbb36d.ngrok.io/getEventIdFromURL/%s", race.getSiteURL().split(".com/")[1]);
        taskFragment.getUpdatedRaceSchedule(URL);
    }

    // EventsFragment callbacks
    public void onEventSelected(Race race) {
        RaceFragment raceFragment = RaceFragment.newInstance(race);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.fragment_container, raceFragment, "RACE_FRAGMENT")
                .addToBackStack("RACE_FRAGMENT")
                .commit();


        String URL = String.format("http://effbb36d.ngrok.io/getEventIdFromURL/%s", race.getSiteURL().split(".com/")[1]);
        taskFragment.startServiceProcess(URL);

        Log.i("TARGETTIMER", Long.toString(System.currentTimeMillis() + 180*1000));
    }

    // RaceFragment callbacks
    public void onRaceButton(View view, Race race) {
        switch (view.getId()) {
            case R.id.race_info_button:
                RaceInfoFragment raceInfoFragment = RaceInfoFragment.newInstance(race);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.fragment_container, raceInfoFragment, "RACE_INFO_FRAGMENT")
                        .addToBackStack("RACE_INFO_FRAGMENT")
                        .commit();
                break;
            case R.id.race_schedule_button:
                RaceScheduleFragment raceScheduleFragment = RaceScheduleFragment.newInstance(race);
                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.fragment_container, raceScheduleFragment, "RACE_SCHEDULE_FRAGMENT")
                        .addToBackStack("RACE_SCHEDULE_FRAGMENT")
                        .commit();
                break;/*
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

    public void onSendStatusUpdate(Race race, String status, String racers, String spotters, String onDeck, Long targetTimer) {
        String URL = String.format("http://effbb36d.ngrok.io/update/race/status/%s", race.getSiteURL().split(".com/")[1]);
        try {
            Log.i("TRY TO START", "PRCOESS WITH TASKFRAGMENT");
            taskFragment.updateDatabaseRaceStatus(URL, status, racers, spotters, onDeck, targetTimer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // RaceInfoFragment callbacks
    public void onRaceInfo(Race race) {

    }

    public void onFragmentInteraction(Uri uri) {

    }

    // TaskFragment callbacks
    public void OnRacesLoaded(final ArrayList<Race> races) {
        runOnUiThread(new Runnable() {
            public void run() {
                EventsFragment eventsFragment = (EventsFragment) getSupportFragmentManager().findFragmentByTag("EVENTS_FRAGMENT");
                eventsFragment.clearEventAdapter();
                eventsFragment.repopulateEventAdapter(races);
                eventsFragment.finishRefreshing();
            }
        });
    }

    public void StartStatusService(String eventId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString("username","PKLee");
        Intent intent = new Intent(this, StatusService.class);
        intent.putExtra("EVENT_ID", eventId);
        intent.putExtra("USERNAME", username);
        this.startService(intent);
    }

    public void UpdateRaceSchedule(Race race) {
        if (getActiveFragment() instanceof RaceScheduleFragment) {
            RaceScheduleFragment raceScheduleFragment = (RaceScheduleFragment) getActiveFragment();
            raceScheduleFragment.updateRoundAdapter(race);
        }
    }

    public void UpdateRace(Race race) {
        if (getActiveFragment() instanceof  RaceFragment) {
            RaceFragment raceFragment = (RaceFragment) getActiveFragment();
            raceFragment.setRace(race);
            raceFragment.checkRaceStatus();
        }

    }


}
