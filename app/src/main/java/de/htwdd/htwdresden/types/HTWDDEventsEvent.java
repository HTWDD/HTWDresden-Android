package de.htwdd.htwdresden.types;

import android.media.Image;

import org.json.JSONObject;

/**
 * Created by Meralium on 20.03.16.
 */
public class HTWDDEventsEvent {

    private int eventID;
    private Image eventImage;
    private String eventOrganizer;
    private String eventName;
    private String eventDate;
    private String eventDateCreated;
    private String eventStatus;
    private String eventVisibility;
    private String eventPlace;
    private String eventDescribing;
    private String eventArt;   //braucht man das?
    private String eventFakul; //braucht man das?


    public HTWDDEventsEvent() {
    }

    public HTWDDEventsEvent(JSONObject eventJSON){
        //TODO initialisiere das ganze aus einer JSON-Datei
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void setEventImage(Image eventImage) {
        this.eventImage = eventImage;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public void setEventDescribing(String eventDescribing) {
        this.eventDescribing = eventDescribing;
    }

    public void setEventArt(String eventArt) {
        this.eventArt = eventArt;
    }

    public void setEventFakul(String eventFakul) {
        this.eventFakul = eventFakul;
    }

    public String getEventFakul() {
        return eventFakul;
    }

    public int getEventID() {
        return eventID;
    }

    public Image getEventImage() {
        return eventImage;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public String getEventDescribing() {
        return eventDescribing;
    }

    public String getEventArt() {
        return eventArt;
    }

}
