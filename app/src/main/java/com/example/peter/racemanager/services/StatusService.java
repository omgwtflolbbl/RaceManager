package com.example.peter.racemanager.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatusService extends Service {

    private static final int NOTIFICATION_ID = 1;
    NotificationManager notificationManager;
    private ArrayList<ServiceRunnable> runnables;
    private ArrayList<Thread> threads;

    public StatusService() {
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        threads = new ArrayList<>();
        runnables = new ArrayList<>();
        Log.i("SERVICEINFORMATION", "SERVICE IS BEING CREATED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String eventId = intent.getStringExtra("EVENT_ID");
        String username = intent.getStringExtra("USERNAME");
        ServiceRunnable runnable = new ServiceRunnable(eventId, username);
        Thread thread = new Thread(runnable);
        runnables.add(runnable);
        threads.add(thread);
        thread.start();

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "SERVICE IS ENDING", Toast.LENGTH_SHORT).show();
        Log.i("SERVICEINFORMATION", "SERVICE IS BEING DESTROYED");
        for (int i = 0; i < threads.size(); i++) {
            runnables.get(i).stopListeners();
            threads.get(i).interrupt();
        }
        super.onDestroy();
    }

    // Runnable will listen for changes and send notifications/trigger updates as needed
    private class ServiceRunnable implements Runnable {
        private DatabaseReference mDatabase;
        private String eventId;
        private String username;
        private Boolean stopped;

        public ServiceRunnable(String eventId, String username) {
            this.eventId = eventId;
            this.username = username;
            this.stopped = false;
            mDatabase = FirebaseDatabase.getInstance().getReference(String.format("events/%s", eventId));
        }

        public void run() {
            // Listens for changes to a specific race's status so that users can automatically know
            // what stage of the race we're at
            mDatabase.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // For removal
                    if (stopped) {
                        mDatabase.removeEventListener(this);
                    }
                    // Sends notification and begins update process
                    else {
                        String status = (String) dataSnapshot.child("status").getValue();
                        String racing = (String) dataSnapshot.child("racing").getValue();
                        String spotter = (String) dataSnapshot.child("spotting").getValue();
                        String onDeck = (String) dataSnapshot.child("ondeck").getValue();

                        sendNotification(status, racing, spotter, onDeck);
                        startAutomaticUpdate();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("SERVICE ERROR", databaseError.toString());
                    if (stopped) {
                        mDatabase.removeEventListener(this);
                    }
                }
            });

            // Listens for any changes to the actual structure of the race. This means point updates,
            // moved racers, changed frequencies, whatever. Should NOT create a notification, only
            // a silent update.
            mDatabase.child("raceStructure").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // For removal
                    if (stopped) {
                        mDatabase.removeEventListener(this);
                    }
                    else {
                        startAutomaticUpdate();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("SERVICE ERROR", databaseError.toString());
                    if (stopped) {
                        mDatabase.removeEventListener(this);
                    }
                }
            });
        }

        // Builds a notification and sends it to be viewed.
        // TODO: Open RaceManager from notification press
        public void sendNotification(String status, String racing, String spotter, String onDeck) {
            racing = racing.replace(username, String.format("<b>%s</b>", username));
            spotter = spotter.replace(username, String.format("<b>%s</b>", username));
            onDeck = onDeck.replace(username, String.format("<b>%s</b>", username));
            String notificationText = String.format("<b>Status</b>: %s<br><b>Racing</b>: %s<br><b>Spotting</b>: %s<br><b>On Deck</b>: %s", status, racing, spotter, onDeck);
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("RACE MANAGER UPDATE")
                    .setContentText(String.format("Status: %s (Pull down)", status))
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setSmallIcon(R.drawable.ic_flight_takeoff_white_24dp)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(notificationText)))
                    .build();

            notificationManager.notify(NOTIFICATION_ID, notification);
        }

        // For removal
        public void stopListeners() {
            stopped = true;
        }
    }

    // Sends broadcast to be picked up by MainActivity. The MainActivity should then sort out how to
    // update based on current screen via MainActivity.startUpdating.
    public void startAutomaticUpdate() {
        Log.i("SERVICEINFORMATION", "BROADCASTING");
        Intent intent = new Intent("RaceManager-Update-Info");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
