package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

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

    public Heat getHeat(int index) {
        return heats.get(index);
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
