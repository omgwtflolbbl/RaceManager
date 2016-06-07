package com.example.peter.racemanager.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.example.peter.racemanager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusService extends Service {

    private static final int NOTIFICATION_ID = 1;
    NotificationManager notificationManager;

    public StatusService() {
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SERVICE INTENT HAS", intent.getStringExtra("EVENT_ID"));
        String eventId = intent.getStringExtra("EVENT_ID");
        Toast.makeText(getApplicationContext(), eventId, Toast.LENGTH_SHORT).show();
        ServiceRunnable runnable = new ServiceRunnable(eventId);
        Thread thread = new Thread(runnable);
        thread.start();


        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "SERVICE IS ENDING", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private class ServiceRunnable implements Runnable {
        private DatabaseReference mDatabase;
        private String eventId;

        public ServiceRunnable(String eventId) {
            this.eventId = eventId;
            mDatabase = FirebaseDatabase.getInstance().getReference(String.format("events/%s/status", eventId));
        }

        public void run() {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("SNAPSHOT", dataSnapshot.toString());
                    String status = (String) dataSnapshot.child("status").getValue();
                    String racing = (String) dataSnapshot.child("racing").getValue();
                    String spotter = (String) dataSnapshot.child("spotting").getValue();
                    String onDeck = (String) dataSnapshot.child("ondeck").getValue();
                    String value = (String) dataSnapshot.child("spotting").getValue();
                    Log.i("SERVICE UPDATE", value == null ? "It's null":value);



                    dosomething(status, racing, spotter, onDeck);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("SERVICE ERROR", databaseError.toString());
                }
            });
        }

        public void dosomething(String status, String racing, String spotter, String onDeck) {
            Log.i("SOMETHING", "WAS DONE");
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
    }
}
