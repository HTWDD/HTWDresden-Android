package de.htwdd.htwdresden.classes;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Definition der Migrationen
 */
public class DatabaseMigrations implements RealmMigration {
    @Override
    public void migrate(@NonNull final DynamicRealm realm, long oldVersion, final long newVersion) {
        Log.d("Migration", "Alt: " + oldVersion + " Neu" + newVersion );
        // DynamicRealm exposes an editable schema
        final RealmSchema schema = realm.getSchema();

        if (oldVersion == 3) {
            schema.remove("Meal");
            schema.create("Meal")
                    .addField("studentPrice", Float.class)
                    .addField("employeePrice", Float.class)
                    .addField("title", String.class)
                    .addField("isSoldOut", boolean.class)
                    .addRealmListField("additives", String.class)
                    .addRealmListField("ingredients", String.class)
                    .addRealmListField("allergens", String.class)
                    .addField("date", Date.class)
                    .addField("detailURL", String.class)
                    .addField("image", String.class)
                    .addField("mensaId", short.class);
        }
    }
}
