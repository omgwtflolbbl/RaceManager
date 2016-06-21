package com.example.peter.racemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 6/20/2016.
 */
public class AddFrequencySlot implements Parcelable {

    public static final String[][] FREQ_MASTER = {
            {"(F1) 5740", "(F2) 5760", "(F3) 5780", "(F4) 5800", "(F5) 5820", "(F6) 5840", "(F7) 5860", "(F8) 5880"},
            {"(E4) 5645", "(E3) 5665", "(E2) 5685", "(E1) 5705", "(E5) 5885", "(E6) 5905", "(E7) 5925", "(E8) 5945"},
            {"(A1) 5725", "(A2) 5745", "(A3) 5765", "(A4) 5785", "(A5) 5805", "(A6) 5825", "(A7) 5845", "(A8) 5865"},
            {"(R1) 5658", "(R2) 5695", "(R3) 5732", "(R4) 5769", "(R5) 5806", "(R6) 5843", "(R7) 5880", "(R8) 5917"},
            {"(B1) 5733", "(B2) 5752", "(B3) 5771", "(B4) 5790", "(B5) 5809", "(B6) 5828", "(B7) 5847", "(B8) 5866"},
            {"(1.3-1) 1080", "(1.3-2) 1120", "(1.3-3) 1160", "(1.3-4) 1200", "(1.3-5) 1258", "(1.3-6) 1280", "(1.3-7) 1320", "(1.3-8) 1360"},
            {"CUSTOM"}
    };

    private List<String> availableBands;
    private List<String> availableFrequencies;
    private String currentBand;
    private String currentFrequency;

    public AddFrequencySlot() {
        this.availableBands = new ArrayList<>();
        this.availableFrequencies = new ArrayList<>();
        this.currentBand = "SELECT";
        this.currentFrequency = "SELECT";
    }

    public AddFrequencySlot(boolean[] availableBands) {
        this.availableBands = new ArrayList<>();
        setAvailableBands(availableBands);
        this.availableFrequencies = new ArrayList<>();
        this.currentBand = "SELECT";
        this.currentFrequency = "SELECT";
    }

    public AddFrequencySlot(boolean[] availableBands, String currentBand, String currentFrequency) {
        this.availableBands = new ArrayList<>();
        setAvailableBands(availableBands);
        this.currentBand = currentBand;
        this.availableFrequencies = new ArrayList<>();
        setAvailableFrequencies();
        this.currentFrequency = currentFrequency;
    }

    public AddFrequencySlot(ArrayList<String> availableBands, ArrayList<String> availableFrequencies, String currentBand, String currentFrequency) {
        this.availableBands = availableBands;
        this.availableFrequencies = availableFrequencies;
        this.currentBand = currentBand;
        this.currentFrequency = currentFrequency;
    }

    // Setup available bands based on an array of booleans
    public void setAvailableBands(boolean[] availableBands) {
        this.availableBands.clear();
        for (int i = 0; i < availableBands.length; i++) {
            if (availableBands[i]) {
                switch(i) {
                    case 0:
                        this.availableBands.add("Fatshark");
                        break;
                    case 1:
                        this.availableBands.add("Boscam E");
                        break;
                    case 2:
                        this.availableBands.add("Boscam A");
                        break;
                    case 3:
                        this.availableBands.add("Raceband");
                        break;
                    case 4:
                        this.availableBands.add("Betaband");
                        break;
                    case 5:
                        this.availableBands.add("1.3");
                        break;
                    default:
                        break;
                }
            }
        }
        this.availableBands.add("CUSTOM");
    }

    public void setAvailableBands(ArrayList<String> availableBands) {
        this.availableBands = availableBands;
    }

    public void setAvailableFrequencies() {
        availableFrequencies.clear();
        switch (currentBand) {
            case "Fatshark":
                for (int i = 0; i < FREQ_MASTER[0].length; i++) {
                    availableFrequencies.add(FREQ_MASTER[0][i]);
                }
                break;
            case "Boscam E":
                for (int i = 0; i < FREQ_MASTER[1].length; i++) {
                    availableFrequencies.add(FREQ_MASTER[1][i]);
                }
                break;
            case "Boscam A":
                for (int i = 0; i < FREQ_MASTER[2].length; i++) {
                    availableFrequencies.add(FREQ_MASTER[2][i]);
                }
                break;
            case "Raceband":
                for (int i = 0; i < FREQ_MASTER[3].length; i++) {
                    availableFrequencies.add(FREQ_MASTER[3][i]);
                }
                break;
            case "Betaband":
                for (int i = 0; i < FREQ_MASTER[4].length; i++) {
                    availableFrequencies.add(FREQ_MASTER[4][i]);
                }
                break;
            case "1.3":
                for (int i = 0; i < FREQ_MASTER[5].length; i++) {
                    availableFrequencies.add(FREQ_MASTER[5][i]);
                }
                break;
            case "CUSTOM":
                availableFrequencies.add("CUSTOM");
                break;
            default:
                break;
        }
    }

    public void setAvailableFrequencies(ArrayList<String> availableFrequencies) {
        this.availableFrequencies = availableFrequencies;
    }

    public void setCurrentBand(String currentBand) {
        this.currentBand = currentBand;
    }

    public void setCurrentFrequency(String currentFrequency) {
        this.currentFrequency = currentFrequency;
    }

    public List<String> getAvailableBands() {
        return availableBands;
    }

    public List<String> getAvailableFrequencies() {
        return availableFrequencies;
    }

    public String getCurrentBand() {
        return currentBand;
    }

    public String getCurrentFrequency() {
        return currentFrequency;
    }

    public void resetFreqMaster() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.availableBands);
        dest.writeStringList(this.availableFrequencies);
        dest.writeString(this.currentBand);
        dest.writeString(this.currentFrequency);
    }

    protected AddFrequencySlot(Parcel in) {
        this.availableBands = in.createStringArrayList();
        this.availableFrequencies = in.createStringArrayList();
        this.currentBand = in.readString();
        this.currentFrequency = in.readString();
    }

    public static final Parcelable.Creator<AddFrequencySlot> CREATOR = new Parcelable.Creator<AddFrequencySlot>() {
        @Override
        public AddFrequencySlot createFromParcel(Parcel source) {
            return new AddFrequencySlot(source);
        }

        @Override
        public AddFrequencySlot[] newArray(int size) {
            return new AddFrequencySlot[size];
        }
    };
}
