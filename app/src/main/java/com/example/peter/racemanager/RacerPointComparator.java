package com.example.peter.racemanager;

import com.example.peter.racemanager.models.Racer;

import java.util.Comparator;

/**
 * Created by Peter on 5/28/2016.
 * Comparator to be used in RacerListAdapter to sort racers by name
 */
public class RacerPointComparator implements Comparator<Racer> {
    public int compare(Racer a, Racer b) {
        if (a.getPoints() < b.getPoints()) {
            return 1;
        }
        else if (a.getPoints() > b.getPoints()) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
