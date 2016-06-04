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

    public Racer() {
        this.username = "EMPTY";
        this.firstName = "-";
        this.lastName = "-";
        this.racerUrl = "-";
        this.racerPhoto = "-";
        this.droneName = "-";
        this.dronePhoto = "-";
        this.frequency = "-";
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
}
