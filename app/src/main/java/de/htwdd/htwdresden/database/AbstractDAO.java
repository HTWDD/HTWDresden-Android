package de.htwdd.htwdresden.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IGetContentValues;

/**
 * Abstrakte Basisklasse für den Zugriff auf die Datenbank
 *
 * @author Kay Förster
 */
public abstract class AbstractDAO<E extends IGetContentValues> {
    final SQLiteOpenHelper sqLiteOpenHelper;

    public AbstractDAO(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
    }

    /**
     * Speichern eines Datensatzes einer Entität. Nutzt die Methode {#getContentValues} um die Werte zu erhalten
     *
     * @param entity die Entität, deren Werte gespeichet werden sollen
     */
    public long save(E entity) {
        long result = Const.database.RESULT_DB_ERROR;
        if (entity != null) {
            ContentValues contentValues = entity.getContentValues();
            if (contentValues != null && contentValues.size() > 0) {
                SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
                result = database.insert(getTableName(), null, contentValues);
                database.close();
            }
        }
        return result;
    }

    /**
     * Methode welche den Namen der Datenbank zur entsprechenden Entität liefert.
     *
     * @return Datenbankname der Entität
     */
    abstract protected String getTableName();

    /**
     * Methode die alle Datensätze einer Entität lädt
     *
     * @return {@link List} mit allen verfügbaren Entitäten oder eine leere Liste
     */
    abstract public ArrayList<E> getAll();
}
