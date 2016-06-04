package com.example.peter.racemanager.models;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 6/2/2016.
 */
public class Round {
    private List<Heat> heats;

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

    public List<Round>  fromJsonToRoundList(JSONArray json) {
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

    public List<String> findRacerInRound(String username) {
        List<String> entries = new ArrayList<String>();
        for (int i = 0, size = heats.size(); i <  size; i++) {
            entries.add(Integer.toString(i) + ":" + heats.get(i).findRacerInHeat(username));
        }
        return entries;
    }

    public int length() {
        return heats.size();
    }
}
