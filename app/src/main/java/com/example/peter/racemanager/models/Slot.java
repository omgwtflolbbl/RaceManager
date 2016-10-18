package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Peter on 6/4/2016.
 */
public class Slot implements Parcelable {
    private String username;
    private String frequency;
    private Racer racer;
    private int points;

    public Slot() {
        username = "EMPTY SLOT";
        frequency = "UNDEFINED";
        points = 0;
    }

    public Slot(String frequency) {
        username = "EMPTY SLOT";
        this.frequency = frequency;
        points = 0;
    }

    public Slot(String username, String frequency) {
        this.username = username;
        this.frequency = frequency;
        points = 0;
    }

    public Slot(String username, String frequency, int points) {
        this.username = username;
        this.frequency = frequency;
        this.points = points;
    }

    public Slot(JSONObject json, ArrayList<Racer> racers) {
        setFromJSON(json, racers);
    }

    public boolean setFromJSON(JSONObject json, ArrayList<Racer> racers) {
        try {
            // Check if this slot is actually supposed to have anyone
            if (json.isNull("raceEntryId")) {
                racer = new Racer();
                username = "EMPTY SLOT";
                frequency = !json.isNull("frequency") ? json.getString("frequency") : "UNDEFINED";
                points = 0;
            }
            else {
                // Pull the racer from the racers array and associate it with this slot
                int racingId = json.getInt("raceEntryId");
                for (int i = 0, size = racers.size(); i < size; i++) {
                    if (racers.get(i).getRacingId() == racingId) {
                        racer = racers.get(i);
                        username = racer.getUsername();
                        frequency = !json.isNull("frequency") ? json.getString("frequency") : frequency;
                        points = !json.isNull("score") ? json.getInt("score") : points;
                        break;
                    }
                }
            }
            return true;
        } catch (JSONException e) {
            try {
                Log.d("json slot", json.toString(4));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();

            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getFrequency() {
        return frequency;
    }

    public int getPoints() {
        return points;
    }

    public Racer getRacer() {
        return racer;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setRacer(Racer racer) {
        this.racer = racer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.frequency);
        dest.writeParcelable(this.racer, flags);
        dest.writeInt(this.points);
    }

    protected Slot(Parcel in) {
        this.username = in.readString();
        this.frequency = in.readString();
        this.racer = in.readParcelable(Racer.class.getClassLoader());
        this.points = in.readInt();
    }

    public static final Creator<Slot> CREATOR = new Creator<Slot>() {
        @Override
        public Slot createFromParcel(Parcel source) {
            return new Slot(source);
        }

        @Override
        public Slot[] newArray(int size) {
            return new Slot[size];
        }
    };
}
