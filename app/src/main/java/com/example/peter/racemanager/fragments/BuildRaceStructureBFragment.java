package com.example.peter.racemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peter.racemanager.ExpandableHeightListView;
import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.FrequencyBuilderAdapter;
import com.example.peter.racemanager.models.AddFrequencySlot;
import com.example.peter.racemanager.models.Race;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuildRaceStructureBFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuildRaceStructureBFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildRaceStructureBFragment extends Fragment {
    private static final String NUM_KEY = "NUM_KEY";
    private static final String A_KEY = "A_KEY";
    private static final String B_KEY = "B_KEY";
    private static final String C_KEY = "C_KEY";
    private static final String D_KEY = "D_KEY";
    private static final String E_KEY = "E_KEY";
    private static final String F_KEY = "F_KEY";
    private static final String G_KEY = "G_KEY";
    private static final String H_KEY = "H_KEY";
    private static final String BANDS_KEY = "BANDS_KEY";
    private static final String RACE_KEY = "RACE_KEY";

    // Other stuff
    private int numSlots;
    private List<List<AddFrequencySlot>> freqSlots;
    private List<AddFrequencySlot> freqSlotA;
    private List<AddFrequencySlot> freqSlotB;
    private List<AddFrequencySlot> freqSlotC;
    private List<AddFrequencySlot> freqSlotD;
    private List<AddFrequencySlot> freqSlotE;
    private List<AddFrequencySlot> freqSlotF;
    private List<AddFrequencySlot> freqSlotG;
    private List<AddFrequencySlot> freqSlotH;
    private boolean[] bands;
    private Race race;

    // UI stuff
    private List<CardView> cards;
    private List<ExpandableHeightListView> listViews;
    private List<FrequencyBuilderAdapter> adapters;
    private List<TextView> addButtons;

    private OnFragmentInteractionListener mListener;

    public BuildRaceStructureBFragment() {
        // Required empty public constructor
    }

    public static BuildRaceStructureBFragment newInstance(int numSlots, boolean[] bands, ArrayList<ArrayList<AddFrequencySlot>> slotFreqs, Race race) {
        BuildRaceStructureBFragment fragment = new BuildRaceStructureBFragment();
        Bundle args = new Bundle();
        args.putInt(NUM_KEY, numSlots);
        args.putParcelableArrayList(A_KEY, slotFreqs.get(0));
        args.putParcelableArrayList(B_KEY, slotFreqs.get(1));
        args.putParcelableArrayList(C_KEY, slotFreqs.get(2));
        args.putParcelableArrayList(D_KEY, slotFreqs.get(3));
        args.putParcelableArrayList(E_KEY, slotFreqs.get(4));
        args.putParcelableArrayList(F_KEY, slotFreqs.get(5));
        args.putParcelableArrayList(G_KEY, slotFreqs.get(6));
        args.putParcelableArrayList(H_KEY, slotFreqs.get(7));
        args.putBooleanArray(BANDS_KEY, bands);
        args.putParcelable(RACE_KEY, race);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numSlots = getArguments().getInt(NUM_KEY);
            freqSlotA = getArguments().getParcelableArrayList(A_KEY);
            freqSlotB = getArguments().getParcelableArrayList(B_KEY);
            freqSlotC = getArguments().getParcelableArrayList(C_KEY);
            freqSlotD = getArguments().getParcelableArrayList(D_KEY);
            freqSlotE = getArguments().getParcelableArrayList(E_KEY);
            freqSlotF = getArguments().getParcelableArrayList(F_KEY);
            freqSlotG = getArguments().getParcelableArrayList(G_KEY);
            freqSlotH = getArguments().getParcelableArrayList(H_KEY);
            freqSlots = new ArrayList<>();
            freqSlots.add(freqSlotA);
            freqSlots.add(freqSlotB);
            freqSlots.add(freqSlotC);
            freqSlots.add(freqSlotD);
            freqSlots.add(freqSlotE);
            freqSlots.add(freqSlotF);
            freqSlots.add(freqSlotG);
            freqSlots.add(freqSlotH);
            bands = getArguments().getBooleanArray(BANDS_KEY);
            race = getArguments().getParcelable(RACE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_race_structure_b, container, false);


        // Put all of the slot cards into one arraylist
        cards = new ArrayList<>();
        cards.add((CardView) view.findViewById(R.id.build_race_b_card_slot_a));
        cards.add((CardView) view.findViewById(R.id.build_race_b_card_slot_b));
        cards.add((CardView) view.findViewById(R.id.build_race_b_card_slot_c));
        cards.add((CardView) view.findViewById(R.id.build_race_b_card_slot_d));
        cards.add((CardView) view.findViewById(R.id.build_race_b_card_slot_e));
        cards.add((CardView) view.findViewById(R.id.build_race_b_card_slot_f));
        cards.add((CardView) view.findViewById(R.id.build_race_b_card_slot_g));
        cards.add((CardView) view.findViewById(R.id.build_race_b_card_slot_h));

        // Set visibility according to the number of allowed slots per heat
        for (int i = numSlots; i < cards.size(); i++) {
            cards.get(i).setVisibility(View.GONE);
        }

        // Put all of the listViews into one arraylist
        listViews = new ArrayList<>();
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_b_slot_a_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_b_slot_b_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_b_slot_c_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_b_slot_d_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_b_slot_e_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_b_slot_f_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_b_slot_g_listview));
        listViews.add((ExpandableHeightListView) view.findViewById(R.id.build_race_b_slot_h_listview));

        // Put all of the adapters into one arraylist
        adapters = new ArrayList<>();
        adapters.add(new FrequencyBuilderAdapter(getContext(), (ArrayList<AddFrequencySlot>) freqSlotA));
        adapters.add(new FrequencyBuilderAdapter(getContext(), (ArrayList<AddFrequencySlot>) freqSlotB));
        adapters.add(new FrequencyBuilderAdapter(getContext(), (ArrayList<AddFrequencySlot>) freqSlotC));
        adapters.add(new FrequencyBuilderAdapter(getContext(), (ArrayList<AddFrequencySlot>) freqSlotD));
        adapters.add(new FrequencyBuilderAdapter(getContext(), (ArrayList<AddFrequencySlot>) freqSlotE));
        adapters.add(new FrequencyBuilderAdapter(getContext(), (ArrayList<AddFrequencySlot>) freqSlotF));
        adapters.add(new FrequencyBuilderAdapter(getContext(), (ArrayList<AddFrequencySlot>) freqSlotG));
        adapters.add(new FrequencyBuilderAdapter(getContext(), (ArrayList<AddFrequencySlot>) freqSlotH));

        // Assign adapters
        for (int i = 0; i < listViews.size(); i++) {
            listViews.get(i).setAdapter(adapters.get(i));
            listViews.get(i).setExpanded(true);
        }

        // Make add buttons function for the adapaters
        addButtons = new ArrayList<>();
        addButtons.add((TextView) view.findViewById(R.id.build_race_b_slot_a_add_frequency));
        addButtons.add((TextView) view.findViewById(R.id.build_race_b_slot_b_add_frequency));
        addButtons.add((TextView) view.findViewById(R.id.build_race_b_slot_c_add_frequency));
        addButtons.add((TextView) view.findViewById(R.id.build_race_b_slot_d_add_frequency));
        addButtons.add((TextView) view.findViewById(R.id.build_race_b_slot_e_add_frequency));
        addButtons.add((TextView) view.findViewById(R.id.build_race_b_slot_f_add_frequency));
        addButtons.add((TextView) view.findViewById(R.id.build_race_b_slot_g_add_frequency));
        addButtons.add((TextView) view.findViewById(R.id.build_race_b_slot_h_add_frequency));

        for (int i = 0; i < addButtons.size(); i++) {
            final int index = i;
            addButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    freqSlots.get(index).add(new AddFrequencySlot(bands, "CUSTOM", "CUSTOM"));
                    adapters.get(index).notifyDataSetChanged();
                }
            });
        }


        TextView continueButton = (TextView) view.findViewById(R.id.build_race_b_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> chart = new ArrayList<>();
                for (int i = 0; i < cards.size(); i++) {
                    String freqString = "";
                    if (cards.get(i).getVisibility() == View.VISIBLE) {
                        for (int j = 0; j < adapters.get(i).getCount(); j++) {
                            adapters.get(i).notifyDataSetChanged();
                            freqString += ";" + adapters.get(i).getItem(j).getCurrentFrequency();
                        }
                    }
                    if (freqString.startsWith(";")) {
                        freqString = freqString.replaceFirst(";", "");
                    }
                    chart.add(freqString);
                }
                mListener.BuildRaceBToC(chart, race);
            }
        });

        TextView cancelButton = (TextView) view.findViewById(R.id.build_race_b_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        getArguments().putInt(NUM_KEY, numSlots);
        getArguments().putParcelableArrayList(A_KEY, (ArrayList<AddFrequencySlot>) freqSlotA);
        getArguments().putParcelableArrayList(B_KEY, (ArrayList<AddFrequencySlot>) freqSlotB);
        getArguments().putParcelableArrayList(C_KEY, (ArrayList<AddFrequencySlot>) freqSlotC);
        getArguments().putParcelableArrayList(D_KEY, (ArrayList<AddFrequencySlot>) freqSlotD);
        getArguments().putParcelableArrayList(E_KEY, (ArrayList<AddFrequencySlot>) freqSlotE);
        getArguments().putParcelableArrayList(F_KEY, (ArrayList<AddFrequencySlot>) freqSlotF);
        getArguments().putParcelableArrayList(G_KEY, (ArrayList<AddFrequencySlot>) freqSlotG);
        getArguments().putParcelableArrayList(H_KEY, (ArrayList<AddFrequencySlot>) freqSlotH);
        getArguments().putBooleanArray(BANDS_KEY, bands);
        getArguments().putParcelable(RACE_KEY, race);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void BuildRaceBToC(ArrayList<String> freq, Race race);
    }
}
