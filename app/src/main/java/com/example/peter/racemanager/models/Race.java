package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Peter on 5/27/2016.
 * Race will take data passed from database for initialization.
 */
public class Race implements Parcelable {
    private int id;
    private String title;
    private String raceURL;
    private String raceImage;
    private String blockquote;
    private String description;
    private int chapterId;
    private String date;
    private String time;
    private Date dateAndTime;
    private String city;
    private String state;
    private String zip;
    private String country;
    private double longitude;
    private double latitude;
    private int currentRound;
    private int currentHeat;
    private ArrayList<Round> rounds;
    private ArrayList<Racer> racers;
    private String currentState;
    private String status;
    private long targetTime;
    private boolean joined;


    public Race(){}

    public Race(String title, String raceURL, String date, String time, String blockquote, String description, String raceImage, ArrayList<Round> rounds, ArrayList<Racer> racers, ArrayList<String> admins, String currentState, Long targetTime) {
        this.title = title;
        this.raceURL = raceURL;
        this.date = date;
        this.time = time;
        this.blockquote = blockquote;
        this.description = description;
        this.raceImage = raceImage;
        this.racers = racers;
        this.setDateAndTime(date, time);
        this.rounds = rounds;
        this.currentState = currentState;
        this.targetTime = targetTime;
    }

