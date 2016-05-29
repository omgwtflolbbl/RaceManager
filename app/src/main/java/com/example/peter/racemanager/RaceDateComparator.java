package com.example.peter.racemanager;

import java.util.Comparator;

/**
 * Created by Peter on 5/28/2016.
 * Comparator to be used in EventAdapter to sort races by date
 */
public class RaceDateComparator implements Comparator<Race> {
    public int compare(Race a, Race b) {
        if (a.getDateAndTime().before(b.getDateAndTime())) {
            return 1;
        }
        else if (a.getDateAndTime().after(b.getDateAndTime())) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
