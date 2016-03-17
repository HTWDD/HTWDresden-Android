package de.htwdd.htwdresden.classes;

import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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

    public static final class IntentParams {
        public static final String START_WITH_FRAGMENT = "START_FRAGMENT";
    }

    public static final class preferencesKey {
        public static final String PREFERENCES_AUTO_MUTE = "autoMute";
        public static final String PREFERENCES_AUTO_MUTE_MODE = "autoMuteMode";
    }

    public static final class internet {
        public static final String WEBSERVICE_URL = "https://www2.htw-dresden.de/~app/API/";
        public static final String WEBSERVICE_URL_HISQIS = "https://wwwqis.htw-dresden.de/appservice/";
        public static final int HTTP_NOT_MODIFIED = 304;
        public static final int HTTP_UNAUTHORIZED = 401;
        public static final int HTTP_NOT_FOUND = 404;
        public static final int HTTP_DOWNLOAD_ERROR = 999;
        public static final int HTTP_TIMEOUT = 998;
        public static final int HTTP_NO_CONNECTION = 997;
        public static final int HTTP_NETWORK_ERROR = 996;
        public static final int HTTP_DOWNLOAD_OK = 200;
        public static final String TAG_ROOM_TIMETABLE = "ROOM_TIMETABLE";
        public static final String TAG_EXAM_RESULTS = "EXAM_RESULTS";
    }

    public static final class Semester {
        public static String getSemesterName(final @NonNull String[] semesterNames, final @NonNull Integer semester) {
            int semesterCalc = semester - 20000;
            if (semesterCalc % 2 == 1)
                return semesterNames[0] + " " + semesterCalc / 10;
            else
                return semesterNames[1] + " " + semesterCalc / 10 + " / " + ((semesterCalc / 10) + 1);
        }
    }

    public static final class Timetable {
        public static final Time[] beginDS = {
                Time.valueOf("07:30:00"),
                Time.valueOf("09:20:00"),
                Time.valueOf("11:10:00"),
                Time.valueOf("13:20:00"),
                Time.valueOf("15:10:00"),
                Time.valueOf("17:00:00"),
                Time.valueOf("18:40:00")};
        public static final Time[] endDS = {
                Time.valueOf("09:00:00"),
                Time.valueOf("10:50:00"),
                Time.valueOf("12:40:00"),
                Time.valueOf("14:50:00"),
                Time.valueOf("16:40:00"),
                Time.valueOf("18:30:00"),
                Time.valueOf("20:10:00")};

        public static int getMinutsBeginDS(int i){
            if(i>=beginDS.length || i<0 ) return 0;
            return (int)beginDS[i].getTime()/1000/60 + 60;
        }
        public static int getMinutsEndDS(int i){
            if(i>=endDS.length || i<0 ) return 0;
            return (int) endDS[i].getTime()/1000/60 + 60;
        }
        public static int db_week(final int calendarWeek) {
            return calendarWeek % 2 == 0 ? 2 : calendarWeek % 2;
        }

        /**
         * Liefert die DS zur übergebene Zeit
         *
         * @param currentTime aktuelle Zeit, in Minuten seit Mitternacht
         * @return Aktuelle Stunde oder 0 falls auserhalb der Unterrichtszeiten
         */
        public static int getCurrentDS(@Nullable Long currentTime) {
            final long offset = TimeZone.getDefault().getOffset(new GregorianCalendar().getTimeInMillis());
            if (currentTime == null) {
                currentTime = getMillisecondsWithoutDate(GregorianCalendar.getInstance());
            }

            if (currentTime >= endDS[6].getTime() + offset) {
                return 0;
            } else if (currentTime >= beginDS[6].getTime() + offset)
                return 7;
            else if (currentTime >= beginDS[5].getTime() + offset)
                return 6;
            else if (currentTime >= beginDS[4].getTime() + offset)
                return 5;
            else if (currentTime >= beginDS[3].getTime() + offset)
                return 4;
            else if (currentTime >= beginDS[2].getTime() + offset)
                return 3;
            else if (currentTime >= beginDS[1].getTime() + offset)
                return 2;
            else if (currentTime >= beginDS[0].getTime() + offset)
                return 1;

            return 0;
        }

        /**
         * Liefert die Millisekunden der übergebenen Uhrzeit
         *
         * @param calendar Uhrzeit welche umgewandelt werden soll
         * @return Millisekunden des übergebenen Datums
         */
        public static long getMillisecondsWithoutDate(Calendar calendar) {
            return TimeUnit.MILLISECONDS.convert(calendar.get(Calendar.HOUR_OF_DAY), TimeUnit.HOURS)
                    + TimeUnit.MILLISECONDS.convert(calendar.get(Calendar.MINUTE), TimeUnit.MINUTES);
        }
    }


    public static final class widget {
        /**
         * Gibt die Anzahl der Zellen für eine gegebene Widgetgröße
         *
         * @param size Widget größe in dp.
         * @return Anzahl der Zellen
         */
        public static int getCellsForSize(final int size) {
            return ((size - 30) / 70) + 1;
        }
    }

    public static final class database {
        public static final String TYPE_TEXT = " TEXT";
        public static final String TYPE_FLOAT = " REAL";
        public static final String TYPE_INT = " INTEGER";
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

        public static class ExamResults implements BaseColumns {
            public static final String COLUMN_NAME_MODUL = "modul";
            public static final String COLUMN_NAME_NOTE = "note";
            public static final String COLUMN_NAME_VERMERK = "vermerk";
            public static final String COLUMN_NAME_STATUS = "status";
            public static final String COLUMN_NAME_CREDITS = "credits";
            public static final String COLUMN_NAME_VERSUCH = "versuch";
            public static final String COLUMN_NAME_SEMESTER = "semester";
            public static final String COLUMN_NAME_KENNZEICHEN = "kennzeichen";
            public static final String TABLE_NAME = "ExamResults";
        }

        public static class SemesterPlanTable implements BaseColumns {
            public static final String TABLE_NAME = "SemesterPlan";
            public static final String COLUMN_NAME_TYPE = "type";
            public static final String COLUMN_NAME_YEAR = "year";
            public static final String COLUMN_NAME_PERIOD_BEGIN = "per_begin";
            public static final String COLUMN_NAME_PERIOD_END = "per_end";
            public static final String COLUMN_NAME_LECTURE_PERIOD_BEGIN = "lec_per_begin";
            public static final String COLUMN_NAME_LECTURE_PERIOD_END = "lec_per_end";
            public static final String COLUMN_NAME_EXAM_PERIOD_BEGIN = "ex_per_begin";
            public static final String COLUMN_NAME_EXAM_PERIOD_END = "ex_per_end";
            public static final String COLUMN_NAME_REG_PERIOD_BEGIN = "reg_per_begin";
            public static final String COLUMN_NAME_REG_PERIOD_END = "reg_per_end";
        }

        public static class FreeDaysTable implements BaseColumns {
            public static final String TABLE_NAME = "FreeDays";
            public static final String COLUMN_NAME_BEZ = "days_bez";
            public static final String COLUMN_NAME_FREE_BEGIN = "free_begin";
            public static final String COLUMN_NAME_FREE_END = "free_end";
            public static final String COLUMN_NAME_PARENT_ID = "parent_id";
        }

    }

    public static final class semesterPlanUpdater {
        public static final long UPDATE_INTERVAL = 3 * 7 * 24 * 60 * 60 * 1000;
        public static final String SEMESTERPLAN_URL_JSON = "https://www2.htw-dresden.de/~app/API/semesterplan.json";
    }
    public static final class VolumeController{
        public static final String PREFERENCES_AUTO_STUMMSCHALTEN="autoStummSchalten";
    }
}
