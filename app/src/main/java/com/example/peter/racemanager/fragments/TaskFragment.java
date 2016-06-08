package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Round;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskFragment.TaskCallbacks} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {
    /**
     * Interface for fragment to report back to activity
     */
    public interface TaskCallbacks {
        void OnRacesLoaded(ArrayList<Race> races);
        void StartStatusService(String eventId);
        void UpdateRaceSchedule(Race race);
        void UpdateRace(Race race);
    }

    private OkHttpClient client;
    private TaskCallbacks mListener;

    private static final String CLIENT_KEY = "client_key";

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        //args.putParcelable(CLIENT_KEY, client);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // set stuff up
        }
        setRetainInstance(true);
        client = new OkHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    /*
    public void onButtonPressed(String result) {
        if (mListener != null) {
            mListener.OnRacesLoaded(result);
        }
    }*/

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

    public void getEvents(String URL) {
        Log.i("LOGTAG", "WOOHOO");
        Log.i("LOGTAG", URL);
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

                String result = "";
                ArrayList<Race> races = new ArrayList<Race>();

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    //System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                //System.out.println(response.body().string());
                //String result = "";
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    Iterator<String> iter = json.keys();
                    while (iter.hasNext()) {
                        String raceId = iter.next();
                        JSONObject raceJson = json.getJSONObject(raceId);
                        String title = raceJson.getString("title");
                        String date = raceJson.getString("date");
                        String time = raceJson.getString("time");
                        String blockquote = raceJson.getString("blockquote");
                        String description = raceJson.getString("description");
                        String siteURL = raceJson.getString("eventURL");
                        Log.i("HOLD UP", raceJson.getJSONArray("raceStructure").toString());
                        List<Round> rounds = new Round().fromJsonToRoundList(raceJson.getJSONArray("raceStructure"));
                        String status = raceJson.getJSONObject("status").getString("status");
                        Long targetTime = raceJson.getJSONObject("status").getLong("time");

                        Race race = new Race(title, siteURL, date, time, blockquote, description, (ArrayList<Round>) rounds, status, targetTime);
                        races.add(race);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                //System.out.println(response.body().string());
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    result = json.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendEventID(result, method);
            }
        });
    }

    public void sendEventID(String eventId, String method) {
        switch (method) {
            case "SERVICE": mListener.StartStatusService(eventId);
                break;
            case "RACE_SCHEDULE": String URL = String.format("http://dbcf20b1.ngrok.io/events/%s", eventId);
                getUpdatedRaceData(URL, "RACE_SCHEDULE");
                break;
            case "RACE": String URL2 = String.format("http://dbcf20b1.ngrok.io/events/%s", eventId);
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
                    String title = json.getString("title");
                    String date = json.getString("date");
                    String time = json.getString("time");
                    String blockquote = json.getString("blockquote");
                    String description = json.getString("description");
                    String siteURL = json.getString("eventURL");
                    List<Round> rounds = new Round().fromJsonToRoundList(json.getJSONArray("raceStructure"));
                    String status = json.getJSONObject("status").getString("status");
                    Long targetTime = json.getJSONObject("status").getLong("time");

                    race = new Race(title, siteURL, date, time, blockquote, description, (ArrayList<Round>) rounds, status, targetTime);

                } catch (JSONException e) {
                    e.printStackTrace();
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

}
