package de.htwdd.htwdresden.types;

import android.support.annotation.Nullable;

import io.realm.RealmObject;

/**
 * Repräsentiert einen Raum für {@link Lesson2}
 *
 * @author Kay Förster
 */
public class Room extends RealmObject {
    private String roomName;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(@Nullable final String roomName) {
        this.roomName = roomName;
    }
}
