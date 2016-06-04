package com.example.peter.racemanager.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Peter on 6/1/2016.
 */
public class Heat {
    private Map<String, Map<String, String>> heatMap;

    public Heat(JSONObject json) {
        heatMap = new LinkedHashMap<String, Map<String, String>>();
        try {
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Map<String, String> slot = new HashMap<String, String>();
                slot.put("username", json.getJSONObject(key).getString("username"));
                slot.put("frequency", json.getJSONObject(key).getString("frequency"));
                heatMap.put(key, slot);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys() {
        return heatMap.keySet();
    }

    public int numSlots() {
        return heatMap.size();
    }

    public String getUsername(String slot) {
        return heatMap.get(slot).get("username");
    }

    public String getFrequency(String slot) {
        return heatMap.get(slot).get("frequency");
    }

    public Map<String, String> getSlot(String slot) {
        return heatMap.get(slot);
    }

    public String findRacerInHeat(String username) {
        Iterator<String> slots = getKeys().iterator();
        while (slots.hasNext()) {
            String slot = slots.next();
            if (heatMap.get(slot).get("username").equals(username)) {
                return slot;
            }
        }
        return null;
    }

    public void removeRacerFromSlot(String slot) {
        heatMap.get(slot).put("username", "EMPTY");
    }

    public void removeRacerFromHeat(String username) {
        String slot = findRacerInHeat(username);
        if (slot != null) {
            removeRacerFromSlot(slot);
        }
    }

    public void changeRacer(String slot, String username) {
        heatMap.get(slot).put("username", username);
    }

    public void changeFrequency(String slot, String frequency) {
        heatMap.get(slot).put("frequency", frequency);
    }

    public void addSlot(String slot, String frequency) {
        Map<String, String> newSlot = new HashMap<String, String>();
        newSlot.put("frequency", frequency);
        newSlot.put("username", "EMPTY");
        heatMap.put(slot, newSlot);
    }

    public void addSlot(String slot, String frequency, String username) {
        Map<String, String> newSlot = new HashMap<String, String>();
        newSlot.put("frequency", frequency);
        newSlot.put("username", username);
        heatMap.put(slot, newSlot);
    }

    public void removeSlot(String slot) {
        heatMap.remove(slot);
    }
}
