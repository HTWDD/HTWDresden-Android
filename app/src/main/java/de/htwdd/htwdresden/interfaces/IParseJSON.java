package de.htwdd.htwdresden.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Definiert Funktionen zum parsen von Objekten
 *
 * @author Kay Förster
 */
public interface IParseJSON {
    void parseFromJSON(JSONObject jsonObject) throws JSONException;
}
