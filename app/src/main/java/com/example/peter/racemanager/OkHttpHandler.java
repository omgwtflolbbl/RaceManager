package com.example.peter.racemanager;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Peter on 5/31/2016.
 */
public class OkHttpHandler {
    private OkHttpClient client;
    private TaskListener mListener;

    public OkHttpHandler(Context context) {
        mListener = (TaskListener) context;
        client = new OkHttpClient();
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

                String result = "";

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
                        String eventId = iter.next();
                        String title = json.getJSONObject(eventId).getString("title");
                        result = result + "{\"" + eventId + "\": \"" + title + "\"}\n";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Log.i("I GOT TO THE END", result);
                mListener.IHASSTRINGNOW(result);
            }
        });
    }

    public interface TaskListener {
        void IHASSTRINGNOW(String result);
    }

    public static void main(String [ ] args)
    {
        int i = 6%5+1;
        System.out.println(Integer.toString(i));
    }
}

