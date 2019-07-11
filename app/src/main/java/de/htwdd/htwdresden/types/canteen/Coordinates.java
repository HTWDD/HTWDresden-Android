package de.htwdd.htwdresden.types.canteen;

import io.realm.RealmObject;

/**
 * Beschreibung von Koordinaten
 *
 * @author Kay Förster
 */
public class Coordinates{

    private float longitude;
    private float latitude;

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }
}
