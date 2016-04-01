package de.htwdd.htwdresden.types.semesterplan;

import android.content.ContentValues;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IGetContentValues;

/**
 * Class to store FreeDays
 * Created by Vitali Drazdovich , Artyom Dyadechkin
 */
public final class FreeDay extends Period implements IGetContentValues{
    private String name;

    public String getName() {
        return name;
    }

    public String getBeginDay() {
        return super.getBeginDay();
    }

    public String getEndDay() {
        return super.getEndDay();
    }

    public FreeDay(String name, String beginDay, String endDay) {
        super(beginDay, endDay);
        this.name = name;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.database.FreeDaysTable.COLUMN_NAME_BEZ, name);
        contentValues.put(Const.database.FreeDaysTable.COLUMN_NAME_FREE_BEGIN, getBeginDay());
        contentValues.put(Const.database.FreeDaysTable.COLUMN_NAME_FREE_END, getEndDay());
        return contentValues;
    }
}