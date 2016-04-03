package de.htwdd.htwdresden.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.semesterplan.FreeDay;
import de.htwdd.htwdresden.types.semesterplan.Period;
import de.htwdd.htwdresden.types.semesterplan.SemesterPlan;

/**
 * Datenbankzugriff für Semesterpläne
 *
 * @author Vitali Drazdovich, Artyom Dyadechkin, Kay Förster
 */
public class SemesterPlanDAO extends AbstractDAO<SemesterPlan> {

    public SemesterPlanDAO(SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }

    @Override
    protected String getTableName() {
        return Const.database.SemesterPlanTable.TABLE_NAME;
    }

    @Override
    @NonNull
    public ArrayList<SemesterPlan> getAll() {
        return new ArrayList<>();
    }

    @Nullable
    public SemesterPlan getSemsterplan(final int year, @NonNull final String semesterBez) {
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(
                getTableName(),
                new String[]{
                        Const.database.SemesterPlanTable._ID,
                        Const.database.SemesterPlanTable.COLUMN_NAME_TYPE,
                        Const.database.SemesterPlanTable.COLUMN_NAME_YEAR,
                        Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_BEGIN,
                        Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_END,
                        Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_BEGIN,
                        Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_END,
                        Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_BEGIN,
                        Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_END,
                        Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_BEGIN,
                        Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_END
                },
                Const.database.SemesterPlanTable.COLUMN_NAME_YEAR + "= ? AND " + "UPPER (" + Const.database.SemesterPlanTable.COLUMN_NAME_TYPE + ")" + " = " + "UPPER(?)",
                new String[]{String.valueOf(year), semesterBez},
                null,
                null,
                null);

        if (!cursor.moveToFirst())
            return null;

        Period period = new Period(cursor.getString(3), cursor.getString(4));
        Period lecturePeriod = new Period(cursor.getString(5), cursor.getString(6));
        Period examPeriod = new Period(cursor.getString(7), cursor.getString(8));
        Period regPeriod = new Period(cursor.getString(9), cursor.getString(10));
        FreeDay freeDay[] = getFreeDays(cursor.getLong(0));
        SemesterPlan semesterPlan = new SemesterPlan(year, semesterBez, period, freeDay, lecturePeriod, examPeriod, regPeriod);

        cursor.close();
        database.close();
        return semesterPlan;
    }

    @NonNull
    private FreeDay[] getFreeDays(final long semID) {
        ArrayList<FreeDay> freeDays = new ArrayList<>();

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(
                Const.database.FreeDaysTable.TABLE_NAME,
                new String[]{
                        "trim(" + Const.database.FreeDaysTable.COLUMN_NAME_BEZ + ",'\n')",
                        Const.database.FreeDaysTable.COLUMN_NAME_FREE_BEGIN,
                        Const.database.FreeDaysTable.COLUMN_NAME_FREE_END,
                },
                Const.database.FreeDaysTable.COLUMN_NAME_PARENT_ID + "= ? ",
                new String[]{String.valueOf(semID)},
                null,
                null,
                null);

        while (cursor.moveToNext())
            freeDays.add(new FreeDay(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)));

        cursor.close();
        database.close();
        return freeDays.toArray(new FreeDay[freeDays.size()]);
    }

    /**
     * Speichert freie Tage zu einem Semesterplan
     *
     * @param freeDays       freite Tage welche gespeichert werden sollen
     * @param semesterPlanId ID des Semesterplans
     */
    private void addFreeDays(@NonNull final FreeDay[] freeDays, final long semesterPlanId) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        for (FreeDay freeDay : freeDays) {
            // ContentValues holen und um Fremdschlüssel ergänzen
            ContentValues contentValues = freeDay.getContentValues();
            contentValues.put(Const.database.FreeDaysTable.COLUMN_NAME_PARENT_ID, semesterPlanId);
            // Speichern
            database.insert(Const.database.FreeDaysTable.TABLE_NAME, null, contentValues);
        }
        database.close();
    }

    @Override
    public long save(@NonNull final SemesterPlan entity) {
        long semesterplanID = super.save(entity);

        // Speichere Freie Tage
        FreeDay[] freeDays = entity.getFreeDays();
        if (freeDays != null)
            addFreeDays(freeDays, semesterplanID);

        return semesterplanID;
    }

    /**
     * Löscht alle Einträge in der Tabelle
     */
    public void clearDatabase() {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.delete(Const.database.FreeDaysTable.TABLE_NAME, null, null);
        database.delete(getTableName(), null, null);
    }
}
