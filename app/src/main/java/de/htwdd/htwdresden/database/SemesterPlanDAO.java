package de.htwdd.htwdresden.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.semesterplan.FreeDay;
import de.htwdd.htwdresden.types.semesterplan.Period;
import de.htwdd.htwdresden.types.semesterplan.SemesterPlan;

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
        return new ArrayList<SemesterPlan>();
    }

    public SemesterPlan getSemsterplan(final int year, final String semesterBez) {
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
                Const.database.SemesterPlanTable.COLUMN_NAME_YEAR + "= ? AND " +
                        "UPPER (" + Const.database.SemesterPlanTable.COLUMN_NAME_TYPE + ")" + " = " + "UPPER(?)",
                new String[]{String.valueOf(year), semesterBez},
                null,
                null,
                null);


        if (!cursor.moveToFirst()) return null;

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

    public FreeDay[] getFreeDays(final long semID) {
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(
                Const.database.FreeDaysTable.TABLE_NAME,                                                                                                                    // tablename
                new String[]{
                        "trim(" + Const.database.FreeDaysTable.COLUMN_NAME_BEZ + ",'\n')",
                        Const.database.FreeDaysTable.COLUMN_NAME_FREE_BEGIN,
                        Const.database.FreeDaysTable.COLUMN_NAME_FREE_END,
                },
                Const.database.FreeDaysTable.COLUMN_NAME_PARENT_ID + "= ? ",        //selection
                new String[]{String.valueOf(semID)},                                                                                    //selectionArgs
                null,                                                                                                                               //groupBy
                null,                                                                                                                               //having
                null);                                                                                                                              //orderBy

        ArrayList<FreeDay> freeDays = new ArrayList<>();

        if (!cursor.moveToFirst()) return freeDays.toArray(new FreeDay[freeDays.size()]);
        do {
            FreeDay f = new FreeDay(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
            freeDays.add(f);
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return freeDays.toArray(new FreeDay[freeDays.size()]);
    }

    public void addFreeDays(SemesterPlan semesterPlan, long fremdID) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        if (semesterPlan.getFreeDays() == null) return;
        for (FreeDay freeDay : semesterPlan.getFreeDays()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Const.database.FreeDaysTable.COLUMN_NAME_BEZ, freeDay.getName());
            contentValues.put(Const.database.FreeDaysTable.COLUMN_NAME_FREE_BEGIN, freeDay.getBeginDay());
            contentValues.put(Const.database.FreeDaysTable.COLUMN_NAME_FREE_END, freeDay.getEndDay());
            contentValues.put(Const.database.FreeDaysTable.COLUMN_NAME_PARENT_ID, fremdID);
            database.insert(Const.database.FreeDaysTable.TABLE_NAME, null, contentValues);
        }
        database.close();
    }

    public void addSemesterPlan(SemesterPlan semesterPlan) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_YEAR, semesterPlan.getYear());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_TYPE, semesterPlan.getType());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_BEGIN, semesterPlan.getLecturePeriod().getBeginDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_END, semesterPlan.getLecturePeriod().getEndDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_BEGIN, semesterPlan.getExamsPeriod().getBeginDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_END, semesterPlan.getExamsPeriod().getEndDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_BEGIN, semesterPlan.getPeriod().getBeginDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_END, semesterPlan.getPeriod().getEndDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_BEGIN, semesterPlan.getReregistration().getBeginDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_END, semesterPlan.getReregistration().getEndDay());
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

        long semesterID = database.insert(Const.database.SemesterPlanTable.TABLE_NAME, null, contentValues);
        addFreeDays(semesterPlan, semesterID);
        database.close();
    }

}
