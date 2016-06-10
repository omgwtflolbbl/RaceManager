package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Peter on 6/1/2016.
 */
public class Racer implements Parcelable {
    private String username;
    private String racerUrl;
    private String racerPhoto;
    private String droneName;
    private String droneURL;
    private String frequency;
    private int points;

    public Racer() {
        this.username = "EMPTY SLOT";
        this.racerUrl = "-";
        this.racerPhoto = "-";
        this.droneName = "-";
        this.droneURL = "-";
        this.frequency = "-";
        this.points = 0;
    }

    public Racer(String username, String racerUrl, String racerPhoto, String droneName, String droneURL, String frequency) {
        this.username = username;
        this.racerUrl = racerUrl;
        this.racerPhoto = racerPhoto;
        this.droneName = droneName;
        this.droneURL = droneURL;
        this.frequency = frequency;
        this.points = 0;
    }

    public Racer(String username, String racerUrl, String racerPhoto, String droneName, String droneURL, String frequency, int points) {
        this.username = username;
        this.racerUrl = racerUrl;
        this.racerPhoto = racerPhoto;
        this.droneName = droneName;
        this.droneURL = droneURL;
        this.frequency = frequency;
        this.points = points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUsername() {
        return username;
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

    public String getdroneURL() {
        return droneURL;
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
        dest.writeString(this.racerUrl);
        dest.writeString(this.racerPhoto);
        dest.writeString(this.droneName);
        dest.writeString(this.droneURL);
        dest.writeString(this.frequency);
        dest.writeInt(this.points);
    }

    protected Racer(Parcel in) {
        this.username = in.readString();
        this.racerUrl = in.readString();
        this.racerPhoto = in.readString();
        this.droneName = in.readString();
        this.droneURL = in.readString();
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
