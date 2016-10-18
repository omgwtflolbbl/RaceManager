package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.peter.racemanager.SntpClient;
import com.example.peter.racemanager.activities.MainActivity;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Slot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This task fragment is meant to be attached the the main activity and retained across all
 * configuration changes. This task fragment should be used for any kind of network calls that
 * are not directly needed to be handled via the long running service class StatusService. This
 * means calls to the server and to Firebase in order to get things like lists of races, event data,
 * user info, etc.
 *
 * Unless you're LoginActivity, in which case I guess you're special and we don't care about you.
 */
public class TaskFragment extends Fragment {
    /**
     * Interface for fragment to report back to activity
     */
    public interface TaskCallbacks {
        void OnRacesLoaded(ArrayList<Race> races);
        void OnEventsAddedToUsername();
        void StartStatusService(String eventId);
        void UpdateRaceSchedule(Race race);
        void UpdateRace(Race race);
        void startUpdating();
        void showPermissionError();
    }

    private OkHttpClient client;
    private TaskCallbacks mListener;

    private static final String CLIENT_KEY = "client_key";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static Long SntpOffset;

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        client = new OkHttpClient();
        setSntpOffset();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskCallbacks) {
            mListener = (TaskCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setSntpOffset() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SntpClient sntpClient = new SntpClient();
                Log.i("SNTP", "Initiate");
                if (sntpClient.requestTime("0.us.pool.ntp.org", 30000)) {
                    Long sntpTime = sntpClient.getNtpTime();
                    SntpOffset = sntpTime-System.currentTimeMillis();
                    Log.i("SNTP", "Success");
                }
                else  {
                    Log.i("SNTP", "Failure");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void getEvents(String URL) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sessionId = sharedPreferences.getString("sessionId", "");
        final int pilotId = sharedPreferences.getInt("pilotId", 0);

        // Build body
        String json = "";
        try {
            JSONObject object = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject joined = new JSONObject();
            joined.put("pilotId", pilotId);
            data.put("joined", joined);
            object.put("data", data);
            object.put("apiKey", MainActivity.API);
            object.put("sessionId", sessionId);
            json = object.toString();
        } catch (JSONException e) {
            Log.d("Login", "Failed to create json string to post");
        }
        RequestBody body = RequestBody.create(JSON, json);

        // Build request
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        // Send request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                ArrayList<Race> races = new ArrayList<Race>();

                /*Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                System.out.println(response.body().string());
                */
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray data = json.getJSONArray("data");
                    for (int i = 0, size = data.length(); i < size; i++) {
                        races.add(new Race(data.getJSONObject(i), pilotId));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
                mListener.OnRacesLoaded(races);
            }
        });
    }

    public void startServiceProcess(String URL) {
        getEventIDFromURL(URL, "SERVICE");
    }

    public void getUpdatedRaceSchedule(String URL) {
        getEventIDFromURL(URL, "RACE_SCHEDULE");
    }

    public void getEventIDFromURL(String URL, final String method) {
        Request request = new Request.Builder()
                .url(URL)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);


                String result = null;
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    result = json.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
                sendEventID(result, method);
            }
        });
    }

    public void sendEventID(String eventId, String method) {
        switch (method) {
            case "SERVICE": mListener.StartStatusService(eventId);
                break;
            case "RACE_SCHEDULE": String URL = String.format("%s/events/%s", MainActivity.FLASK, eventId);
                requestRaceData(URL, "RACE_SCHEDULE");
                break;
            case "RACE": String URL2 = String.format("%s/events/%s", MainActivity.FLASK, eventId);
                requestRaceData(URL2, "RACE");
                break;
            default: Log.i("WHAT THE HECK MAN", "YOU CRAZY");
                break;
        }
    }

    public void requestRaceData(String URL, final String method) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sessionId = sharedPreferences.getString("sessionId", "");
        final int pilotId = sharedPreferences.getInt("pilotId", 0);

        // Build body
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("apiKey", MainActivity.API);
            object.put("sessionId", sessionId);
            json = object.toString();
        } catch (JSONException e) {
            Log.d("Login", "Failed to create json string to post");
        }
        RequestBody body = RequestBody.create(JSON, json);

        // Build request
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Race race = new Race();

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    race.setFromJSON(json, pilotId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
                sendRace(race, method);
            }
        });
    }

    public void requestRaceData(final Race race) {
        // Build URL
        String URL = String.format(Locale.US, "%s/race/view?id=%d", MainActivity.ROOT, race.getId());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sessionId = sharedPreferences.getString("sessionId", "");
        final int pilotId = sharedPreferences.getInt("pilotId", 0);

        // Build body
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("apiKey", MainActivity.API);
            object.put("sessionId", sessionId);
            json = object.toString();
        } catch (JSONException e) {
            Log.d("Login", "Failed to create json string to post");
        }
        RequestBody body = RequestBody.create(JSON, json);

        // Build request
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    race.setFromJSON(json.getJSONObject("data"), pilotId);
                    mListener.UpdateRace(race);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
            }
        });
    }

    public void requestFirebaseData(final Race race) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(String.format("/id/%d", race.getId()));

        mDatabase.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            race.setTargetTime(Long.parseLong(dataSnapshot.child("targetTime").getValue().toString()));
                            //mListener.UpdateRace(race);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Cancelled", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void sendRace(Race race, String method){
        switch (method) {
            case "RACE_SCHEDULE": mListener.UpdateRaceSchedule(race);
                break;
            case "RACE": mListener.UpdateRace(race);
            default:
                break;
        }
    }

    // Todo: set up timer stuff too
    public void setCurrentHeat(final Race race, final int round, final int heat) {
        //Build URL
        String URL = String.format(Locale.US, "%s/race/startHeat?id=%d", MainActivity.ROOT, race.getId());

        Log.d("heatset","here");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sessionId = sharedPreferences.getString("sessionId", "");

        // Build body
        String json = "";
        try {
            JSONObject object = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("cycle", round + 1);
            data.put("heat", heat + 1);
            object.put("data", data);
            object.put("apiKey", MainActivity.API);
            object.put("sessionId", sessionId);
            json = object.toString();
        } catch (JSONException e) {
            Log.d("Login", "Failed to create json string to post");
        }
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                if (response.body().string().contains("\"status\":false")) {
                    mListener.showPermissionError();
                }
                else {
                    if (race.getTargetTime() == 1) {
                        race.setCurrentRound(round);
                        race.setCurrentHeat(heat);
                        sendFlaskUpdate(race, race.getRound(race.getCurrentRound()).getHeat(race.getCurrentHeat()).getAllRacers(), race.getRound(race.getNext()[0]).getHeat(race.getNext()[1]).getAllRacers());
                    }
                    else {
                        sendFlaskUpdate(race, "", "");
                    }
                }
                response.close();
            }
        });
    }

    public void addEventByURL(String requestURL, String inputURL, String username, String usertype) {
        RequestBody body = new FormBody.Builder()
                .add("url", inputURL)
                .add("username", username)
                .add("usertype", usertype)
                .build();
        Request request = new Request.Builder()
                .url(requestURL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                response.close();
                mListener.OnEventsAddedToUsername();
            }
        });
    }

    public void sendRebuiltRace(String URL, Race race) {
        RequestBody body = RequestBody.create(JSON, race.toJSONObject().toString());
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                response.close();
            }
        });
    }

    public void test() {
        JSONObject json;
        String jsonStr = "";
        try {
            json = new JSONObject();
            json.put("HELLO WORLD", "IT'S ME");
            jsonStr = json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(String.format("%s/test",  MainActivity.FLASK))
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                response.close();
            }
        });
    }

    public void getUpdatedAttendance(String requestURL, String inputURL) {
        RequestBody body = new FormBody.Builder()
                .add("url", inputURL)
                .build();
        Request request = new Request.Builder()
                .url(requestURL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                response.close();
                mListener.startUpdating();
            }
        });
    }

    public void sendSlotUpdate(final Race race, Slot slot, String tag) {
        // Build URL string
        String URL = String.format(Locale.US, "%s/race/assignSlot?id=%d&cycle=%s&heat=%s&slot=%s", MainActivity.ROOT, race.getId(), Integer.parseInt(tag.split(" ")[0]) + 1, Integer.parseInt(tag.split(" ")[1]) + 1, Integer.parseInt(tag.split(" ")[2]) + 1);

        Log.d("Update slot", URL);
        Log.d("Slot", Integer.toString(slot.getRacer().getPilotId()));

        // Get session
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sessionId = sharedPreferences.getString("sessionId", "");

        // Build body
        String json = "";
        try {
            JSONObject object = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("pilotId", slot.getRacer().getPilotId());
            data.put("score", slot.getPoints());
            object.put("data", data);
            object.put("apiKey", MainActivity.API);
            object.put("sessionId", sessionId);
            json = object.toString();
        } catch (JSONException e) {
            Log.d("Slot update", "Failed to create json string to post");
        }
        RequestBody body = RequestBody.create(JSON, json);

        // Build request
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                if (!response.isSuccessful()) {
                    if (response.code() == 403) {
                        // This user does not have permission to do this, throw back error message
                        mListener.showPermissionError();
                    }
                    else {
                        throw new IOException("Unexpected code " + response);
                    }
                }
                else {
                    sendFlaskUpdate(race, "", "");
                }
                Log.d("Response", response.body().string());

                response.body().close();
                response.close();
            }
        });
    }

    // Sends update to flask server to notify stuff
    public void sendFlaskUpdate(Race race, String current, String next) {
        // Build URL string
        String URL = String.format(Locale.US, "%s/event/update", MainActivity.FLASK);

        Log.d("Flask", "Update sent");

        // Build body
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("id", race.getId());
            object.put("targetTime", race.getTargetTime());
            object.put("current", current);
            object.put("next", next);
            json = object.toString();
        } catch (JSONException e) {
            Log.d("Slot update", "Failed to create json string to post");
        }
        RequestBody body = RequestBody.create(JSON, json);

        // Build request
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                if (!response.isSuccessful()) {
                    if (response.code() == 403) {
                        // This user does not have permission to do this, throw back error message
                        mListener.showPermissionError();
                    }
                    else {
                        throw new IOException("Unexpected code " + response);
                    }
                }

                Log.d("Response", response.body().string());

                response.body().close();
                response.close();
            }
        });
    }

    public void resignRace(Race race) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sessionId = sharedPreferences.getString("sessionId", "");

        // Build URL string
        String URL = String.format(Locale.US, "%s/race/resign?id=%d", MainActivity.FLASK, race.getId());

        // Build body
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("apiKey", MainActivity.API);
            object.put("sessionId", sessionId);
            json = object.toString();
        } catch (JSONException e) {
            Log.d("Slot update", "Failed to create json string to post");
        }
        RequestBody body = RequestBody.create(JSON, json);

        // Build request
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                if (response.body().string().contains("false"))
                Log.d("Response", response.body().string());

                response.body().close();
                response.close();
            }
        });
    }
}
