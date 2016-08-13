package com.example.peter.racemanager.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
            String statusText;
            if (status.charAt(0) == 'N') {
                statusText = "The race is still setting up";
            }
            else if (status.charAt(0) == 'W') {
                statusText = String.format(Locale.US, "Preparing for round %d, heat %d", Integer.parseInt(status.split(" ")[1]) + 1, Integer.parseInt(status.split(" ")[2]) + 1);
            }
            else if (status.charAt(0) == 'R') {
                statusText = String.format(Locale.US, "Round %d, heat %d is in the air!", Integer.parseInt(status.split(" ")[1]) + 1, Integer.parseInt(status.split(" ")[2]) + 1);
            }
            else if (status.charAt(0) == 'T') {
                statusText = "";
            }
            else {
                statusText = "The race is over!";
            }
            racing = racing.replace(username, String.format("<b>%s</b>", username));
            spotter = spotter.replace(username, String.format("<b>%s</b>", username));
            onDeck = onDeck.replace(username, String.format("<b>%s</b>", username));
            String notificationText = String.format("<b>Status</b>: %s<br><b>Racing</b>: %s<br><b>Spotting</b>: %s<br><b>On Deck</b>: %s", statusText, racing, spotter, onDeck);

            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentIntent(contentIntent)
                    .setContentTitle("RACESYNC UPDATE")
                    .setContentText(String.format("Status: %s (Pull down)", statusText))
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setSmallIcon(R.drawable.checkered_logo_white)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.MultiGPRed))
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
