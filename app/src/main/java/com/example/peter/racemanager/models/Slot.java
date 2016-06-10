package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Peter on 6/4/2016.
 */
public class Slot implements Parcelable {
    private String username;
    private String frequency;
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

    public String getUsername() {
        return username;
    }

    public String getFrequency() {
        return frequency;
    }

    public int getPoints() {
        return points;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.frequency);
        dest.writeInt(this.points);
    }

    protected Slot(Parcel in) {
        this.username = in.readString();
        this.frequency = in.readString();
        this.points = in.readInt();
    }

    public static final Parcelable.Creator<Slot> CREATOR = new Parcelable.Creator<Slot>() {
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
