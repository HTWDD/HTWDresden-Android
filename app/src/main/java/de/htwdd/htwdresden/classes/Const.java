package de.htwdd.htwdresden.classes;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class Const {
    public static final String NOTIFICATION_CHANNEL_EXAMS = "examResults";

    public static final class BundleParams {
        public static final String MENSA_DETAIL_MODE = "MENSA_DETAIL_MODE";
        public static final String CANTEEN_DETAIL_MODE = "CANTEEN_DETAIL_MODE";
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
        static final String PREFERENCES_AUTO_MUTE_MODE = "autoMuteMode";
        public static final String PREFERENCES_SEMESTERPLAN_UPDATETIME = "semesterPlanUpdateTime";
        public static final String PREFERENCES_MENSA_WEEK_LASTUPDATE = "mensaWeekLastUpdate";
        public static final String PREFERENCES_MENSA_NEXT_WEEK_LASTUPDATE = "mensaWeekLastUpdate";
        public static final String PREFERENCES_MENSA_DAY_LASTUPDATE = "mensaDayLastUpdate";
        public static final String PREFERENCES_STUDY_GROUP_LAST_UPDATE = "studyGroupsLastUpdate";
        public static final String PREFERENCES_AUTO_EXAM_UPDATE = "autoExamUpdate";
        public static final String PREFERENCES_TIMETABLE_STUDIENJAHR = "studyGroupYear";
        public static final String PREFERENCES_TIMETABLE_STUDIENGANG = "Stg";
        public static final String PREFERENCES_TIMETABLE_STUDIENGRUPPE = "StgGrp";
        public static final String PREFERENCES_TIMETABLE_PROFESSOR = "professorKey";
    }

    public static final class Timetable {
        public static final int TAG_VORLESUNG = 0;
        public static final int TAG_PRAKTIKUM = 1;
        public static final int TAG_UBUNG = 2;
        static final int TAG_OTHER = 3;

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
        public static class Canteen {
            public static final String MENSA_ID = "mensaId";
            public static final String MENSA_DATE = "date";
            public static final String MENSA_IS_SOLDOUT = "isSoldOut";
            public static final String MENSA_CATEGORY = "category";
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

        public static class SemesterPlan {
            public static final String SEMESTER_START = "period.beginDay";
            public static final String SEMESTER_END = "period.endDay";
        }

        public static class StudyGroups {
            public static final String STUDY_YEAR = "studyYear";
            public static final String STUDY_COURSE = "studyCourse";
            public static final String STUDY_GROUP = "studyGroup";
            public static final String STUDY_GROUP_COURSE = "studyCourses.studyCourse";
            public static final String STUDY_GROUP_COURSE_YEAR = "studyCourses.studyYears.studyYear";
        }
    }
}
