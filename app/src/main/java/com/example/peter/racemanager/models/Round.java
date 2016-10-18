package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 6/2/2016.
 */
public class Round implements Parcelable {
    private List<Heat> heats;

    public Round() {
        heats = new ArrayList<>();
    }

    public Round fromJsonToRound(JSONArray json) {
        Round round = new Round();
        try {
            round.heats = new ArrayList<Heat>();
            for (int  i = 0, size = json.length(); i < size; i++) {
                Heat heat = new Heat(json.getJSONObject(i));
                round.heats.add(heat);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return round;
    }

    public List<Round> fromJsonToRoundList(JSONArray json) {
        List<Round> rounds = new ArrayList<Round>();
        try {
            for (int i = 0, size = json.length(); i < size; i++) {
                Round round = new Round().fromJsonToRound(json.getJSONArray(i));
                rounds.add(round);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rounds;
    }

    public Round(JSONArray heats, ArrayList<Racer> racers) {
        setFromJSON(heats, racers);
    }


    public boolean setFromJSON(JSONArray heats, ArrayList<Racer> racers) {
        try {
            if (this.heats == null) {
                this.heats = new ArrayList<Heat>();
            }
            this.heats.clear();
            for (int i = 0, size = heats.length(); i < size; i++) {
                this.heats.add(new Heat(heats.getJSONObject(i), racers));
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Round> fromJSONToRoundList(JSONArray json, ArrayList<Racer> racers) {
        List<Round> rounds = new ArrayList<Round>();
        try {
            for (int i = 0, size = json.length(); i < size; i++) {
                rounds.add(new Round(json.getJSONObject(i).getJSONArray("heats"), racers));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rounds;
    }

    public Heat getHeat(int index) {
        return index != -1 && heats.size() > index ? heats.get(index) : new Heat();
    }

    public List<Heat> getHeats() {
        return heats;
    }

    public void addHeat(Heat heat) {
        heats.add(heat);
    }

    public void addHeat(Heat heat, int index) {
        heats.add(index, heat);
    }

    public void removeRacerFromRound(String username) {
        for (int i = 0, size = heats.size(); i < size; i++) {
            Heat heat = heats.get(i);
            heat.removeRacerFromHeat(username);
        }
    }

    public Slot findRacerInRound(String username) {
        List<String> entries = new ArrayList<String>();
        for (int i = 0, size = heats.size(); i <  size; i++) {
            if (heats.get(i).findRacerInHeat(username) != null) {
                return heats.get(i).getSlot(heats.get(i).findRacerInHeat(username));
            }
        }
        return null;
    }

    public int length() {
        return heats.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.heats);
    }

    protected Round(Parcel in) {
        this.heats = in.createTypedArrayList(Heat.CREATOR);
    }

    public static final Creator<Round> CREATOR = new Creator<Round>() {
        @Override
        public Round createFromParcel(Parcel source) {
            return new Round(source);
        }

        @Override
        public Round[] newArray(int size) {
            return new Round[size];
        }
    };
}
