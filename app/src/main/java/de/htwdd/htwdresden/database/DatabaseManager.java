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
    private static final String DATABASE_NAME = "HTWDresden.db";
    private static final int DATABASE_VERSION = 6;

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
        sqLiteDatabase.execSQL("CREATE TABLE " + Const.database.SemesterPlanTable.TABLE_NAME + " (" +
                Const.database.SemesterPlanTable._ID + Const.database.TYPE_INT + " PRIMARY KEY" + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_TYPE + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_YEAR + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_END + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_END + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_END + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_END + Const.database.TYPE_TEXT + ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + Const.database.FreeDaysTable.TABLE_NAME + " (" +
                Const.database.FreeDaysTable._ID + Const.database.TYPE_INT + " PRIMARY KEY" + Const.database.COMMA_SEP +
                Const.database.FreeDaysTable.COLUMN_NAME_PARENT_ID + Const.database.TYPE_INT + Const.database.COMMA_SEP +
                Const.database.FreeDaysTable.COLUMN_NAME_BEZ + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.FreeDaysTable.COLUMN_NAME_FREE_BEGIN + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                Const.database.FreeDaysTable.COLUMN_NAME_FREE_END + Const.database.TYPE_TEXT + Const.database.COMMA_SEP +
                " FOREIGN KEY(" + Const.database.FreeDaysTable.COLUMN_NAME_PARENT_ID + ") REFERENCES " + Const.database.SemesterPlanTable.TABLE_NAME + "(" + Const.database.SemesterPlanTable._ID + ")" +")");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Const.database.SemesterPlanTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Const.database.FreeDaysTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ExamResults");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TimetableUser");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TimetableRoom");
        onCreate(sqLiteDatabase);
    }
}
