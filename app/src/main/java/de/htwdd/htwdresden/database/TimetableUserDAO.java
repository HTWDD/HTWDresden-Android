package de.htwdd.htwdresden.database;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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

    public long countDS(final int week, final int day, final int ds) {
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(
                database,
                getTableName(),
                "(" + Const.database.TimetableEntry.COLUMN_NAME_WEEK + "= ? OR " + Const.database.TimetableEntry.COLUMN_NAME_WEEK + " = 0) AND " +
                        Const.database.TimetableEntry.COLUMN_NAME_DAY + "= ? AND " +
                        Const.database.TimetableEntry.COLUMN_NAME_DS + "= ?",
                new String[]{String.valueOf(Const.Timetable.db_week(week)), String.valueOf(day), String.valueOf(ds)}
        );
        database.close();
        return count;
    }

    /**
     * Liefert die Veranstaltungen anhand der übergebene Parameter
     * @param week Woche im Kalenderjahr
     * @param day Tag in der Woche
     * @param ds Stunde der Verastaltung
     * @return Liste aller Veranstaltungen
     */
    public ArrayList<Lesson> getByDS(final int week, final int day, final int ds) {
        ArrayList<Lesson> lessons = new ArrayList<>();
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

        if (cursor.moveToFirst())
            do {
                Lesson lesson = new Lesson();
                lesson.setId(cursor.getLong(0));
                lesson.setName(cursor.getString(1));
                lesson.setTag(cursor.getString(2));
                lesson.setType(cursor.getString(3));
                lesson.setRooms(cursor.getString(4));
                lesson.setWeek(cursor.getInt(5));
                lesson.setDay(cursor.getInt(6));
                lesson.setDs(cursor.getInt(7));
                lesson.setWeeksOnly(cursor.getString(8));
                lesson.setProfessor(cursor.getString(9));
                lessons.add(lesson);
            } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return lessons;
    }

    public Lesson getByID(final long id) {
        Lesson lesson = null;
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
                Const.database.TimetableEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            lesson = new Lesson();
            lesson.setId(cursor.getLong(0));
            lesson.setName(cursor.getString(1));
            lesson.setTag(cursor.getString(2));
            lesson.setType(cursor.getString(3));
            lesson.setRooms(cursor.getString(4));
            lesson.setWeek(cursor.getInt(5));
            lesson.setDay(cursor.getInt(6));
            lesson.setDs(cursor.getInt(7));
            lesson.setWeeksOnly(cursor.getString(8));
            lesson.setProfessor(cursor.getString(9));
        }

        cursor.close();
        database.close();
        return lesson;
    }

    /**
     * Aktuallisiert die übergebene Stunde anhand der Methode {@see Lesson#getId} in der Datenbank
     *
     * @param lesson welche aktuallisiert werden soll.
     * @return Anzahl der geänderten Entitys
     */
    public long update(final Lesson lesson) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        long result = database.update(getTableName(), lesson.getContentValues(), BaseColumns._ID + " = ?", new String[]{String.valueOf(lesson.getId())});
        database.close();
        return result;
    }
}
