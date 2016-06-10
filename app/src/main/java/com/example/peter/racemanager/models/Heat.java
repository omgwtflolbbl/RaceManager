package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Heat implements Parcelable {
    private Map<String, Slot> heatMap;

    public Heat(JSONObject json) {
        heatMap = new LinkedHashMap<String, Slot>();
        try {
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Slot slot = new Slot(json.getJSONObject(key).getString("username"), json.getJSONObject(key).getString("frequency"), Integer.parseInt(json.getJSONObject(key).getString("points")));
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

    public Slot getSlot(String slotKey) {
        return heatMap.get(slotKey);
    }

    public String findRacerInHeat(String username) {
        Iterator<String> slots = getKeys().iterator();
        while (slots.hasNext()) {
            String slotKey = slots.next();
            if (getSlot(slotKey).getUsername().equals(username)) {
                return slotKey;
            }
        }
        return null;
    }

    public void removeRacerFromSlot(String slotKey) {
        getSlot(slotKey).setUsername("EMPTY SLOT");
    }

    public void removeRacerFromHeat(String username) {
        String slotKey = findRacerInHeat(username);
        if (slotKey != null) {
            removeRacerFromSlot(slotKey);
        }
    }

    public void changeRacer(String slotKey, String username) {
        getSlot(slotKey).setUsername(username);
    }

    public void changeFrequency(String slotKey, String frequency) {
        getSlot(slotKey).setFrequency(frequency);
    }

    public void addSlot(String slotKey) {
        Slot slot = new Slot();
        heatMap.put(slotKey, slot);
    }

    public void addSlot(String slotKey, String frequency, String username) {
        Slot slot = new Slot(username, frequency);
        heatMap.put(slotKey, slot);
    }

    public void removeSlot(String slotKey) {
        heatMap.remove(slotKey);
    }

    public String getAllRacers() {
        Iterator<String> slots = getKeys().iterator();
        String racers = "";
        while (slots.hasNext()) {
            String slotKey = slots.next();
            if (!getSlot(slotKey).getUsername().equals("EMPTY SLOT")) {
                racers = racers + ", " + getSlot(slotKey).getUsername();
            }
        }
        if (!racers.equals("")) {
            racers = racers.substring(2);
        }
        return racers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.heatMap.size());
        for (Map.Entry<String, Slot> entry : this.heatMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    protected Heat(Parcel in) {
        int heatMapSize = in.readInt();
        this.heatMap = new HashMap<String, Slot>(heatMapSize);
        for (int i = 0; i < heatMapSize; i++) {
            String key = in.readString();
            Slot value = in.readParcelable(Slot.class.getClassLoader());
            this.heatMap.put(key, value);
        }
    }

    public static final Creator<Heat> CREATOR = new Creator<Heat>() {
        @Override
        public Heat createFromParcel(Parcel source) {
            return new Heat(source);
        }

        @Override
        public Heat[] newArray(int size) {
            return new Heat[size];
        }
    };
}
