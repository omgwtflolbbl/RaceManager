package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    private ArrayList<Racer> racers;
    private ArrayList<String> admins;
    private String status;
    private Long targetTime;

    private Date dateAndTime;

    public Race(){}

    public Race(String title, String siteURL, String date, String time, String blockquote, String description, ArrayList<Round> rounds, ArrayList<Racer> racers, ArrayList<String> admins, String status, Long targetTime) {
        this.title = title;
        this.siteURL = siteURL;
        this.date = date;
        this.time = time;
        this.blockquote = blockquote;
        this.description = description;
        this.racers = racers;
        this.setDateAndTime(date, time);
        this.rounds = rounds;
        this.admins = admins;
        this.status = status;
        this.targetTime = targetTime;
    }

    public Race(String title, String siteURL, String date, String time, String blockquote, String description, ArrayList<Round> rounds, ArrayList<Racer> racers, ArrayList<String> admins, String status, Long targetTime, String raceId) {
        this.title = title;
        this.siteURL = siteURL;
        this.date = date;
        this.time = time;
        this.blockquote = blockquote;
        this.description = description;
        this.setDateAndTime(date, time);
        this.rounds = rounds;
        this.racers = racers;
        this.admins = admins;
        this.raceId = raceId;
        this.status = status;
        this.targetTime = targetTime;
    }
    // Tentative constructor for Race objects being built directly from some JSON received from Firebase
    // Will obviously need to be reworked, either here or on the flask server that actually sends it
    public static Race fromJson(JSONObject json) {
        Race race = new Race();
        try {
            // Basic race info
            race.title = json.getString("title");
            race.date = json.getString("date");
            race.time = json.getString("time");
            race.setDateAndTime(race.date, race.time);
            race.blockquote = json.getString("blockquote");
            race.description = json.getString("description");
            race.siteURL = json.getString("eventURL");

            // Round information
            List<Round> rounds = new Round().fromJsonToRoundList(json.getJSONArray("raceStructure"));
            race.rounds = (ArrayList<Round>) rounds;

            // Racer information
            Iterator<String> racerKeys = json.getJSONObject("racers").keys();
            ArrayList<Racer> racers = new ArrayList<Racer>();
            while (racerKeys.hasNext()) {
                String username = racerKeys.next();
                JSONObject userJson = json.getJSONObject("racers").getJSONObject(username);
                String racerURL = userJson.getString("racerPage");
                String racerPhoto = userJson.getString("racerPhoto");
                String droneName = userJson.getString("dronename");
                String droneURL = userJson.getString("droneURL");
                String frequency = userJson.getString("frequency");
                racers.add(new Racer(username, racerURL, racerPhoto, droneName, droneURL, frequency));
            }
            race.racers = racers;

            // Admin information
            Iterator<String> adminIter = json.getJSONObject("admins").keys();
            ArrayList<String> admins = new ArrayList<String>();
            while (adminIter.hasNext()) {
                admins.add(adminIter.next());
            }
            race.admins = admins;

            // Status data
            race.status = json.getJSONObject("status").getString("status");
            race.targetTime = Long.parseLong(json.getJSONObject("status").getString("time"));
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

    public int[] getNext(int[] current) {
        // First check if the "current" is even valid and return invalid if not
        if (current[0] == -1 || current[1] == -1) {
            return new int[] {-1, -1};
        }

        int cRound = current[0];
        int cHeat = current[1];
        int[] next = new int[2];

        // Check if current round has another heat
        int cRoundSize = rounds.get(cRound).length();
        if (cHeat + 1 < cRoundSize) {
            // Success, so get the next heat index
            next[0] = cRound;
            next[1] = cHeat + 1;
        }
        // There are no more heats left in the current round
        // Check if the race has another round
        else if (cRound + 1 < rounds.size()) {
            // Success, but check to make sure there is actually a heat in this round
            if (rounds.get(cRound + 1).length() > 0) {
                // Success, so get the first heat in the next round
                next[0] = cRound + 1;
                next[1] = 0;
            }
        }
        // There are no more valid heats or rounds left
        else {
            next[0] = -1;
            next[1] = -1;
        }
        return next;
    }

    // Calculate all the point totals and update accordingly. In the future only the admin will need
    // to call this, but for now just call it anytime it is actually necessary to use total point
    // values.
    // TODO: This should return a new list of users (or have getRacers called to pull update)
    public void calculatePoints() {
        for (int i = 0; i < getRacers().size(); i++) {
            String username = getRacers().get(i).getUsername();
            int total = 0;
            for (int j = 0; j < getRounds().size(); j++) {
                Slot slot = getRounds().get(j).findRacerInRound(username);
                if (slot != null) {
                    total = total + slot.getPoints();
                }
            }
            getRacers().get(i).setPoints(total);
        }
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

    public ArrayList<Racer> getRacers() {
        return racers;
    }

    public ArrayList<String> getAdmins() {
        return admins;
    }

    public String getStatus() {
        return status;
    }

    public Long getTargetTime() {
        return targetTime;
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

    public void setRacers(ArrayList<Racer> racers) {
        this.racers = racers;
    }

    public void setAdmins(ArrayList<String> admins) {
        this.admins = admins;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTargetTime(Long targetTime) {
        this.targetTime = targetTime;
    }

    public void setRaceId(String raceId) {
        this.raceId = raceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.siteURL);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeString(this.blockquote);
        dest.writeString(this.description);
        dest.writeString(this.raceId);
        dest.writeTypedList(this.rounds);
        dest.writeTypedList(this.racers);
        dest.writeString(this.status);
        dest.writeValue(this.targetTime);
        dest.writeLong(this.dateAndTime != null ? this.dateAndTime.getTime() : -1);
    }

    protected Race(Parcel in) {
        this.title = in.readString();
        this.siteURL = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.blockquote = in.readString();
        this.description = in.readString();
        this.raceId = in.readString();
        this.rounds = in.createTypedArrayList(Round.CREATOR);
        this.racers = in.createTypedArrayList(Racer.CREATOR);
        this.status = in.readString();
        this.targetTime = (Long) in.readValue(Long.class.getClassLoader());
        long tmpDateAndTime = in.readLong();
        this.dateAndTime = tmpDateAndTime == -1 ? null : new Date(tmpDateAndTime);
    }

    public static final Creator<Race> CREATOR = new Creator<Race>() {
        @Override
        public Race createFromParcel(Parcel source) {
            return new Race(source);
        }

        @Override
        public Race[] newArray(int size) {
            return new Race[size];
        }
    };
}