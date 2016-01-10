package de.htwdd.htwdresden.database;

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
}
