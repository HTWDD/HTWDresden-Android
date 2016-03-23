package de.htwdd.htwdresden;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.HTWDDEventsEvent;


public class HTWDDEventsEventCreator extends Fragment {

    View view;

    public HTWDDEventsEventCreator() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_htwddevents_event_creator, container, false);

        ((INavigation)getActivity()).setTitle("Event erstellen/ändern");

        Button saveButton = (Button)view.findViewById(R.id.htwddevents_event_creator_saveButton);

        if(saveButton != null){
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HTWDDEventsEvent event = new HTWDDEventsEvent();
                    event = setInfosFromViewsToEvent(view, event);

                }
            });
        }

        ImageView viewEventFotoIc = (ImageView)view.findViewById(R.id.htwddevents_event_creator_fotoic);

        viewEventFotoIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("OnFOTOicon", "YOU PUSHED ME!");
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.show(getFragmentManager(),"tag");
            }
        });



        //----------------für das Toolbar-Ausblenden aber halt überall
        //((MainActivity) getActivity()).getSupportActionBar().hide();
        //listener.hideToolbar();

        /*
        Auswahl Foto/Iamge von Galerie
        alles schon beim erstellen kontrollieren
        -new Instanz von Event;
        durch editView's alle Info's für den Event bekommen -> die Infos ins Event-Instanz rein schmeisen;
        eventInstanz mit dem Ersteller verknüpfen (UserID->eventOrganizer)
        send: url/eventCreate.php?sNummer=s72743&RZLogin="sdasDDas"&when="19.01.2222"&where="My room"
        falls SPEICHERN:
            eventInstanz an Server schreiben
            falls ALLESGUT:
                "Sie haben Ihr Event erfolgreich erstellt!" -> view alle meine Events
            falls FEHLER:
                die Fehler konkrett ansagen (z.B. wurde kein "Name" vom Event eingegeben)
        */
        return view;
    }

    private HTWDDEventsEvent setInfosFromViewsToEvent(View view, HTWDDEventsEvent event) {
        TextView viewEventName = (TextView)view.findViewById(R.id.htwddevents_event_creator_name);
        TextView viewEventDatum = (TextView)view.findViewById(R.id.htwddevents_event_creator_datum);
        TextView viewEventOrt = (TextView)view.findViewById(R.id.htwddevents_event_creator_ort);
        TextView viewEventArt = (TextView)view.findViewById(R.id.htwddevents_event_creator_art);
        TextView viewEventFakul = (TextView)view.findViewById(R.id.htwddevents_event_creator_fakul);
        TextView viewEventBeschreib = (TextView)view.findViewById(R.id.htwddevents_event_creator_beschreibung);
        //ImageView viewEventImg = (ImageView)view.findViewById(R.id.htwddevents_event_creator_img);

        event.setEventName(viewEventName.getText().toString());
        event.setEventDate(viewEventDatum.getText().toString());
        event.setEventPlace(viewEventOrt.getText().toString());
        event.setEventArt(viewEventArt.getText().toString());
        event.setEventFakul(viewEventFakul.getText().toString());
        event.setEventDescribing(viewEventBeschreib.getText().toString());
        Log.e("VIEWTEXT",event.getEventName());
        return event;
    }


    public class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String fotoChooser[]= {"Ein Foto machen","Aus der Galerie"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Wählen Sie ein Bild aus")
                    .setItems(fotoChooser, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                        }
                    });
            return builder.create();
        }
    }


    //----------------für das Toolbar-Ausblenden aber halt überall
/*
    private static OnEventListener listener;

    public interface OnEventListener {
        void hideToolbar() ;
    }

    public static void setOnEventListener(OnEventListener newListener) {
        listener = newListener;
    }
*/


}
