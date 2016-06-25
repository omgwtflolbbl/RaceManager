package com.example.peter.racemanager;

import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;

import java.util.Comparator;

/**
 * Created by Peter on 5/28/2016.
 * Comparator to be used in RacerListAdapter to sort racers by name
 */
public class RacerNameComparator implements Comparator<Racer> {
    public int compare(Racer a, Racer b) {
        if (a.getUsername().toLowerCase().compareTo(b.getUsername().toLowerCase()) > 0) {
            return 1;
        }
        else if (a.getUsername().toLowerCase().compareTo(b.getUsername().toLowerCase()) < 0) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
