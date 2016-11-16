package de.htwdd.htwdresden.interfaces;

import android.support.annotation.NonNull;

import io.realm.RealmModel;

/**
 * Interface zur Definition eines Wertes welches im Spinner angezeigt wird
 *
 * @author Kay Förster
 */
public interface ISpinnerName  extends RealmModel{
    @NonNull String getSpinnerSelect();
}