    public Race(String title, String raceURL, String date, String time, String blockquote, String description, String raceImage, ArrayList<Round> rounds, ArrayList<Racer> racers, ArrayList<String> admins, String currentState, Long targetTime, String raceId) {
        this.title = title;
        this.raceURL = raceURL;
        this.date = date;
        this.time = time;
        this.blockquote = blockquote;
        this.description = description;
        this.raceImage = raceImage;
        this.setDateAndTime(date, time);
        this.rounds = rounds;
        this.racers = racers;
        this.currentState = currentState;
        this.targetTime = targetTime;
    }
    // Tentative constructor for Race objects being built directly from some JSON received from Firebase
    // Will obviously need to be reworked, either here or on the flask server that actually sends it
    public static Race fromJson(JSONObject json) {
        Race race = new Race();
        try {
            // Basic race info
            race.title = json.getString("title");
            race.date = json.getString("date");
            race.time = json.getString("time");
            race.setDateAndTime(race.date, race.time);
            race.blockquote = json.getString("blockquote");
            race.description = json.getString("description");
            race.raceImage = json.getString("chapterImage");
            race.raceURL = json.getString("eventURL");

            // Round information
            List<Round> rounds = new Round().fromJsonToRoundList(json.getJSONArray("raceStructure"));
            race.rounds = (ArrayList<Round>) rounds;

            // Racer information
            Iterator<String> racerKeys = json.getJSONObject("racers").keys();
            ArrayList<Racer> racers = new ArrayList<Racer>();
            while (racerKeys.hasNext()) {
                String username = racerKeys.next();
                JSONObject userJson = json.getJSONObject("racers").getJSONObject(username);
                String racerURL = userJson.getString("racerPage");
                String racerPhoto = userJson.getString("racerPhoto");
                String droneName = userJson.getString("dronename");
                String droneURL = userJson.getString("droneURL");
                String frequency = userJson.getString("frequency");
                racers.add(new Racer(username, racerURL, racerPhoto, droneName, droneURL, frequency));
            }
            race.racers = racers;

            // Admin information
            Iterator<String> adminIter = json.getJSONObject("admins").keys();
            ArrayList<String> admins = new ArrayList<String>();
            while (adminIter.hasNext()) {
                admins.add(adminIter.next());
            }

            // Status data
            race.currentState = json.getJSONObject("currentState").getString("currentState");
            race.targetTime = Long.parseLong(json.getJSONObject("currentState").getString("time"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return race;
    }

    public Race(JSONObject json, int pilotId) {
        setFromJSON(json, pilotId);
    }
    
    // Tentative way to update a race from JSON strings
    public boolean setFromJSON(JSONObject json, int pilotId) {

        try {
            id = json.has("id") ? json.getInt("id") : id;
            title = json.getString("name");
            raceURL = json.getString("urlName");
            raceImage = !json.isNull("mainImageFileName") ? json.getString("mainImageFileName") : raceImage;
            blockquote = json.getString("description");
            description = json.getString("content");
            chapterId = !json.isNull("chapterId") ? json.getInt("chapterId") : chapterId;
            dateAndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.US).parse(json.getString("startDate"));
            date = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(dateAndTime);
            time = new SimpleDateFormat("hh:mm aa", Locale.US).format(dateAndTime);
            city = !json.isNull("city") ? json.getString("city") : city;
            state = !json.isNull("state") ? json.getString("state") : state;
            zip = !json.isNull("zip") ? json.getString("zip") : zip;
            country = !json.isNull("country") ? json.getString("country") : country;
            latitude = !json.isNull("latitude") ? json.getDouble("latitude") : -99999;
            longitude = !json.isNull("longitude") ? json.getDouble("longitude") : -99999;
            currentRound = !json.isNull("currentCycle") ? json.getInt("currentCycle") - 1 : currentRound;
            currentHeat = !json.isNull("currentHeat") ? json.getInt("currentHeat") - 1 : currentHeat;
            status = !json.isNull("status") ? json.getString("status") : status;
            // TODO targetTime;
            targetTime = !json.isNull("targetTime") ? json.getLong("targetTime") : targetTime;
            calculateCurrentState();

            // Racer information and joined status
            if (racers == null) {
                racers = new ArrayList<Racer>();
            }
            if (!json.isNull("entries")) {
                racers.clear();
                joined = false;
                JSONArray entries = json.getJSONArray("entries");
                for (int i = 0, size = entries.length(); i < size; i++) {
                    Racer racer = new Racer(entries.getJSONObject(i));
                    racers.add(racer);
                    if (racer.getPilotId() == pilotId) {
                        joined = true;
                    }

                }
            }

            // Round information
            if (rounds == null) {
                rounds = new ArrayList<Round>();
            }
            if (!json.isNull("schedule")) {
                rounds.clear();
                rounds.addAll(new Round().fromJSONToRoundList(json.getJSONObject("schedule").getJSONArray("groups"), racers));
            }

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tentative method to decode an array of JSON results
    public static ArrayList<Race> fromJson(JSONArray jsonArray) {
        JSONObject raceJson;
        ArrayList<Race> races = new ArrayList<Race>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                raceJson = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            Race race = Race.fromJson(raceJson);
            if (race != null) {
                races.add(race);
            }
        }

        return races;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        try {
            json.put("title", getTitle());
            json.put("date", getDate());
            json.put("time", getTime());
            json.put("blockquote", getBlockquote());
            json.put("description", getDescription());
            json.put("eventURL", getRaceURL());
            json.put("chapterImage", getRaceImage());

            JSONObject statusJson = new JSONObject();
            statusJson.put("ondeck", "None");
            statusJson.put("racing", "None");
            statusJson.put("spotting", "None");
            statusJson.put("currentState", getCurrentState());
            statusJson.put("time", getTargetTime());
            json.put("currentState", statusJson);

            JSONObject adminsJson = new JSONObject();
            for (String admin : getAdmins()) {
                adminsJson.put(admin, "true");
            }
            json.put("admins", adminsJson);

            JSONObject racersJson = new JSONObject();
            for (Racer racer : getRacers()) {
                JSONObject racerJson = new JSONObject();
                racerJson.put("droneURL", racer.getdroneURL());
                racerJson.put("dronename", racer.getDroneName());
                racerJson.put("frequency", racer.getFrequency());
                racerJson.put("racerPage", racer.getRacerUrl());
                racerJson.put("racerPhoto", racer.getRacerPhoto());
                racersJson.put(racer.getUsername(), racerJson);
            }
            json.put("racers", racersJson);

            JSONArray raceStructureJson = new JSONArray();
            for (Round round : getRounds()) {
                JSONArray roundJson = new JSONArray();
                for (Heat heat : round.getHeats()) {
                    JSONObject heatJson = new JSONObject();
                    for (String slotKey : heat.getKeys()) {
                        JSONObject slotJson = new JSONObject();
                        slotJson.put("username", heat.getSlot(slotKey).getUsername());
                        slotJson.put("frequency", heat.getSlot(slotKey).getFrequency());
                        slotJson.put("points", heat.getSlot(slotKey).getPoints());
                        heatJson.put(slotKey, slotJson);
                    }
                    roundJson.put(heatJson);
                }
                raceStructureJson.put(roundJson);
            }
            json.put("raceStructure", raceStructureJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
    
    public void updateAll(Race race) {
        title = race.getTitle() != null ? race.getTitle() : title;
        raceURL = race.getRaceURL() != null ? race.getRaceURL() : raceURL;
        raceImage = race.getRaceImage() != null ? race.getRaceImage() : raceImage;
        blockquote = race.getBlockquote() != null ? race.getBlockquote() : blockquote;
        description = race.getDescription() != null ? race.getDescription() : description;
        chapterId = race.getChapterId() != 0 ? race.getChapterId() : chapterId;
        date = race.getDate() != null ? race.getDate() : date;
        time = race.getTime() != null ? race.getTime() : time;
        dateAndTime = race.getDateAndTime() != null ? race.getDateAndTime() : dateAndTime;
        city = race.getCity() != null ? race.getCity() : city;
        state = race.getState() != null ? race.getState() : state;
        zip = race.getZip() != null ? race.getZip() : zip;
        country = race.getCountry() != null ? race.getCountry() : country;
        longitude = race.getLongitude();
        latitude = race.getLatitude();
        currentRound = race.getCurrentRound();
        currentHeat = race.getCurrentHeat();
        rounds = race.getRounds() != null && race.getRounds().size() > 0 ? race.getRounds() : rounds;
        racers = race.getRacers() != null && race.getRacers().size() > 0 ? race.getRacers() : racers;
        status = race.getStatus() != null ? race.getStatus() : status;
        targetTime = race.getTargetTime();
        calculateCurrentState();
    }

    public int[] getCurrent() {
        return rounds.size() == 0 ? new int[] {-1, -1} : new int[] {currentRound, currentHeat};
    }

    public int[] getNext() {
        // First check if the "current" is even valid and return invalid if not
        if (currentRound == -1 || currentHeat == -1 || rounds.size() == 0) {
            return new int[] {-1, -1};
        }

        int[] next = new int[2];

        // Check if current round has another heat
        int cRoundSize = rounds.get(currentRound).length();
        if (currentHeat + 1 < cRoundSize) {
            // Success, so get the next heat index
            next[0] = currentRound;
            next[1] = currentHeat + 1;
        }
        // There are no more heats left in the current round
        // Check if the race has another round
        else if (currentRound + 1 < rounds.size()) {
            // Success, but check to make sure there is actually a heat in this round
            if (rounds.get(currentRound + 1).length() > 0) {
                // Success, so get the first heat in the next round
                next[0] = currentRound + 1;
                next[1] = 0;
            }
        }
        // There are no more valid heats or rounds left
        else {
            next[0] = -1;
            next[1] = -1;
        }
        return next;
    }

    public int[] getPrevious() {
        // First check if the "current" is even valid and return invalid if not
        if (currentRound == -1 || currentHeat == -1 || rounds.size() == 0) {
            return new int[] {-1, -1};
        }

        int[] last = new int[2];

        // Check if current round was the first heat (if so there was no previous heat)
        if (currentRound == 0 && currentHeat == 0) {
            // This is the first heat - return invalid
            return new int[] {-1, -1};
        }
        // There was a previous heat
        else {
            // Check if this was the first heat of the round
            if (currentHeat == 0) {
                // It was the first heat, so we need to get the last heat of the previous round
                last[0] = currentRound - 1;
                last[1] = rounds.get(currentRound - 1).length() - 1;
            }
            else {
                // There was a previous heat in this round
                last[0] = currentRound;
                last[1] = currentHeat - 1;
            }
        }

        return last;
    }

    // Calculate all the point totals and update accordingly. In the future only the admin will need
    // to call this, but for now just call it anytime it is actually necessary to use total point
    // values.
    // TODO: This should be unneeded
    public void calculatePoints() {
        /*
        for (int i = 0; i < getRacers().size(); i++) {
            String username = getRacers().get(i).getUsername();
            int total = 0;
            for (int j = 0; j < getRounds().size(); j++) {
                Slot slot = getRounds().get(j).findRacerInRound(username);
                if (slot != null) {
                    total = total + slot.getPoints();
                }
            }
            getRacers().get(i).setPoints(total);
        }*/
    }

    public Round getRound(int i) {
        return i <= rounds.size() && i >= 0 ? rounds.get(i) : new Round();
    }

    private void calculateCurrentState() {
        if (targetTime == 0 || currentRound == -1 || currentHeat == -1) {
            currentState = "NS";
        }
        else if (targetTime == -1) {
            currentState = "F";
        }
        else if (targetTime == 1) {
            currentState = "W";
        }
        else if (targetTime > 1){
            currentState = "R";
        }
    }

    public int getId() {
        return id;
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int round) {
        currentRound = round;
    }

    public int getCurrentHeat() {
        return currentHeat;
    }

    public void setCurrentHeat(int heat) {
        currentHeat = heat;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public String getStatus() {
        return status;
    }

    public boolean getJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public String getTitle() {
        return title;
    }

    public String getRaceURL() {
        return raceURL;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getBlockquote() {
        return blockquote;
    }

    public String getDescription() {
        return description;
    }

    public String getRaceImage() {
        return raceImage;
    }

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public ArrayList<Round> getRounds() {
        return rounds;
    }

    public ArrayList<Racer> getRacers() {
        return racers;
    }

    public ArrayList<String> getAdmins() {
        return null;
    }

    public String getCurrentState() {
        return currentState;
    }

    public long getTargetTime() {
        return targetTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRaceURL(String raceURL) {
        this.raceURL = raceURL;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBlockquote(String blockquote) {
        this.blockquote = blockquote;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateAndTime(String date, String time) {
        String dateAndTimeString = date + " / " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy / hh:mm a");
        try {
            this.dateAndTime = sdf.parse(dateAndTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setDateAndTime(Date dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setRounds(ArrayList<Round> rounds) {
        this.rounds = rounds;
    }

    public void setRacers(ArrayList<Racer> racers) {
        this.racers = racers;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public void setTargetTime(long targetTime) {
        this.targetTime = targetTime;
        System.out.println("TT: " + targetTime);
        calculateCurrentState();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.raceURL);
        dest.writeString(this.raceImage);
        dest.writeString(this.blockquote);
        dest.writeString(this.description);
        dest.writeInt(this.chapterId);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeLong(this.dateAndTime != null ? this.dateAndTime.getTime() : -1);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.zip);
        dest.writeString(this.country);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeInt(this.currentRound);
        dest.writeInt(this.currentHeat);
        dest.writeTypedList(this.rounds);
        dest.writeTypedList(this.racers);
        dest.writeString(this.currentState);
        dest.writeString(this.status);
        dest.writeValue(this.targetTime);
    }

    protected Race(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.raceURL = in.readString();
        this.raceImage = in.readString();
        this.blockquote = in.readString();
        this.description = in.readString();
        this.chapterId = in.readInt();
        this.date = in.readString();
        this.time = in.readString();
        long tmpDateAndTime = in.readLong();
        this.dateAndTime = tmpDateAndTime == -1 ? null : new Date(tmpDateAndTime);
        this.city = in.readString();
        this.state = in.readString();
        this.zip = in.readString();
        this.country = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.currentRound = in.readInt();
        this.currentHeat = in.readInt();
        this.rounds = in.createTypedArrayList(Round.CREATOR);
        this.racers = in.createTypedArrayList(Racer.CREATOR);
        this.currentState = in.readString();
        this.status = in.readString();
        this.targetTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<Race> CREATOR = new Creator<Race>() {
        @Override
        public Race createFromParcel(Parcel source) {
            return new Race(source);
        }

        @Override
        public Race[] newArray(int size) {
            return new Race[size];
        }
    };
}