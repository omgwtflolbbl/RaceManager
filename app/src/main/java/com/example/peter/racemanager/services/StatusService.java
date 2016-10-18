package com.example.peter.racemanager.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.activities.MainActivity;
import com.example.peter.racemanager.models.Race;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class StatusService extends Service {

    private static final int NOTIFICATION_ID = 1;
    NotificationManager notificationManager;
    private ArrayList<ServiceRunnable> runnables;
    private ArrayList<Thread> threads;
    private String username;

    public StatusService() {
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        threads = new ArrayList<>();
        runnables = new ArrayList<>();
        username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "");
        Log.i("SERVICEINFORMATION", "SERVICE IS BEING CREATED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int eventId = intent.getIntExtra("EVENT_ID", -1);
        Race race = intent.getParcelableExtra("Race");
        if (checkId(eventId) == -1) {
            ServiceRunnable runnable = new ServiceRunnable(race, username);
            Thread thread = new Thread(runnable);
            runnables.add(runnable);
            threads.add(thread);
            thread.start();
        }
        for (int i = 0, size = runnables.size(); i < size; i++) {
            Log.d("Service Runnables", Integer.toString(runnables.get(i).getId()));
        }
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        for (int i = 0; i < threads.size(); i++) {
            runnables.get(i).stopListeners();
            threads.get(i).interrupt();
        }
        runnables.clear();
        threads.clear();
        super.onDestroy();
    }

    // Runnable will listen for changes and send notifications/trigger updates as needed
    private class ServiceRunnable implements Runnable {
        private DatabaseReference mDatabase;
        private Race race;
        private String username;
        private Boolean stopped;

        public ServiceRunnable(Race race, String username) {
            this.race = race;
            this.username = username;
            this.stopped = false;
            mDatabase = FirebaseDatabase.getInstance().getReference(String.format("/id/%d", race.getId()));
        }

        public int getId() {
            return race.getId();
        }

        public void run() {
            // Listens for changes to a specific race's status so that users can automatically know
            // what stage of the race we're at
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // For removal
                    if (stopped) {
                        mDatabase.removeEventListener(this);
                    }
                    // Sends notification and begins update process
                    else {
                        Log.d("Service notice", String.format("%d changed", race.getId()));
                        System.out.println(dataSnapshot.toString());
                        System.out.println(dataSnapshot.hasChildren());
                        if (dataSnapshot.hasChildren()) {
                            race.setTargetTime(Long.parseLong(dataSnapshot.child("targetTime").getValue().toString()));
                            String current = dataSnapshot.child("current").getValue().toString();
                            String next = dataSnapshot.child("next").getValue().toString();
                            sendNotification(current, next);
                        }

                        //sendNotification(status, racing, spotter, onDeck);
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

        public void sendNotification(String current, String next) {
            if (!current.equals("")) {
                String nTitle = "";
                String nMessage = "";
                for (String racer: next.split(",")) {
                    if (racer.equals(username)) {
                        nTitle = "It's almost your turn!";
                        nMessage = username + ", you are flying after this heat! Prepare your quad and be ready to spot if necessary!";
                    }
                }
                for (String racer : current.split(",")) {
                    if (racer.equals(username)) {
                        nTitle = "Time to race!";
                        nMessage = username + ", it's your turn to fly! Bring your quad up to the starting line and ensure you have a spotter!";
                    }
                }
                if (!nMessage.equals("")) {
                    Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    Notification notification = new NotificationCompat.Builder(getApplicationContext())
                            .setContentIntent(contentIntent)
                            .setContentTitle(nTitle)
                            .setContentText(nMessage)
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setSmallIcon(R.drawable.checkered_logo_white)
                            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.MultiGPRed))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(nMessage))
                            .build();

                    notificationManager.notify(NOTIFICATION_ID, notification);
                }
            }
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

    // Checks to see if an event is already being listened to
    private int checkId(int eventId) {
        int index = -1;
        for (int i = 0, size = runnables.size(); i < size; i++) {
            if (runnables.get(i).getId() == eventId) {
                index = i;
            }
        }
        return index;
    }

    // Removes a specific runnable
    public void removeId(int eventId) {
        int i = checkId(eventId);
        runnables.get(i).stopListeners();
        runnables.remove(i);
        threads.get(i).interrupt();
        threads.remove(i);
    }

    // Clears all events and runnables
    public void removeAll() {
        for (int i = 0, size = runnables.size(); i < size; i++) {
            runnables.get(i).stopListeners();
            runnables.remove(i);
            threads.get(i).interrupt();
            threads.remove(i);
        }
    }
}
