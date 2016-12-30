package de.htwdd.htwdresden.types;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IGetContentValues;
import de.htwdd.htwdresden.interfaces.IParseJSON;
import io.realm.RealmObject;

/**
 * Klasse für ein Prüfungsergebnis
 *
 * @author Kay Förster
 */
public class ExamResult extends RealmObject implements IGetContentValues, IParseJSON {
    public String modul;
    public Float note;
    public String vermerk;
    public String status;
    public Float credits;
    public Short versuch;
    public Integer semester;
    public String kennzeichen;

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.database.ExamResults.COLUMN_NAME_MODUL, modul);
        contentValues.put(Const.database.ExamResults.COLUMN_NAME_SEMESTER, semester);
        if (note != null)
            contentValues.put(Const.database.ExamResults.COLUMN_NAME_NOTE, note);
        else contentValues.putNull(Const.database.ExamResults.COLUMN_NAME_NOTE);
        if (vermerk != null)
            contentValues.put(Const.database.ExamResults.COLUMN_NAME_VERMERK, vermerk);
        else contentValues.putNull(Const.database.ExamResults.COLUMN_NAME_VERMERK);
        if (status != null)
            contentValues.put(Const.database.ExamResults.COLUMN_NAME_STATUS, status);
        else contentValues.putNull(Const.database.ExamResults.COLUMN_NAME_STATUS);
        if (credits != null)
            contentValues.put(Const.database.ExamResults.COLUMN_NAME_CREDITS, credits);
        else contentValues.putNull(Const.database.ExamResults.COLUMN_NAME_CREDITS);
        if (versuch != null)
            contentValues.put(Const.database.ExamResults.COLUMN_NAME_VERSUCH, versuch);
        else contentValues.putNull(Const.database.ExamResults.COLUMN_NAME_VERSUCH);
        if (kennzeichen != null)
            contentValues.put(Const.database.ExamResults.COLUMN_NAME_KENNZEICHEN, kennzeichen);
        else contentValues.putNull(Const.database.ExamResults.COLUMN_NAME_KENNZEICHEN);
        return contentValues;
    }

    @Override
    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        modul = jsonObject.getString("PrTxt");
        vermerk = jsonObject.getString("Vermerk");
        status = jsonObject.getString("Status");
        credits = Float.parseFloat(jsonObject.getString("EctsCredits"));
        versuch = Short.parseShort(jsonObject.getString("Versuch"));
        semester = jsonObject.getInt("Semester");
        kennzeichen = jsonObject.getString("PrForm");
        if (!jsonObject.getString("PrNote").equals(""))
            note = Float.parseFloat(jsonObject.getString("PrNote")) / 100;
        else note = 0f;
    }
}
