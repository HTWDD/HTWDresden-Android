package de.htwdd.htwdresden.types;

import java.util.ArrayList;

/**
 * Beschreibt einen Raum mit seinem Stundenplan
 *
 * @author Kay Förster
 */
public class RoomTimetable {
    public String roomName;
    public int day;
    public ArrayList<Lesson> timetable;
}
