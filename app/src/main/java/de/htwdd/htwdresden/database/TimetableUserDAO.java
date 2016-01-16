package de.htwdd.htwdresden.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.Lesson;

/**
 * Datenbankzugriff für den User-Stundenplan
 *
 * @author Kay Förster
 */
public class TimetableUserDAO extends AbstractDAO<Lesson> {

    public TimetableUserDAO(SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }

    public boolean replaceTimetable(ArrayList<Lesson> lessons) {
        long result;
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        database.delete(getTableName(), null, null);
        for (Lesson lesson : lessons) {
            result = database.insert(getTableName(), null, lesson.getContentValues());

            if (result < 0) {
                database.endTransaction();
                database.close();
                return false;
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
        return true;
    }

    @Override
    protected String getTableName() {
        return Const.database.TimetableEntry.TABLE_NAME;
    }

    @Override
    public ArrayList<Lesson> getAll() {
        return null;
    }

    public ArrayList<Lesson> getWeekShort(int week) {
        ArrayList<Lesson> lessons = new ArrayList<>();
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(
                getTableName(),
                new String[]{
                        Const.database.TimetableEntry.COLUMN_NAME_DAY,
                        Const.database.TimetableEntry.COLUMN_NAME_DS,
                        Const.database.TimetableEntry.COLUMN_NAME_LESSONTAG,
                        Const.database.TimetableEntry.COLUMN_NAME_TYP,
                        Const.database.TimetableEntry.COLUMN_NAME_WEEKSONLY,
                        Const.database.TimetableEntry.COLUMN_NAME_ROOMS},
                Const.database.TimetableEntry.COLUMN_NAME_WEEK + "= ? OR " + Const.database.TimetableEntry.COLUMN_NAME_WEEK + " = 0",
                new String[]{String.valueOf(Const.Timetable.db_week(week))},
                null,
                null,
                null);

        if (cursor.moveToFirst())
            do {
                Lesson lesson = new Lesson();
                lesson.setDay(cursor.getInt(0));
                lesson.setDs(cursor.getInt(1));
                lesson.setTag(cursor.getString(2));
                lesson.setType(cursor.getString(3));
                lesson.setWeeksOnly(cursor.getString(4));
                lesson.setRooms(cursor.getString(5));
                lessons.add(lesson);
            } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return lessons;
    }
}
