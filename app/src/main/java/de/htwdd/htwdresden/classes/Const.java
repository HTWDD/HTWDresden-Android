package de.htwdd.htwdresden.classes;

import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.sql.Time;
import java.util.Date;
import java.util.GregorianCalendar;

public final class Const {

    public static final class BundleParams {
        public static final String MENSA_DETAIL_MODE = "MENSA_DETAIL_MODE";
        public static final String TIMETABLE_WEEK = "TIMETABLE_WEEK";
        public static final String TIMETABLE_DAY = "TIMETABLE_DAY";
        public static final String TIMETABLE_DS = "TIMETABLE_DS";
        public static final String TIMETABLE_LESSON_ID = "TIMETABLE_LESSON_ID";
        public static final String TIMETABLE_EDIT = "TIMETABLE_EDIT";
        public static final String TIMETABLE_CREATE = "TIMETABLE_CREATE";
        public static final String ROOM_TIMETABLE_ROOM = "ROOM_TIMETABLE_ROOM";
    }

    public static final class internet {
        public static final int HTTP_NOT_MODIFIED = 304;
        public static final int HTTP_UNAUTHORIZED = 401;
        public static final int HTTP_NOT_FOUND = 404;
        public static final int HTTP_DOWNLOAD_ERROR = 999;
        public static final int HTTP_TIMEOUT = 998;
        public static final int HTTP_NO_CONNECTION = 997;
        public static final int HTTP_NETWORK_ERROR = 996;
        public static final int HTTP_DOWNLOAD_OK = 200;
    }

    public static final class Timetable {
        public static final Time[] beginDS = {
                Time.valueOf("07:30:00"),
                Time.valueOf("09:20:00"),
                Time.valueOf("11:10:00"),
                Time.valueOf("13:10:00"),
                Time.valueOf("15:00:00"),
                Time.valueOf("16:50:00"),
                Time.valueOf("18:30:00")};
        public static final Time[] endDS = {
                Time.valueOf("09:00:00"),
                Time.valueOf("10:50:00"),
                Time.valueOf("12:40:00"),
                Time.valueOf("14:40:00"),
                Time.valueOf("16:30:00"),
                Time.valueOf("18:20:00"),
                Time.valueOf("20:00:00")};

        public static int db_week(final int calendarWeek) {
            return calendarWeek % 2 == 0 ? 2 : calendarWeek % 2;
        }

        /**
         * Liefert die DS zur übergebene Zeit
         *
         * @param currentTime aktuelle Zeit, in Minuten seit Mitternacht
         * @return Aktuelle Stunde oder 0 falls auserhalb der Unterrichtszeiten
         */
        public static int getCurrentDS(@Nullable Date currentTime) {
            if (currentTime == null)
                currentTime = GregorianCalendar.getInstance().getTime();

            if (currentTime.after(endDS[6]))
                return 0;
            else if (currentTime.after(beginDS[6]))
                return 7;
            else if (currentTime.after(beginDS[5]))
                return 6;
            else if (currentTime.after(beginDS[4]))
                return 5;
            else if (currentTime.after(beginDS[3]))
                return 4;
            else if (currentTime.after(beginDS[2]))
                return 3;
            else if (currentTime.after(beginDS[1]))
                return 2;
            else if (currentTime.after(beginDS[0]))
                return 1;

            return 0;
        }
    }

    public static final class database {
        public static final String TYPE_TEXT = " TEXT";
        public static final String TYPE_FLOAT = " REAL";
        public static final String TYPE_INT = " INTEGER";
        public static final String TYPE_TIME = " TIME";
        public static final String COMMA_SEP = ",";
        public static final long RESULT_DB_ERROR = -1;

        public static class TimetableEntry implements BaseColumns {
            public static final String COLUMN_NAME_LESSONTAG = "lessonTag";
            public static final String COLUMN_NAME_NAME = "name";
            public static final String COLUMN_NAME_TYP = "typ";
            public static final String COLUMN_NAME_WEEK = "week";
            public static final String COLUMN_NAME_DAY = "day";
            public static final String COLUMN_NAME_DS = "ds";
            public static final String COLUMN_NAME_PROFESSOR = "professor";
            public static final String COLUMN_NAME_WEEKSONLY = "WeeksOnly";
            public static final String COLUMN_NAME_ROOMS = "rooms";
            public static final String TABLE_NAME = "TimetableUser";
        }

        public static class RoomTimetableEntry extends TimetableEntry {
            public static final String TABLE_NAME = "TimetableRoom";
        }
    }
}
