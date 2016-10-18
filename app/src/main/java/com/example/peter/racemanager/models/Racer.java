package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Peter on 6/1/2016.
 */
public class Racer implements Parcelable {
    private String username;
    private String pilotName;
    private int racingId;
    private int pilotId;
    private int aircraftId;
    private String racerUrl;
    private String racerPhoto;
    private String droneName;
    private String scannableId;
    private String frequency;
    private int points;

    public Racer() {

    }

    public Racer(String username, String racerUrl, String racerPhoto, String droneName, String droneURL, String frequency) {
        this.username = username;
        this.racerUrl = racerUrl;
        this.racerPhoto = racerPhoto;
        this.droneName = droneName;
        this.frequency = frequency;
        this.points = 0;
    }

    public Racer(String username, String racerUrl, String racerPhoto, String droneName, String droneURL, String frequency, int points) {
        this.username = username;
        this.racerUrl = racerUrl;
        this.racerPhoto = racerPhoto;
        this.droneName = droneName;
        this.frequency = frequency;
        this.points = points;
    }

    public Racer(JSONObject json) {
        setFromJSON(json);
    }

    public boolean setFromJSON(JSONObject json) {
        try {
            if (json.getString("userName").contains("\"")) {
                String[] string = json.getString("userName").split("\"");
                username = string[1];
                pilotName = string[0].trim() + " " + string[string.length - 1].trim();
            }
            else {
                username = json.getString("userName");
                pilotName = json.getString("pilotName");
            }
            racingId = json.getInt("id");
            pilotId = json.getInt("pilotId");
            aircraftId = !json.isNull("aircraftId") ? json.getInt("aircraftId") : aircraftId;
            racerUrl = !json.isNull("racerURL") ? json.getString("racerURL") : racerUrl;
            racerPhoto = !json.isNull("profilePictureUrl") ? json.getString("profilePictureUrl") : racerPhoto;
            droneName = !json.isNull("aircraftName") ? json.getString("aircraftName") : droneName;
            scannableId = !json.isNull("scannableId") ? json.getString("scannableId") : scannableId;
            frequency = !json.isNull("frequency") ? json.getString("frequency") : frequency;
            points = !json.isNull("score") ? json.getInt("score") : 0;
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getUsername() {
        return username;
    }

    public String getPilotName() {
        return pilotName;
    }

    public int getRacingId() {
        return racingId;
    }

    public int getPilotId() {
        return pilotId;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public String getRacerUrl() {
        return racerUrl;
    }

    public String getRacerPhoto() {
        return racerPhoto;
    }

    public String getDroneName() {
        return droneName;
    }

    // TODO: REMOVE THIS
    public String getdroneURL() {
        return "";
    }

    public String getScannableId() {
        return scannableId;
    }

    public String getFrequency() {
        return frequency;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.pilotName);
        dest.writeInt(this.racingId);
        dest.writeInt(this.pilotId);
        dest.writeInt(this.aircraftId);
        dest.writeString(this.racerUrl);
        dest.writeString(this.racerPhoto);
        dest.writeString(this.droneName);
        dest.writeString(this.scannableId);
        dest.writeString(this.frequency);
        dest.writeInt(this.points);
    }

    protected Racer(Parcel in) {
        this.username = in.readString();
        this.pilotName = in.readString();
        this.racingId = in.readInt();
        this.pilotId = in.readInt();
        this.aircraftId = in.readInt();
        this.racerUrl = in.readString();
        this.racerPhoto = in.readString();
        this.droneName = in.readString();
        this.scannableId = in.readString();
        this.frequency = in.readString();
        this.points = in.readInt();
    }

    public static final Creator<Racer> CREATOR = new Creator<Racer>() {
        @Override
        public Racer createFromParcel(Parcel source) {
            return new Racer(source);
        }

        @Override
        public Racer[] newArray(int size) {
            return new Racer[size];
        }
    };
}
