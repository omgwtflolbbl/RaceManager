package com.example.peter.racemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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

        // Create instance of event manager fragment
        EventsFragment eventsFragment = new EventsFragment();
        Intent intent = getIntent();
        intent.putExtra(EXTRA_MESSAGE, strJson);
        eventsFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the "fragment_container" LinearLayout
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, eventsFragment).commit();
    }


}
