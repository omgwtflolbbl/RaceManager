package com.example.peter.racemanager.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.peter.racemanager.FontManager;
import com.example.peter.racemanager.ListUtils;
import com.example.peter.racemanager.R;
import com.example.peter.racemanager.UnitUtils;
import com.example.peter.racemanager.adapters.RacerInfoListAdapter;
import com.example.peter.racemanager.models.Race;
import com.example.peter.racemanager.models.Racer;
import com.joanzapata.iconify.Iconify;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RaceInfoFragment.OnRaceInfoListener} interface
 * to handle interaction events.
 * Use the {@link RaceInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaceInfoFragment extends Fragment {
    private static final String RACE_KEY = "race_key";


    private OnRaceInfoListener mListener;

    // Data
    private Race race;
    private ArrayList<Racer> racers;

    // UI stuff
    private ImageView imageMap;
    private TextView iconDate;
    private TextView iconTime;
    private TextView iconLocation;
    private TextView viewTitle;
    private TextView viewDate;
    private TextView viewTime;
    private TextView viewLocation;
    private TextView viewBlockquote;
    private TextView viewDescription;
    private Button buttonToggle;
    private Button buttonJoin;
    private Button buttonResign;
    private ListView listRacers;
    private CardView cardView;
    private ScrollView scrollView;

    public RaceInfoFragment() {
        // Required empty public constructor
    }

    public static RaceInfoFragment newInstance(Race race) {
        RaceInfoFragment fragment = new RaceInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(RACE_KEY, race);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            race = getArguments().getParcelable(RACE_KEY);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_add_event).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_race_info, container, false);

        // TODO: Set up image of location
        String lat = "29.812784";
        String lng = "-95.704570";
        String mapUrl = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=600x200&sensor=false&markers=" + lat + "," + lng;
        imageMap = (ImageView) view.findViewById(R.id.race_info_map);
        Picasso.with(getContext())
                .load(mapUrl)
                .fit()
                .centerCrop()
                .into(imageMap);

        // Set up fontawesome for icons
        iconDate = (TextView) view.findViewById(R.id.race_info_icon_date);
        iconDate.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME));
        iconTime = (TextView) view.findViewById(R.id.race_info_icon_time);
        iconTime.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME));
        iconLocation = (TextView) view.findViewById(R.id.race_info_icon_location);
        iconLocation.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME));

        // Populate data
        viewTitle = (TextView) view.findViewById(R.id.race_info_title);
        viewTitle.setText(race.getTitle());

        viewDate = (TextView) view.findViewById(R.id.race_info_date);
        viewDate.setText(race.getDate());

        viewTime = (TextView) view.findViewById(R.id.race_info_time);
        viewTime.setText(race.getTime());

        viewLocation = (TextView) view.findViewById(R.id.race_info_location);
        //TODO: Fix this once data actually contains location
        viewLocation.setText("Someplace, Earth");

        viewBlockquote = (TextView) view.findViewById(R.id.race_info_blockquote);
        viewBlockquote.setText(race.getBlockquote());

        viewDescription = (TextView) view.findViewById(R.id.race_info_description);
        viewDescription.setText(race.getDescription());

        buttonToggle = (Button) view.findViewById(R.id.race_info_toggle_description);
        buttonToggle.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME));
        if (!(race.getDescription().equals("")) && !(race.getDescription().equals(""))) {
            buttonToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleDescription();
                }
            });
        }
        else {
            buttonToggle.setVisibility(View.GONE);
        }

        buttonJoin = (Button) view.findViewById(R.id.race_info_button_join);
        buttonJoin.setTransformationMethod(null);
        buttonJoin.setText(Iconify.compute(buttonJoin.getContext(), getResources().getString(R.string.button_join)));
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonJoin.animate()
                        .setStartDelay(0)
                        .translationX(buttonJoin.getWidth())
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                buttonJoin.setVisibility(View.GONE);
                                buttonJoin.setClickable(false);
                            }
                        });
                buttonResign.setVisibility(View.VISIBLE);
                buttonResign.animate()
                        .setStartDelay(300)
                        .translationX(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                buttonResign.setClickable(true);
                            }
                        });
            }
        });

        buttonResign = (Button) view.findViewById(R.id.race_info_button_resign);
        buttonResign.setTransformationMethod(null);
        buttonResign.setText(Iconify.compute(buttonResign.getContext(), getResources().getString(R.string.button_resign)));
        buttonResign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonResign.animate()
                        .setStartDelay(0)
                        .translationX(buttonResign.getWidth())
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                buttonResign.setVisibility(View.GONE);
                                buttonResign.setClickable(false);
                            }
                        });
                buttonJoin.setVisibility(View.VISIBLE);
                buttonJoin.animate()
                        .setStartDelay(300)
                        .translationX(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                buttonJoin.setClickable(true);
                            }
                        });
            }
        });

        setActionButtonVisibility();

        listRacers = (ListView) view.findViewById(R.id.race_info_racers);
        listRacers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.multigp.com" + ((Racer) listRacers.getItemAtPosition(i)).getRacerUrl()));
                startActivity(browserIntent);
            }
        });
        racers = new ArrayList<>();

        cardView = (CardView) view.findViewById(R.id.race_info_card_layout);

        scrollView = (ScrollView) view.findViewById(R.id.race_info_scroll);

        // Set up so that blockquote will show first, if possible
        toggleDescription();

        return view;
    }

    public void onButtonPressed(Race race) {
        if (mListener != null) {
            mListener.onRaceInfo(race);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRaceInfoListener) {
            mListener = (OnRaceInfoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRaceInfoListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        prepareRacerList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRaceInfoListener {
        void onRaceInfo(Race race);
    }

    public void toggleDescription() {
        listRacers.setFocusable(false);
        listRacers.setVisibility(View.GONE);
        listRacers.postDelayed(new Runnable() {
            @Override
            public void run() {
                listRacers.setVisibility(View.VISIBLE);
            }
        }, 500);
        if (viewBlockquote.getVisibility() == View.GONE && !(race.getBlockquote().equals(""))) {
            viewDescription.setVisibility(View.GONE);
            viewBlockquote.setVisibility(View.VISIBLE);
            buttonToggle.setText(R.string.fa_chevron_down);
            buttonToggle.setPadding(0,0,0,0);
        }
        else if (viewDescription.getVisibility() == View.GONE && !(race.getDescription().equals(""))) {
            viewBlockquote.setVisibility(View.GONE);
            viewDescription.setVisibility(View.VISIBLE);
            buttonToggle.setText(R.string.fa_chevron_up);
            buttonToggle.setPadding(0, 0, 0, UnitUtils.dpToPx(getContext(), 3));
        }
    }

    // Sets up the join/resign button depending on user status
    public void setActionButtonVisibility() {
        // Check if user has joined the race
        String username = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", "AnonymousSpectator159753");
        Boolean joined = false;
        for (int i = 0, size = race.getRacers().size(); i < size; i++) {
            if (race.getRacers().get(i).getUsername().equals(username)) {
                // Change button to resign race
                // First make button invisible, then actually do initial translation and remove it from view once button layout has been done
                buttonJoin.setVisibility(View.INVISIBLE);
                buttonJoin.post(new Runnable() {
                    @Override
                    public void run() {
                        buttonJoin.setTranslationX(buttonJoin.getWidth());
                        buttonJoin.setVisibility(View.GONE);
                    }
                });
                buttonResign.setVisibility(View.VISIBLE);
                joined = true;
            }
        }
        // If not, then change button to join race
        if (!joined) {
            buttonJoin.setVisibility(View.VISIBLE);
            buttonResign.setVisibility(View.INVISIBLE);
            buttonResign.post(new Runnable() {
                @Override
                public void run() {
                    buttonResign.setTranslationX(buttonResign.getWidth());
                    buttonResign.setVisibility(View.GONE);
                }
            });
        }
    }

    public void prepareRacerList() {
        racers.clear();
        racers.addAll(race.getRacers());
        RacerInfoListAdapter racersAdapter = new RacerInfoListAdapter(getContext(), racers);
        listRacers.setAdapter(racersAdapter);
        ListUtils.setDynamicHeight(listRacers);
    }

    public void updateBlockquote(String blockquote) {
        TextView blockquoteText = (TextView) getView().findViewById(R.id.race_info_blockquote);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            blockquoteText.setText(Html.fromHtml(blockquote, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE));
        } else {
            blockquoteText.setText(Html.fromHtml(blockquote));
        }
    }

    public void updateDescription(String description) {
        TextView descriptionText = (TextView) getView().findViewById(R.id.race_info_description);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            descriptionText.setText(description);
        } else {
            descriptionText.setText(description);
        }
    }
}
