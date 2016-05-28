package com.example.peter.racemanager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Peter on 5/27/2016.
 * Race will take data passed from database for initialization.
 */
public class Race {
    private String title;
    private String siteURL;
    private String date;
    private String time;
    private String blockquote;
    private String description;

    public Race(){}

    public Race(String title, String siteURL, String date, String time, String blockquote, String description) {
        this.title = title;
        this.siteURL = siteURL;
        this.date = date;
        this.time = time;
        this.blockquote = blockquote;
        this.description = description;
    }

    // Tentative constructor for Race objects being built directly from some JSON received from Firebase
    // Will obviously need to be reworked, either here or on the flask server that actually sends it
    public static Race fromJson(JSONObject jsonObject) {
        Race race = new Race();
        try {
            race.title = jsonObject.getString("title");
            race.siteURL = jsonObject.getString("siteURL");
            race.date = jsonObject.getString("date");
            race.time = jsonObject.getString("time");
            race.blockquote = jsonObject.getString("blockquote");
            race.description = jsonObject.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return race;
    }

    // Tentative method to decode an array of JSON results
    public static ArrayList<Race> fromJson(JSONArray jsonArray) {
        JSONObject raceJson;
        ArrayList<Race> races = new ArrayList<Race>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                raceJson = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            Race race = Race.fromJson(raceJson);
            if (race != null) {
                races.add(race);
            }
        }

        return races;
    }

    public String getTitle() {
        return title;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getBlockquote() {
        return blockquote;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBlockquote(String blockquote) {
        this.blockquote = blockquote;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
