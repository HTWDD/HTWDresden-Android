package de.htwdd.htwdresden.types;

import android.support.annotation.Nullable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Repräsentiert einen Raum für {@link LessonUser}
 *
 * @author Kay Förster
 */
public class Room extends RealmObject {
    @PrimaryKey
    private String roomName;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(@Nullable final String roomName) {
        this.roomName = roomName;
    }
}
