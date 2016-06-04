package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Peter on 5/27/2016.
 * Race will take data passed from database for initialization.
 */
public class Race implements Parcelable {
    private String title;
    private String siteURL;
    private String date;
    private String time;
    private String blockquote;
    private String description;
    private String raceId = "";
    private ArrayList<Round> rounds;

    private Date dateAndTime;

    public Race(){}

    public Race(String title, String siteURL, String date, String time, String blockquote, String description, ArrayList<Round> rounds) {
        this.title = title;
        this.siteURL = siteURL;
        this.date = date;
        this.time = time;
        this.blockquote = blockquote;
        this.description = description;
        this.setDateAndTime(date, time);
        this.rounds = rounds;
    }

    public Race(String title, String siteURL, String date, String time, String blockquote, String description, ArrayList<Round> rounds, String raceId) {
        this.title = title;
        this.siteURL = siteURL;
        this.date = date;
        this.time = time;
        this.blockquote = blockquote;
        this.description = description;
        this.setDateAndTime(date, time);
        this.rounds = rounds;
        this.raceId = raceId;
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
            race.setDateAndTime(race.date, race.time);
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

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public ArrayList<Round> getRounds() {
        return rounds;
    }

    public String getRaceId() {
        return raceId;
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

    public void setTime(String time) {
        this.time = time;
    }

    public void setBlockquote(String blockquote) {
        this.blockquote = blockquote;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateAndTime(String date, String time) {
        String dateAndTimeString = date + " / " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy / hh:mm a");
        try {
            this.dateAndTime = sdf.parse(dateAndTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setDateAndTime(Date dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setRounds(ArrayList<Round> rounds) {
        this.rounds = rounds;
    }

    public void setRaceId(String raceId) {
        this.raceId = raceId;
    }

    protected Race(Parcel in) {
        title = in.readString();
        siteURL = in.readString();
        date = in.readString();
        time = in.readString();
        blockquote = in.readString();
        description = in.readString();
        raceId = in.readString();
        long tmpDateAndTime = in.readLong();
        dateAndTime = tmpDateAndTime != -1 ? new Date(tmpDateAndTime) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(siteURL);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(blockquote);
        dest.writeString(description);
        dest.writeString(raceId);
        dest.writeLong(dateAndTime != null ? dateAndTime.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Race> CREATOR = new Parcelable.Creator<Race>() {
        @Override
        public Race createFromParcel(Parcel in) {
            return new Race(in);
        }

        @Override
        public Race[] newArray(int size) {
            return new Race[size];
        }
    };
}