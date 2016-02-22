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