package com.example.peter.racemanager.models;

/**
 * Created by Peter on 6/1/2016.
 */
public class Racer {
    private String username;
    private String firstName;
    private String lastName;
    private String racerUrl;
    private String racerPhoto;
    private String droneName;
    private String dronePhoto;
    private String frequency;
    private int points;

    public Racer() {
        this.username = "EMPTY SLOT";
        this.firstName = "-";
        this.lastName = "-";
        this.racerUrl = "-";
        this.racerPhoto = "-";
        this.droneName = "-";
        this.dronePhoto = "-";
        this.frequency = "-";
        this.points = 0;
    }

    public Racer(String username, String firstname, String lastname, String racerUrl, String racerPhoto, String droneName, String dronePhoto, String frequency) {
        this.username = username;
        this.firstName = firstname;
        this.lastName = lastname;
        this.racerUrl = racerUrl;
        this.racerPhoto = racerPhoto;
        this.droneName = droneName;
        this.dronePhoto = dronePhoto;
        this.frequency = frequency;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public String getDronePhoto() {
        return dronePhoto;
    }

    public String getFrequency() {
        return frequency;
    }

    public int getPoints() {
        return points;
    }
}
