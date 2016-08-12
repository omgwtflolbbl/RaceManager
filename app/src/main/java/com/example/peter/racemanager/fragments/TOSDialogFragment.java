package com.example.peter.racemanager.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.peter.racemanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TOSDialogFragment extends DialogFragment {

    private final String tos = "Bacon ipsum dolor amet short ribs dolore excepteur, prosciutto shank in doner cillum culpa nisi reprehenderit. Landjaeger beef nisi, meatloaf ground round spare ribs sed. Sirloin cupidatat proident id andouille aute. Tenderloin deserunt qui, short loin picanha landjaeger turkey corned beef proident chuck ribeye in. Tongue in sed pig tri-tip, dolor flank jerky short ribs.\n" +
            "\n" +
            "Ham leberkas labore sausage cow irure. Strip steak sausage ad leberkas est, tenderloin bacon meatloaf laborum adipisicing. Enim pork chop eu, voluptate venison aute duis pariatur incididunt. Ea ham kielbasa, laborum sausage est aliquip mollit chuck shoulder beef.\n" +
            "\n" +
            "Salami pork loin pastrami, alcatra dolore corned beef kevin tempor. Dolor tempor shankle enim fugiat ground round kielbasa sint esse brisket quis bacon ham hock beef cupim. Proident nulla in, culpa cupidatat occaecat aliqua sirloin ham. Fugiat minim beef ribs deserunt, elit bacon capicola. Capicola excepteur ground round, mollit t-bone labore deserunt kielbasa in exercitation drumstick tenderloin pastrami. Boudin fatback short ribs velit non. Duis pariatur kevin dolor strip steak mollit shankle tri-tip aute labore.\n" +
            "\n" +
            "Bacon eu shank mollit. Ullamco eiusmod cupidatat dolore. Spare ribs deserunt adipisicing quis consequat do kielbasa rump id elit shankle. Cow cupim shank pariatur kevin, meatball kielbasa sint.\n" +
            "\n" +
            "Filet mignon tenderloin et t-bone ullamco adipisicing ea, tongue dolore. Sausage pork loin aute ut, dolor laboris kielbasa aliqua leberkas. Pork belly reprehenderit et t-bone ea ham hock. Chuck tenderloin short ribs, fugiat strip steak dolore ipsum consequat duis beef biltong elit capicola.\n" +
            "\n" +
            "Aliqua reprehenderit duis aliquip pariatur, excepteur laborum. Irure kevin short loin occaecat leberkas dolor tri-tip pork belly do pig andouille. Do dolore pork consequat. Veniam spare ribs ham, duis chicken pancetta deserunt pork eiusmod mollit non filet mignon bresaola ribeye. Tenderloin alcatra nulla irure. Nostrud chuck irure, id adipisicing fugiat pork chop ribeye minim boudin.\n" +
            "\n" +
            "Do frankfurter beef, tongue commodo eu tail cupidatat spare ribs. Fatback strip steak ribeye turducken aliqua. Exercitation brisket nisi turducken, beef ribs aliquip aliqua non ut officia anim bacon. Aute nulla bresaola, laboris commodo ipsum alcatra sirloin lorem voluptate drumstick tenderloin ut quis culpa. Deserunt exercitation swine consectetur nostrud corned beef fatback ullamco tri-tip pork loin ribeye. Tongue rump ex tri-tip, ad deserunt pork turducken consequat sed boudin venison beef ribs. Aute sirloin adipisicing venison, mollit tri-tip ex pastrami aliqua shank tongue sausage drumstick commodo elit.\n" +
            "\n" +
            "Lorem prosciutto tri-tip pork. Pork belly do fugiat dolor kevin minim pastrami. Alcatra excepteur doner deserunt sint, swine salami ut kielbasa nulla mollit do irure pork belly elit. Pancetta meatball nisi adipisicing, drumstick pork loin anim shankle prosciutto enim kevin irure tail brisket magna. Meatloaf eiusmod landjaeger chuck.\n" +
            "\n" +
            "Shoulder prosciutto tail, minim ham capicola pork meatloaf ut beef ribs pork chop aute. Jerky hamburger strip steak chuck, est doner pancetta. Est biltong esse aliquip, duis proident ex tri-tip sed deserunt ad brisket fatback frankfurter nisi. Meatball laboris adipisicing cillum beef. Dolor drumstick consequat shoulder enim laboris meatball nostrud kielbasa. In picanha pig lorem officia, hamburger ham hock esse elit consectetur nisi. Esse velit ex, boudin short ribs laborum sed sint.\n" +
            "\n" +
            "Aute ullamco in quis eu consequat, sausage turkey aliqua dolor deserunt sirloin brisket. Pork tail bresaola ipsum, id rump dolor. Officia doner labore deserunt quis et excepteur nostrud picanha. Exercitation pork chop lorem, spare ribs voluptate tongue fugiat ullamco laborum sed velit beef ex esse id. Eu voluptate fugiat ut, corned beef doner exercitation proident cupidatat elit prosciutto cillum chuck consectetur anim.";


    public TOSDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tos_dialog, container, false);

        // Remove title from dialog
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        TextView tosText = (TextView) view.findViewById(R.id.tos_text);
        tosText.setText(tos);
        tosText.setMovementMethod(new ScrollingMovementMethod());

        TextView tosOK = (TextView) view.findViewById(R.id.tos_ok);
        tosOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

}
