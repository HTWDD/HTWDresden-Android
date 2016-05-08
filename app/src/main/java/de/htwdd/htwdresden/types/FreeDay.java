package de.htwdd.htwdresden.types;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IGetContentValues;

/**
 * Klasse zum speichern von freien Tagen
 *
 * @author Vitali Drazdovich, Artyom Dyadechkin
 */
public final class FreeDay extends Period implements IGetContentValues {
    private final String name;

    public FreeDay(@Nullable final String name, @NonNull final String beginDay, @NonNull final String endDay) {
        super(beginDay, endDay);
        this.name = name;
    }

    @Nullable
    public String getName() {
        return name;
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