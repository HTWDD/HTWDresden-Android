package de.htwdd.htwdresden.database;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.ExamResult;
import de.htwdd.htwdresden.types.ExamStats;

/**
 * Datenbankzugriff für die Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public class ExamResultDAO extends AbstractDAO<ExamResult> {

    private static final String LOG_TAG = "ExamResultDAO";

    public ExamResultDAO(SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }

    @Override
    protected String getTableName() {
        return Const.database.ExamResults.TABLE_NAME;
    }

    @Override
    public ArrayList<ExamResult> getAll() {
        ArrayList<ExamResult> examResults = new ArrayList<>();

        // Datenbank öffnen
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        // Datenbankabfrage
        Cursor cursor = sqLiteDatabase.query(
                getTableName(),
                new String[]{
                        Const.database.ExamResults.COLUMN_NAME_MODUL,
                        Const.database.ExamResults.COLUMN_NAME_NOTE,
                        Const.database.ExamResults.COLUMN_NAME_VERMERK,
                        Const.database.ExamResults.COLUMN_NAME_STATUS,
                        Const.database.ExamResults.COLUMN_NAME_CREDITS,
                        Const.database.ExamResults.COLUMN_NAME_VERSUCH,
                        Const.database.ExamResults.COLUMN_NAME_SEMESTER,
                        Const.database.ExamResults.COLUMN_NAME_KENNZEICHEN
                },
                null,
                null,
                null,
                null,
                Const.database.ExamResults.COLUMN_NAME_SEMESTER);

        while (cursor.moveToNext()) {
            ExamResult examResult = new ExamResult();
            examResult.modul = cursor.getString(cursor.getColumnIndex(Const.database.ExamResults.COLUMN_NAME_MODUL));
            examResult.note = cursor.getFloat(cursor.getColumnIndex(Const.database.ExamResults.COLUMN_NAME_NOTE));
            examResult.vermerk = cursor.getString(cursor.getColumnIndex(Const.database.ExamResults.COLUMN_NAME_VERMERK));
            examResult.status = cursor.getString(cursor.getColumnIndex(Const.database.ExamResults.COLUMN_NAME_STATUS));
            examResult.credits = cursor.getFloat(cursor.getColumnIndex(Const.database.ExamResults.COLUMN_NAME_CREDITS));
            examResult.versuch = cursor.getShort(cursor.getColumnIndex(Const.database.ExamResults.COLUMN_NAME_VERSUCH));
            examResult.semester = cursor.getInt(cursor.getColumnIndex(Const.database.ExamResults.COLUMN_NAME_SEMESTER));
            examResult.kennzeichen = cursor.getString(cursor.getColumnIndex(Const.database.ExamResults.COLUMN_NAME_KENNZEICHEN));

            examResults.add(examResult);
        }

        cursor.close();
        sqLiteDatabase.close();

        return examResults;
    }

    /**
     * Ersetzt alle gespeicherten Ergebnise mit den übergebenen
     *
     * @param examResults Ergebnise welche gespeichert werden sollen
     * @return true bei Erfolg, sonst false
     */
    public boolean replaceExamResults(@NonNull ArrayList<ExamResult> examResults) {
        long result;
        try (SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase()) {
            database.beginTransaction();
            database.delete(Const.database.ExamResults.TABLE_NAME, null, null);
            for (ExamResult examResult : examResults) {
                result = database.insert(Const.database.ExamResults.TABLE_NAME, null, examResult.getContentValues());

                if (result < 0) {
                    database.endTransaction();
                    database.close();
                    return false;
                }
            }
            database.setTransactionSuccessful();
            database.endTransaction();
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "[Fehler] beim Speichern in die Datenbank");
            Log.e(LOG_TAG, e.toString());
            return false;
        }
    }

    public ArrayList<ExamStats> getStats() {
        ArrayList<ExamStats> examStatses = new ArrayList<>();

        // Datenbank öffnen
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        // Datenbankabfrage
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " +
                Const.database.ExamResults.TABLE_NAME + "." + Const.database.ExamResults.COLUMN_NAME_SEMESTER + Const.database.COMMA_SEP +
                "MAX(" + Const.database.ExamResults.COLUMN_NAME_NOTE + ")" + Const.database.COMMA_SEP +
                "MIN(" + Const.database.ExamResults.COLUMN_NAME_NOTE + ")" + Const.database.COMMA_SEP +
                "AnzahlNoten" + Const.database.COMMA_SEP +
                "SUM(" + Const.database.ExamResults.COLUMN_NAME_CREDITS + ")" + Const.database.COMMA_SEP +
                "SUM(" + Const.database.ExamResults.COLUMN_NAME_NOTE + "*" + Const.database.ExamResults.COLUMN_NAME_CREDITS + ")" +
                " FROM " + Const.database.ExamResults.TABLE_NAME +
                " JOIN (" +
                "   SELECT " + Const.database.ExamResults.COLUMN_NAME_SEMESTER + ", COUNT(" + Const.database.ExamResults.COLUMN_NAME_CREDITS + ") AS AnzahlNoten" +
                "   FROM " + Const.database.ExamResults.TABLE_NAME + "" +
                "   WHERE Credits != 0.0 GROUP BY Semester) AS UA" +
                " ON UA." + Const.database.ExamResults.COLUMN_NAME_SEMESTER + " == " + Const.database.ExamResults.TABLE_NAME + "." + Const.database.ExamResults.COLUMN_NAME_SEMESTER +
                " WHERE " + Const.database.ExamResults.COLUMN_NAME_NOTE + " != 0" +
                " GROUP BY " + Const.database.ExamResults.TABLE_NAME + "." + Const.database.ExamResults.COLUMN_NAME_SEMESTER +
                " ORDER BY " + Const.database.ExamResults.TABLE_NAME + "." + Const.database.ExamResults.COLUMN_NAME_SEMESTER + " DESC", null);

        if (cursor.moveToFirst()) {
            ExamStats total = new ExamStats();
            total.gradeBest = 5.0f;
            total.gradeWorst = 1.0f;

            do {
                ExamStats examStats = new ExamStats();
                examStats.semester = cursor.getInt(0);
                examStats.gradeWorst = cursor.getFloat(1);
                examStats.gradeBest = cursor.getFloat(2);
                examStats.gradeCount = cursor.getInt(3);
                examStats.credits = cursor.getFloat(4);
                examStats.average = cursor.getFloat(5) / examStats.credits;

                total.gradeBest = Math.min(total.gradeBest, cursor.getFloat(2));
                total.gradeWorst = Math.max(total.gradeWorst, cursor.getFloat(1));
                total.credits += cursor.getFloat(4);
                total.gradeCount += cursor.getInt(3);
                total.average += cursor.getFloat(5);
                examStatses.add(examStats);

            } while (cursor.moveToNext());

            total.average = total.average / total.credits;

            examStatses.add(0, total);
        }

        // Datenbank schließen
        cursor.close();
        sqLiteOpenHelper.close();

        return examStatses;
    }

    /**
     * Liefert die Anzahl gespeicherter Prüfungsleistungen
     *
     * @return Anzahl gespeicherter Prüfungsleistungen
     */
    public long queryNumEntries() {
        long result;
        // Datenbank öffnen
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        result = DatabaseUtils.queryNumEntries(sqLiteDatabase, getTableName());
        sqLiteDatabase.close();
        return result;
    }
}