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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.peter.racemanager.fragments.BuildRaceStructureAFragment;
import com.example.peter.racemanager.fragments.BuildRaceStructureBFragment;
import com.example.peter.racemanager.fragments.BuildRaceStructureCFragment;
import com.example.peter.racemanager.fragments.BuildRaceStructureDFragment;
import com.example.peter.racemanager.fragments.EventsFragment;
import com.example.peter.racemanager.R;
import com.example.peter.racemanager.fragments.HangarFragment;
import com.example.peter.racemanager.fragments.OverviewFragment;
import com.example.peter.racemanager.fragments.RaceFragment;
import com.example.peter.racemanager.fragments.RaceInfoFragment;
import com.example.peter.racemanager.fragments.RaceRacersFragment;
import com.example.peter.racemanager.fragments.RaceScheduleCardFragment;
import com.example.peter.racemanager.fragments.RaceScheduleFragment;
import com.example.peter.racemanager.fragments.TaskFragment;
import com.example.peter.racemanager.models.AddFrequencySlot;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;
import com.example.peter.racemanager.models.Slot;
import com.example.peter.racemanager.services.StatusService;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements EventsFragment.OnEventSelectedListener, RaceFragment.OnRaceListener, RaceInfoFragment.OnRaceInfoListener, RaceScheduleFragment.OnFragmentInteractionListener, TaskFragment.TaskCallbacks, RaceScheduleCardFragment.OnRaceScheduleCardFragmentListener, RaceRacersFragment.OnFragmentInteractionListener, BuildRaceStructureAFragment.OnFragmentInteractionListener, BuildRaceStructureBFragment.OnFragmentInteractionListener, BuildRaceStructureCFragment.OnFragmentInteractionListener, BuildRaceStructureDFragment.OnFragmentInteractionListener, OverviewFragment.OnFragmentInteractionListener, HangarFragment.OnFragmentInteractionListener {

    public final static String FLASK = "https://racemanagerflask.herokuapp.com";
    public final static String ROOT = "http://www.multigp.com/multigpwebservice";
    public final static String API = "dSSU56VK4cfUaYwwFCCb6680LCRMFpwCl4DZ8wl75jvyWPpVyaEUjh52q1rtavdB";

    private TaskFragment taskFragment;
    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startUpdating();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Check if user is logged in. Otherwise, go to login screen.
        validateUser();

        // Set up no sleep mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set up broadcaster receiver so that we know when to we are getting important status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver, new IntentFilter("RaceManager-Update-Info"));

        // Check if we have a retained TaskFragment already.
        if (taskFragment == null) {
            taskFragment = new TaskFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(taskFragment, "TASK_FRAGMENT")
                    .commit();
        }

        // Check if we have an EventsFragment already. If so, get old state. Else, create a new one.
        EventsFragment eventsFragment = null;
        if (savedInstanceState != null) {
            eventsFragment = (EventsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "EVENTS_FRAGMENT");
        }
        if (eventsFragment == null) {
            eventsFragment = new EventsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, eventsFragment, "EVENTS_FRAGMENT")
                    .addToBackStack("EVENTS_FRAGMENT")
                    .commit();
        }
    }

    // Check if this is a valid/guest user or invalid by seeing if they have a username
    protected void validateUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString("username", null) == null) {
            // Force to LoginActivity
            sendToLogin();
        }
    }

    // Change to LoginActivity
    protected void sendToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Logout the user
    protected void logout() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove("sessionId");
        editor.remove("username");
        editor.remove("pilotId");
        editor.remove("authorization");
        editor.apply();
        sendToLogin();
    }

    // Full logout including removing guest token - this can't be retrieved!
    protected void fullLogout() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.remove("guestToken");
        editor.apply();
        logout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_refresh).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_refresh).colorRes(R.color.white).actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        switch (item.getItemId()) {
            case R.id.action_refresh:
                startUpdating();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_logout_complete:
                fullLogout();
                return true;
            case R.id.action_stop_service:
                Intent intent = new Intent(this, StatusService.class);
                this.stopService(intent);
                return true;
            /*case R.id.action_settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();*/
            default:
                return super.onOptionsItemSelected(item);
        }
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
            refreshRaceFragment(((RaceScheduleFragment) fragment).getRace());
        }
        else if (fragment instanceof RaceFragment) {
            refreshRaceFragment(((RaceFragment) fragment).getRace());
        }
        else if (fragment instanceof RaceRacersFragment) {
            Log.i("FRAGMENTNAME", fragment.getClass().toString());
        }
        else if (fragment instanceof OverviewFragment) {
            refreshRaceFragment(((OverviewFragment) fragment).getRace());
        }
        else {
            Log.i("FRAGMENTNAME", fragment.getClass().toString());
        }
    }

    public void refreshEventsFragment(EventsFragment fragment) {
        String URL = String.format("%s/race/list", ROOT);
        taskFragment.getEvents(URL);
        fragment.startRefreshing();
    }

    public void refreshRaceFragment(Race race) {
        taskFragment.requestFirebaseData(race);
        taskFragment.requestRaceData(race);
    }

    public void refreshRaceScheduleFragment(Race race) {
        taskFragment.requestRaceData(race);
    }

    // EventsFragment callbacks
    public void onEventSelected(Race race) {
        //RaceFragment raceFragment = RaceFragment.newInstance(race);
        OverviewFragment overviewFragment = OverviewFragment.newInstance(race);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                //.replace(R.id.fragment_container, raceFragment, "RACE_FRAGMENT")
                .replace(R.id.fragment_container, overviewFragment, "OVERVIEW_FRAGMENT")
                .addToBackStack("OVERVIEW_FRAGMENT")
                .commit();

        // TODO: FIX Start service
        //String URL = String.format("%s/getEventIdFromURL/%s", FLASK, race.getRaceURL().split(".com/")[1]);
        //taskFragment.startServiceProcess(URL);
    }

    public void addNewEvent(String inputURL) {
        String requestURL = String.format("%s/add/event", FLASK);
        String username = PreferenceManager.getDefaultSharedPreferences(this).getString("token", null);
        String usertype = PreferenceManager.getDefaultSharedPreferences(this).getString("username", LoginActivity.GUEST).equals(LoginActivity.GUEST) ? "guest" : "multigp";
        taskFragment.addEventByURL(requestURL, inputURL, username, usertype);
    }

    // RaceFragment callbacks
    public void onRaceButton(View view, Race race) {
        switch (view.getId()) {
            /*TODO: remove
            case R.id.race_builder_button:
                BuildRaceStructureAFragment buildRaceStructureAFragment = BuildRaceStructureAFragment.newInstance(0, true, true, false, false, false, false, true, race);
                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.fragment_container, buildRaceStructureAFragment, "BUILD_RACE_STRUCTURE_A_FRAGMENT")
                    .addToBackStack("BUILD_RACE_STRUCTURE_A_FRAGMENT")
                    .commit();
                break;
            case R.id.race_info_button:
                RaceInfoFragment raceInfoFragment = RaceInfoFragment.newInstance(race);
                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.fragment_container, raceInfoFragment, "RACE_INFO_FRAGMENT")
                    .addToBackStack("RACE_INFO_FRAGMENT")
                    .commit();
                break;
            case R.id.race_racers_button:
                RaceRacersFragment raceRacersFragment = RaceRacersFragment.newInstance(race);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.fragment_container, raceRacersFragment, "RACE_RACERS_FRAGMENT")
                        .addToBackStack("RACE_RACERS_FRAGMENT")
                        .commit();
                break;*/
            case R.id.race_schedule_button:
                RaceScheduleFragment raceScheduleFragment = RaceScheduleFragment.newInstance(race);
                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.fragment_container, raceScheduleFragment, "RACE_SCHEDULE_FRAGMENT")
                    .addToBackStack("RACE_SCHEDULE_FRAGMENT")
                    .commit();
                break;
            default:
                Log.i("RACE_FRAGMENT", "Unknown button pressed");
                break;
        }
    }

    public void onSendStatusUpdate(Race race, int round, int heat) {
        taskFragment.setCurrentHeat(race, round, heat);

    }

    public void getUpdatedAttendance(Race race) {
        String requestURL = String.format("%s/update/race/attendance/multigp", FLASK);
        String inputURL = race.getRaceURL();
        taskFragment.getUpdatedAttendance(requestURL, inputURL);
    }

    // BuildRaceStructureAFragment callbacks
    public void BuildRaceAToB(int numSlots, boolean[] bands, ArrayList<ArrayList<AddFrequencySlot>> freqSlots, Race race) {
        BuildRaceStructureBFragment fragment = BuildRaceStructureBFragment.newInstance(numSlots, bands, freqSlots, race);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.fragment_container, fragment, "BUILD_RACE_STRUCTURE_B_FRAGMENT")
                .addToBackStack("BUILD_RACE_STRUCTURE_B_FRAGMENT")
                .commit();
    }

    // BuildRaceStructureBFragment callbacks
    public void BuildRaceBToC(ArrayList<String> freq, Race race) {
        BuildRaceStructureCFragment fragment = BuildRaceStructureCFragment.newInstance(freq, race);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.fragment_container, fragment, "BUILD_RACE_STRUCTURE_C_FRAGMENT")
                .addToBackStack("BUILD_RACE_STRUCTURE_C_FRAGMENT")
                .commit();
    }

    // BuildRaceStructureCFragment callbacks
    public void BuildRaceCToD(int numSlots, List<List<Racer>> racersInSlots, Race race) {
        BuildRaceStructureDFragment fragment = BuildRaceStructureDFragment.newInstance(numSlots, racersInSlots, race);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.fragment_container, fragment, "BUILD_RACE_STRUCTURE_D_FRAGMENT")
                .addToBackStack("BUILD_RACE_STRUCTURE_D_FRAGMENT")
                .commit();
    }

    // BuildRaceStructureDFragment callbacks
    public void onFinishWizard(Race race) {
        taskFragment.test();
        String URL = String.format("%s/rebuild/race/%s", FLASK, race.getRaceURL().split(".com/")[1]);
        taskFragment.sendRebuiltRace(URL, race);

        // Exit the wizard completely
        getSupportFragmentManager().popBackStack("RACE_FRAGMENT", 0);
    }

    // RaceInfoFragment callbacks
    public void onRaceInfo(Race race) {

    }

    public void onFragmentInteraction(Race race) {

    }

    public void onFragmentInteraction(Uri uri) {

    }

    // RaceScheduleCardFragment callbacks
    public void onUpdateSlotOnServer(Race race, Slot slot, String tag) {
        taskFragment.sendSlotUpdate(race, slot, tag);
    }

    // TaskFragment callbacks
    public void OnRacesLoaded(final ArrayList<Race> races) {
        if (getActiveFragment() instanceof EventsFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    EventsFragment eventsFragment = (EventsFragment) getSupportFragmentManager().findFragmentByTag("EVENTS_FRAGMENT");
                    eventsFragment.clearEventAdapter();
                    eventsFragment.updateEventAdapter(races);
                    eventsFragment.finishRefreshing();
                }
            });
        }
    }

    @Override
    public void OnEventsAddedToUsername() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startUpdating();
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
            raceScheduleFragment.updateRoundAdapter();
        }
    }

    public void UpdateRace(final Race race) {
        if (getActiveFragment() instanceof RaceFragment) {
            final RaceFragment raceFragment = (RaceFragment) getActiveFragment();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //raceFragment.setRace(race);
                    raceFragment.checkRaceStatus();
                    raceFragment.onChangedViewPermissions();
                }
            });
        }
        else if (getActiveFragment() instanceof OverviewFragment) {
            ViewPager viewPager = ((OverviewFragment) getActiveFragment()).getViewPager();
            final RaceFragment raceFragment = (RaceFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //raceFragment.setRace(race);
                    raceFragment.checkRaceStatus();
                    raceFragment.onChangedViewPermissions();
                }
            });
            final RaceRacersFragment raceRacersFragment = (RaceRacersFragment) viewPager.getAdapter().instantiateItem(viewPager, 3);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    raceRacersFragment.updateRace(race);
                }
            });
        }
        else if (getActiveFragment() instanceof RaceScheduleFragment) {
            RaceScheduleFragment raceScheduleFragment = (RaceScheduleFragment) getActiveFragment();
            raceScheduleFragment.updateRoundAdapter();
        }
    }

    public void showPermissionError() {
        if (getActiveFragment() instanceof OverviewFragment) {
            ViewPager viewPager = ((OverviewFragment) getActiveFragment()).getViewPager();
            final RaceFragment raceFragment = (RaceFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    raceFragment.showPermissionError();
                }
            });
        }
        else if (getActiveFragment() instanceof  RaceScheduleFragment) {
            ((RaceScheduleFragment) getActiveFragment()).showPermissionError();
        }
    }

    // Refresh time from SNTP
    public void resyncTime() {
        taskFragment.setSntpOffset();
    }
}
