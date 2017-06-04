package de.htwdd.htwdresden.classes;

import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
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
        public static final String TIMETABLE_FILTER_CURRENT_WEEK = "TIMETABLE_FILTER_CURRENT_WEEK";
        public static final String TIMETABLE_FILTER_SHOW_HIDDEN = "TIMETABLE_FILTER_SHOW_HIDDEN";
        public static final String ROOM_TIMETABLE_ROOM = "ROOM_TIMETABLE_ROOM";
    }

    public static final class IntentParams {
        public static final String BROADCAST_ACTION = "de.htwdd.htwdresden.BROADCAST";
        public static final String BROADCAST_CODE = "statusCode";
        public static final String BROADCAST_MESSAGE = "message";
        public static final String BROADCAST_FINISH_TIMETABLE_UPDATE = "de.htwdd.htwdresden.timetableUpdate";
        public static final String START_ACTION_TIMETABLE = "de.htwdd.htwdresden.timetable";
        public static final String START_ACTION_MENSA = "de.htwdd.htwdresden.mensa";
        public static final String START_ACTION_EXAM_RESULTS = "de.htwdd.htwdresden.examResults";
    }

    public static final class preferencesKey {
        public static final String PREFERENCES_AUTO_MUTE = "autoMute";
        public static final String PREFERENCES_AUTO_MUTE_MODE = "autoMuteMode";
        public static final String PREFERENCES_SEMESTERPLAN_UPDATETIME = "semesterPlanUpdateTime";
        public static final String PREFERENCES_MENSA_WEEK_LASTUPDATE = "mensaWeekLastUpdate";
        public static final String PREFERENCES_AUTO_EXAM_UPDATE = "autoExamUpdate";
        public static final String PREFERENCES_TIMETABLE_STUDIENJAHR = "StgJhr";
        public static final String PREFERENCES_TIMETABLE_STUDIENGANG = "Stg";
        public static final String PREFERENCES_TIMETABLE_STUDIENGRUPPE = "StgGrp";
    }

    public static final class internet {
        public static final String WEBSERVICE_URL = "https://www2.htw-dresden.de/~app/API/";
        public static final String WEBSERVICE_URL_APP = "https://rubu2.rz.htw-dresden.de/API/";
        public static final String WEBSERVICE_URL_HISQIS = "https://wwwqis.htw-dresden.de/appservice/v2/";
        public static final String WEBSERVICE_URL_SEMESTERPLAN = WEBSERVICE_URL + "semesterplan.json";
        public static final int HTTP_UNAUTHORIZED = 401;
        public static final int HTTP_NOT_FOUND = 404;
        public static final int HTTP_DOWNLOAD_ERROR = 999;
        public static final int HTTP_TIMEOUT = 998;
        public static final int HTTP_NO_CONNECTION = 997;
        public static final int HTTP_NETWORK_ERROR = 996;
        public static final String TAG_EXAM_RESULTS = "EXAM_RESULTS";
        public static final String TAG_TIMETABLE = "TIMETABLE";
    }

    public static final class Semester {
        public static String getSemesterName(final @NonNull String[] semesterNames, final @NonNull Integer semester) {
            int semesterCalc = semester - 20000;
            if (semesterCalc % 2 == 1)
                return semesterNames[0] + " " + semesterCalc / 10;
            else
                return semesterNames[1] + " " + semesterCalc / 10 + " / " + ((semesterCalc / 10) + 1);
        }

        public static String getSemesterName(final @NonNull String[] semesterNames, final @NonNull String semester) {
            if (semester.equals("S"))
                return semesterNames[0];
            else return semesterNames[1];
        }
    }

    public static final class Timetable {
        public static final int TAG_VORLESUNG = 0;
        public static final int TAG_PRAKTIKUM = 1;
        public static final int TAG_UBUNG = 2;
        public static final int TAG_OTHER = 3;

        public static final int NO_LESSON_FOUND = 0;
        public static final int ONE_LESSON_FOUND = 1;
        public static final int MORE_LESSON_FOUND = 2;

        public static final int[] beginDS = {
                (int) TimeUnit.MINUTES.convert(7, TimeUnit.HOURS) + 30,
                (int) TimeUnit.MINUTES.convert(9, TimeUnit.HOURS) + 20,
                (int) TimeUnit.MINUTES.convert(11, TimeUnit.HOURS) + 10,
                (int) TimeUnit.MINUTES.convert(13, TimeUnit.HOURS) + 20,
                (int) TimeUnit.MINUTES.convert(15, TimeUnit.HOURS) + 10,
                (int) TimeUnit.MINUTES.convert(17, TimeUnit.HOURS),
                (int) TimeUnit.MINUTES.convert(18, TimeUnit.HOURS) + 40,
                (int) TimeUnit.MINUTES.convert(20, TimeUnit.HOURS) + 20};

        public static final int[] endDS = {
                (int) TimeUnit.MINUTES.convert(9, TimeUnit.HOURS),
                (int) TimeUnit.MINUTES.convert(10, TimeUnit.HOURS) + 50,
                (int) TimeUnit.MINUTES.convert(12, TimeUnit.HOURS) + 40,
                (int) TimeUnit.MINUTES.convert(14, TimeUnit.HOURS) + 50,
                (int) TimeUnit.MINUTES.convert(16, TimeUnit.HOURS) + 40,
                (int) TimeUnit.MINUTES.convert(18, TimeUnit.HOURS) + 30,
                (int) TimeUnit.MINUTES.convert(20, TimeUnit.HOURS) + 10,
                (int) TimeUnit.MINUTES.convert(21, TimeUnit.HOURS) + 50};

        public static int db_week(final int calendarWeek) {
            return calendarWeek % 2 == 0 ? 2 : calendarWeek % 2;
        }

        /**
         * Wandelt die übergebenen Minuten seit Mitternacht in ein {@link Date}-Objekt um
         *
         * @param minutesSinceMidnight Minuten seit Mitternacht
         * @return {@link Date}-Objekt mit der übergebenen Zeit
         */
        public static Date getDate(final long minutesSinceMidnight) {
            final long millis = TimeUnit.MILLISECONDS.convert(minutesSinceMidnight, TimeUnit.MINUTES);
            return new Date(millis - TimeZone.getDefault().getOffset(millis));
        }

        /**
         * Liefert die DS zur übergebenen / aktuellen Zeit
         *
         * @param currentTime aktuelle Zeit, in Minuten seit Mitternacht oder null für aktuelle Zeit
         * @return Aktuelle Stunde oder 0 falls außerhalb der Unterrichtszeiten
         */
        @Deprecated
        public static int getCurrentDS(@Nullable Long currentTime) {
            if (currentTime == null) {
                final Calendar calendar = GregorianCalendar.getInstance();
                currentTime = TimeUnit.MINUTES.convert(calendar.get(Calendar.HOUR_OF_DAY), TimeUnit.HOURS) + calendar.get(Calendar.MINUTE);
            }

            if (currentTime >= endDS[6])
                return 0;
            else if (currentTime >= beginDS[6])
                return 7;
            else if (currentTime >= beginDS[5])
                return 6;
            else if (currentTime >= beginDS[4])
                return 5;
            else if (currentTime >= beginDS[3])
                return 4;
            else if (currentTime >= beginDS[2])
                return 3;
            else if (currentTime >= beginDS[1])
                return 2;
            else if (currentTime >= beginDS[0])
                return 1;

            return 0;
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

        public static class ExamResults {
            public static final String id = "id";
            public static final String SEMESTER = "semester";
            static final String GRADE = "grade";
            static final String CREDITS = "credits";
        }

        public static class Lesson {
            public static final String ID = "id";
            static final String DAY = "day";
            static final String WEEK = "week";
            static final String END_TIME = "endTime";
            static final String BEGIN_TIME = "beginTime";
            static final String WEEKS_ONLY = "weeksOnly";
            static final String HIDE_LESSON = "hideLesson";
            public static final String CREATED_BY_USER = "createdByUser";
        }

        public static class LessonRoom {
            public static final String ROOM = "room";
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
}
