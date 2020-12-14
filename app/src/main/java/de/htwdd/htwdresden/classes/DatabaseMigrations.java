package de.htwdd.htwdresden.classes;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Date;

import de.htwdd.htwdresden.types.canteen.Prices;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
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
                    .addRealmListField("information", String.class)
                    .addRealmListField("allergens", String.class)
                    .addField("date", Date.class)
                    .addField("detailURL", String.class)
                    .addField("image", String.class)
                    .addField("mensaId", short.class);
            oldVersion++;
        }

        if (oldVersion == 4) {
            final RealmObjectSchema lessonUserSchema = schema.get("LessonUser");
            if (lessonUserSchema != null) {
                // Room in primitiven Datentyp umwandeln
                lessonUserSchema.addRealmListField("rooms_tmp", String.class)
                        .transform(obj -> {
                            final RealmList<DynamicRealmObject> rooms = obj.getList("rooms");
                            final RealmList<String> migratedRooms = obj.getList("rooms_tmp", String.class);
                            for (final DynamicRealmObject week : rooms) {
                                migratedRooms.add(week.getString("roomName"));
                            }
                        })
                        .removeField("rooms")
                        .renameField("rooms_tmp", "rooms");
                schema.remove("Room");
            }
            final RealmObjectSchema lessonRoomSchema = schema.get("LessonRoom");
            if (lessonRoomSchema != null) {
                lessonRoomSchema.removeField("studyGroups");
                lessonRoomSchema.addRealmListField("studyGroups", String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 5) {
            schema.remove("Meal");
            schema.create("Meal")
                    .addField("prices", Prices.class)
                    .addField("category", String.class)
                    .addField("name", String.class)
                    .addRealmListField("notes", String.class)
                    .addField("date", Date.class)
                    .addField("id", long.class)
                    .addField("mensaId", short.class);

            schema.remove("Canteen");
            schema.create("Canteen")
                    .addField("id", int.class)
                    .addField("city", String.class)
                    .addField("name", String.class)
                    .addField("address", String.class)
                    .addField("isFav", Boolean.class)
                    .addRealmListField("coordinates", Float.class);

            schema.create("Prices")
                    .addField("students", Double.class)
                    .addField("employees", Double.class);
            oldVersion++;
        }

        if (oldVersion == 6) {
            schema.remove("Meal");
            schema.create("Meal")
                    .addField("prices", Prices.class)
                    .addField("category", String.class)
                    .addField("name", String.class)
                    .addRealmListField("notes", String.class)
                    .addField("date", Date.class)
                    .addField("id", long.class)
                    .addField("mensaId", short.class);

            schema.remove("Canteen");
            schema.create("Canteen")
                    .addField("id", int.class)
                    .addField("city", String.class)
                    .addField("name", String.class)
                    .addField("address", String.class)
                    .addField("isFav", Boolean.class)
                    .addRealmListField("coordinates", Float.class);

            schema.create("Prices")
                    .addField("students", Double.class)
                    .addField("employees", Double.class);
            oldVersion++;
        }

        if (oldVersion == 7) {
            schema.create("TimetableRealm")
                    .addField("id", String.class)
                    .addField("moduleId", String.class)
                    .addField("lessonTag", String.class)
                    .addField("name", String.class)
                    .addField("day", Long.class)
                    .addField("beginTime", Date.class)
                    .addField("endTime", Date.class)
                    .addField("week", Long.class)
                    .addRealmListField("weeksOnly", Long.class)
                    .addField("professor", String.class)
                    .addRealmListField("rooms", String.class)
                    .addField("lastChanged", String.class)
                    .addRealmListField("lessonDays", String.class);
            oldVersion++;
        }


        // weeksOnly in primitiven Datentyp umwandeln
        // TODO Wartet auf Umsetzung https://github.com/realm/realm-java/issues/5361
//        lessonUserSchema.addRealmListField("weeksOnly_tmp", Integer.class)
//                .transform(obj -> {
//                    final RealmList<DynamicRealmObject> weeksOnly = obj.getList("weeksOnly");
//                    final RealmList<Integer> migratedWeeksOnly = obj.getList("weeksOnly_tmp", Integer.class);
//                    for (final DynamicRealmObject week : weeksOnly) {
//                        migratedWeeksOnly.add(week.getInt("weekOfYear"));
//                    }
//                })
//                .removeField("children")
//                .renameField("weeksOnly_tmp", "weeksOnly");
//        lessonUserSchema.removeField("LessonWeek");
    }
}
