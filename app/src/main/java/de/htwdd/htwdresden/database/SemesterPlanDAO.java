package de.htwdd.htwdresden.database;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.semesterplan.FreeDay;
import de.htwdd.htwdresden.classes.semesterplan.Period;
import de.htwdd.htwdresden.classes.semesterplan.SemesterPlan;

public class SemesterPlanDAO extends AbstractDAO<SemesterPlan> {
    public SemesterPlanDAO(SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }

    @Override
    protected String getTableName() {
        return Const.database.SemesterPlanTable.TABLE_NAME;
    }

    @Override
    public ArrayList<SemesterPlan> getAll() {
        return null;
    }

    public ArrayList<SemesterPlan> getByDS(final int week, final int day, final int ds) {
        ArrayList<SemesterPlan> lessons = new ArrayList<>();
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(
                getTableName(),
                new String[]{
                        Const.database.TimetableEntry._ID,
                        Const.database.TimetableEntry.COLUMN_NAME_NAME,
                        Const.database.TimetableEntry.COLUMN_NAME_LESSONTAG,
                        Const.database.TimetableEntry.COLUMN_NAME_TYP,
                        Const.database.TimetableEntry.COLUMN_NAME_ROOMS,
                        Const.database.TimetableEntry.COLUMN_NAME_WEEK,
                        Const.database.TimetableEntry.COLUMN_NAME_DAY,
                        Const.database.TimetableEntry.COLUMN_NAME_DS,
                        Const.database.TimetableEntry.COLUMN_NAME_WEEKSONLY,
                        Const.database.TimetableEntry.COLUMN_NAME_PROFESSOR},
                "(" + Const.database.TimetableEntry.COLUMN_NAME_WEEK + "= ? OR " + Const.database.TimetableEntry.COLUMN_NAME_WEEK + " = 0) AND " +
                        Const.database.TimetableEntry.COLUMN_NAME_DAY + " = ? AND " + Const.database.TimetableEntry.COLUMN_NAME_DS + " = ?",
                new String[]{String.valueOf(Const.Timetable.db_week(week)), String.valueOf(day), String.valueOf(ds)},
                null,
                null,
                null);


        SemesterPlan semesterPlan = new SemesterPlan();

        if (cursor.moveToFirst()) {

            ArrayList<FreeDay> freeDays = new ArrayList<>();
            Period period = new Period(cursor.getString(3), cursor.getString(4));
            Period lecturePeriod = new Period(cursor.getString(5), cursor.getString(6));
            Period examPeriod = new Period(cursor.getString(7), cursor.getString(8));
            Period regPeriod = new Period(cursor.getString(9), cursor.getString(10));

            do {

                FreeDay f = new FreeDay(
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11));
                freeDays.add(f);
                lessons.add(semesterPlan);
            } while (cursor.moveToNext());

        }
    /*
        1   2016    w   p1    FreeDay1
        1   2016    w   p1    FreeDay2
        1   2016    w   p1    FreeDay3

        if(ID == _lastID){
            FreeDay = new FreeDay(getString(freeDayName),getString(freeDayBegin),getString(freeDayEnd));
            free
        }
    */


        cursor.close();
        database.close();
        return lessons;
    }
}
