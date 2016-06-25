package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.SntpClient;
import com.example.peter.racemanager.activities.MainActivity;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;
import com.example.peter.racemanager.models.Round;
import com.example.peter.racemanager.models.Slot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        Log.i("We're Here","Woot?");
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
                Log.i("Trying SNTP stuff", "Probably gonna fail");
                if (sntpClient.requestTime("0.us.pool.ntp.org", 30000)) {
                    Long sntpTime = sntpClient.getNtpTime();
                    SntpOffset = sntpTime-System.currentTimeMillis();
                    Log.i("GOT SNTP TIME", "IT WAS GLORIOUS");
                }
                else  {
                    Log.i("SNTP FAILURE?", "WHO KNOWS MANG");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void getEvents(String URL) {
        Request request = new Request.Builder()
                .url(URL)
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

                ArrayList<Race> races = new ArrayList<Race>();

                /*Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                System.out.println(response.body().string());
                */
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    Iterator<String> iter = json.keys();
                    while (iter.hasNext()) {
                        String raceId = iter.next();
                        JSONObject raceJson = json.getJSONObject(raceId);
                        Race race = Race.fromJson(raceJson);
                        races.add(race);
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

    public void getUpdatedRace(String URL) {
        getEventIDFromURL(URL, "RACE");
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
                getUpdatedRaceData(URL, "RACE_SCHEDULE");
                break;
            case "RACE": String URL2 = String.format("%s/events/%s", MainActivity.FLASK, eventId);
                getUpdatedRaceData(URL2, "RACE");
                break;
            default: Log.i("WHAT THE HECK MAN", "YOU CRAZY");
                break;
        }
    }

    public void getUpdatedRaceData(String URL, final String method) {
        Request request = new Request.Builder()
                .url(URL)
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
                    race = Race.fromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
                sendRace(race, method);
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

    public void updateDatabaseRaceStatus(String URL, String status, String racers, String spotters, String onDeck, Long targetTimer) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("status", status)
                .add("racing", racers)
                .add("spotting", spotters)
                .add("ondeck", onDeck)
                .add("time", Long.toString(targetTimer))
                .build();
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
                response.close();
            }
        });
    }

    public void updateDatabaseRaceSlot(String URL, Slot slot, String tag) {
        String[] splitTag = tag.split(" ");
        RequestBody body = new FormBody.Builder()
                .add("round", splitTag[0])
                .add("heat", splitTag[1])
                .add("slotKey", splitTag[2])
                .add("username", slot.getUsername())
                .add("frequency", slot.getFrequency())
                .add("points", Integer.toString(slot.getPoints()))
                .build();
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
}
