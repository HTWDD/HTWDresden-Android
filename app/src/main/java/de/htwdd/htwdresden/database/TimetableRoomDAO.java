package de.htwdd.htwdresden.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.Lesson;
import de.htwdd.htwdresden.types.RoomTimetable;

/**
 * Datenbankzugriff für den Belegungsplan
 *
 * @author Kay Förster
 */
public class TimetableRoomDAO extends AbstractDAO<Lesson> {

    public TimetableRoomDAO(SQLiteOpenHelper sqLiteOpenHelper) {
        super(sqLiteOpenHelper);
    }

    @Override
    protected String getTableName() {
        return Const.database.RoomTimetableEntry.TABLE_NAME;
    }

    @Override
    public ArrayList<Lesson> getAll() {
        return null;
    }

    public boolean replaceTimetable(String room, ArrayList<Lesson> lessons) {
        long result;
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        database.delete(getTableName(), Const.database.RoomTimetableEntry.COLUMN_NAME_ROOMS + "= ?", new String[]{room});
        for (Lesson lesson : lessons) {
            // Ersetze Räume durch den aktuellen Raum
            lesson.setRooms(room);
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

    public ArrayList<RoomTimetable> getOverview(int day, int week) {
        ArrayList<RoomTimetable> roomTimetables = new ArrayList<>();

        // Datenbank öffnen
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

        // Hole alle Räume aus der DB
        Cursor cursor_rooms = sqLiteDatabase.query(
                getTableName(),
                new String[]{Const.database.RoomTimetableEntry.COLUMN_NAME_ROOMS},
                null,
                null,
                Const.database.RoomTimetableEntry.COLUMN_NAME_ROOMS,
                null,
                null);

        if (cursor_rooms.moveToFirst()) {
            do {
                RoomTimetable roomTimetable = new RoomTimetable();
                roomTimetable.roomName = cursor_rooms.getString(0);
                roomTimetable.day = day;
                roomTimetable.timetable = new ArrayList<>();

                Cursor cursor = sqLiteDatabase.query(
                        getTableName(),
                        new String[]{
                                Const.database.RoomTimetableEntry.COLUMN_NAME_LESSONTAG,
                                Const.database.RoomTimetableEntry.COLUMN_NAME_DS,
                                Const.database.RoomTimetableEntry.COLUMN_NAME_DAY,
                                Const.database.RoomTimetableEntry.COLUMN_NAME_TYP,
                                Const.database.RoomTimetableEntry.COLUMN_NAME_WEEKSONLY
                        },
                        Const.database.RoomTimetableEntry.COLUMN_NAME_ROOMS + " = ? AND " +

                                Const.database.RoomTimetableEntry.COLUMN_NAME_DAY + " = ? AND " +
                                "(" + Const.database.RoomTimetableEntry.COLUMN_NAME_WEEK + " = ? OR " + Const.database.RoomTimetableEntry.COLUMN_NAME_WEEK + " = 0)",
                        new String[]{
                                cursor_rooms.getString(0),
                                String.valueOf(day),
                                String.valueOf(Const.Timetable.db_week(week))},
                        null,
                        null,
                        Const.database.RoomTimetableEntry.COLUMN_NAME_DS + " DESC");

                if (cursor.moveToFirst()) {
                    do {
                        Lesson lesson = new Lesson();
                        lesson.setTag(cursor.getString(0));
                        lesson.setDs(cursor.getInt(1));
                        lesson.setDay(cursor.getInt(2));
                        lesson.setType(cursor.getString(3));
                        lesson.setWeeksOnly(cursor.getString(4));

                        // Zur Liste hinzufügen
                        roomTimetable.timetable.add(lesson);
                    }
                    while (cursor.moveToNext());
                }

                // Cursor für einzelne Stunden schliessen
                cursor.close();

                // Füge Raum zur Liste hinzu
                roomTimetables.add(roomTimetable);
            }
            while (cursor_rooms.moveToNext());
        }

        cursor_rooms.close();
        sqLiteDatabase.close();

        return roomTimetables;
    }

    public ArrayList<Lesson> getWeekShort(int week, @NonNull String room) {
        ArrayList<Lesson> lessons = new ArrayList<>();
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(
                getTableName(),
                new String[]{
                        Const.database.RoomTimetableEntry.COLUMN_NAME_DAY,
                        Const.database.RoomTimetableEntry.COLUMN_NAME_DS,
                        Const.database.RoomTimetableEntry.COLUMN_NAME_LESSONTAG,
                        Const.database.RoomTimetableEntry.COLUMN_NAME_TYP,
                        Const.database.RoomTimetableEntry.COLUMN_NAME_WEEKSONLY,
                        Const.database.RoomTimetableEntry.COLUMN_NAME_ROOMS},
                "(" + Const.database.RoomTimetableEntry.COLUMN_NAME_WEEK + "= ? OR " + Const.database.RoomTimetableEntry.COLUMN_NAME_WEEK + " = 0) AND " +
                Const.database.RoomTimetableEntry.COLUMN_NAME_ROOMS + "= ?",
                new String[]{String.valueOf(Const.Timetable.db_week(week)), room},
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

    public boolean deleteRoom(final String room) {
        // Datenbank öffnen
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        long result = sqLiteDatabase.delete(getTableName(), Const.database.RoomTimetableEntry.COLUMN_NAME_ROOMS + "= ?", new String[]{room});
        sqLiteDatabase.close();
        return result > 0;
    }
}
