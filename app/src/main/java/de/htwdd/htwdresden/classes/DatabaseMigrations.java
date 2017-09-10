package de.htwdd.htwdresden.classes;

import android.support.annotation.NonNull;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Definition der Migrationen
 */
public class DatabaseMigrations implements RealmMigration {
    @Override
    public void migrate(@NonNull final DynamicRealm realm, long oldVersion, final long newVersion) {

    }
}
