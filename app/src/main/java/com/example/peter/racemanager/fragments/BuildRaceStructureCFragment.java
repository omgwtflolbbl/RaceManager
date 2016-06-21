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
import android.widget.ListView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.adapters.FrequencyBuilderAdapter;
import com.example.peter.racemanager.models.AddFrequencySlot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuildRaceStructureCFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuildRaceStructureCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildRaceStructureCFragment extends Fragment {
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

    // Other stuff
    private int numSlots;
    private List<AddFrequencySlot> freqSlotA;
    private List<AddFrequencySlot> freqSlotB;
    private List<AddFrequencySlot> freqSlotC;
    private List<AddFrequencySlot> freqSlotD;
    private List<AddFrequencySlot> freqSlotE;
    private List<AddFrequencySlot> freqSlotF;
    private List<AddFrequencySlot> freqSlotG;
    private List<AddFrequencySlot> freqSlotH;

    // UI stuff
    List<CardView> cards;
    List<ListView> listViews;
    List<FrequencyBuilderAdapter> adapters;

    private OnFragmentInteractionListener mListener;

    public BuildRaceStructureCFragment() {
        // Required empty public constructor
    }

    public static BuildRaceStructureCFragment newInstance(int numSlots, ArrayList<ArrayList<AddFrequencySlot>> slotFreqs) {
        BuildRaceStructureCFragment fragment = new BuildRaceStructureCFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_race_structure_c, container, false);


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
        listViews.add((ListView) view.findViewById(R.id.build_race_b_slot_a_listview));
        listViews.add((ListView) view.findViewById(R.id.build_race_b_slot_b_listview));
        listViews.add((ListView) view.findViewById(R.id.build_race_b_slot_c_listview));
        listViews.add((ListView) view.findViewById(R.id.build_race_b_slot_d_listview));
        listViews.add((ListView) view.findViewById(R.id.build_race_b_slot_e_listview));
        listViews.add((ListView) view.findViewById(R.id.build_race_b_slot_f_listview));
        listViews.add((ListView) view.findViewById(R.id.build_race_b_slot_g_listview));
        listViews.add((ListView) view.findViewById(R.id.build_race_b_slot_h_listview));

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
            Log.i("FRAGMENTC", "ABOUT TO SET FIRST ADAPTER");
            listViews.get(i).setAdapter(adapters.get(i));
            Log.i("FRAGMENTC", "JUST SET FIRST ADAPTER");
        }

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
        void onFragmentInteraction(Uri uri);
    }
}
