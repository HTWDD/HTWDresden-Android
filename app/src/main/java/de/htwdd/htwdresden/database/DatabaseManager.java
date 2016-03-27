package de.htwdd.htwdresden.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.htwdd.htwdresden.classes.Const;

/**
 * Zugriff auf die SQLiteDatenbank
 *
 * @author Kay Förster
 */
public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "HTWDresden.db";
    public static final int DATABASE_VERSION = 5;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // Aktiviere Fremdschlüsselüberprüfung
        if (!db.isReadOnly())
            db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + Const.database.TimetableEntry.TABLE_NAME + " (" +
                Const.database.TimetableEntry._ID + Const.database.TYPE_INT + " PRIMARY KEY" + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_LESSONTAG + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_NAME + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_TYP + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_WEEK + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_DAY + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_DS + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_PROFESSOR + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_WEEKSONLY + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.TimetableEntry.COLUMN_NAME_ROOMS + Const.database.TYPE_TEXT + ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + Const.database.RoomTimetableEntry.TABLE_NAME + " (" +
                Const.database.RoomTimetableEntry._ID + Const.database.TYPE_INT + " PRIMARY KEY" + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_LESSONTAG + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_NAME + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_TYP + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_WEEK + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_DAY + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_DS + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_PROFESSOR + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_WEEKSONLY + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.RoomTimetableEntry.COLUMN_NAME_ROOMS + Const.database.TYPE_TEXT + ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + Const.database.ExamResults.TABLE_NAME + " (" +
                Const.database.ExamResults.COLUMN_NAME_MODUL + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.ExamResults.COLUMN_NAME_NOTE + Const.database.TYPE_FLOAT + Const.database.COMMA_SEP +
                Const.database.ExamResults.COLUMN_NAME_VERMERK + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.ExamResults.COLUMN_NAME_STATUS + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.ExamResults.COLUMN_NAME_CREDITS + Const.database.TYPE_FLOAT + Const.database.COMMA_SEP +
                Const.database.ExamResults.COLUMN_NAME_VERSUCH + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.ExamResults.COLUMN_NAME_SEMESTER + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.ExamResults.COLUMN_NAME_KENNZEICHEN + Const.database.TYPE_TEXT + " )");
        sqLiteDatabase.execSQL("CREATE INDEX IndexSemester ON " + Const.database.ExamResults.TABLE_NAME + "(" + Const.database.ExamResults.COLUMN_NAME_SEMESTER + ");");
        sqLiteDatabase.execSQL("CREATE TABLE " + Const.database.SemesterPlanTable.TABLE_NAME + " (" +
                Const.database.SemesterPlanTable._ID + Const.database.TYPE_INT + " PRIMARY KEY" + Const.database.COMMA_SEP +//0
                Const.database.SemesterPlanTable.COLUMN_NAME_TYPE + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +//1
                Const.database.SemesterPlanTable.COLUMN_NAME_YEAR + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +//2
                Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +//3
                Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_END + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +//4
                Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +//5
                Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_END + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +//6
                Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +//7
                Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_END + Const.database.TYPE_TEXT + Const.database.COMMA_SEP + //8
                Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +//9
                Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_END + Const.database.TYPE_TEXT + ")");//10
        sqLiteDatabase.execSQL("CREATE TABLE " + Const.database.FreeDaysTable.TABLE_NAME + " (" +
                Const.database.FreeDaysTable._ID + Const.database.TYPE_INT + " PRIMARY KEY" + Const.database.COMMA_SEP +
                Const.database.FreeDaysTable.COLUMN_NAME_PARENT_ID + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.FreeDaysTable.COLUMN_NAME_BEZ + Const.database.TYPE_TEXT + Const.database.COMMA_SEP + //12
                Const.database.FreeDaysTable.COLUMN_NAME_FREE_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.FreeDaysTable.COLUMN_NAME_FREE_END + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                " FOREIGN KEY(" + Const.database.FreeDaysTable.COLUMN_NAME_PARENT_ID + ") REFERENCES " + Const.database.SemesterPlanTable.TABLE_NAME + "(" + Const.database.SemesterPlanTable._ID + ")" +")");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Const.database.TimetableEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Const.database.RoomTimetableEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Const.database.ExamResults.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Const.database.SemesterPlanTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Const.database.FreeDaysTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
