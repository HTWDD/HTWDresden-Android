package de.htwdd.htwdresden.interfaces;

import android.content.ContentValues;

/**
 * Interface für Entitäten die ihre Werte in ContentValues (Datenbank) speichern möchten
 * @author Kay Förster
 */
public interface IGetContentValues {
    ContentValues getContentValues();
}
