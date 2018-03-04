package de.htwdd.htwdresden.adapter.typeadapter;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.types.LessonUser;

/**
 * Konvertiert {@link LessonUser} von der Json Repräsentation in das entsprechende Java Objekt
 *
 * @author Kay Förster
 */
public class LessonUserTypeAdapter implements JsonDeserializer<LessonUser> {
    private static JsonArray convertPrimitivTypToJsonObject(@NonNull final JsonArray array, @NonNull final String type) {
        final int count = array.size();
        final JsonArray result = new JsonArray();

        JsonObject jsonObject;
        for (int i = 0; i < count; i++) {
            jsonObject = new JsonObject();
            jsonObject.add(type, array.get(i));
            result.add(jsonObject);
        }

        return result;
    }

    @Override
    public LessonUser deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final JsonObject jsonObject = json.getAsJsonObject();

        // Zeit in Minuten seit Mitternacht umrechnen
        final long beginTime = Time.valueOf(jsonObject.get("beginTime").getAsString()).getTime();
        final long endTime = Time.valueOf(jsonObject.get("endTime").getAsString()).getTime();
        jsonObject.addProperty("beginTime", TimeUnit.MINUTES.convert(beginTime + calendar.getTimeZone().getOffset(beginTime), TimeUnit.MILLISECONDS));
        jsonObject.addProperty("endTime", TimeUnit.MINUTES.convert(endTime + calendar.getTimeZone().getOffset(endTime), TimeUnit.MILLISECONDS));
        // Datentyp umwandeln
        jsonObject.add("weeksOnly", convertPrimitivTypToJsonObject(jsonObject.getAsJsonArray("weeksOnly"), "weekOfYear"));

        return new Gson().fromJson(jsonObject, LessonUser.class);
    }
}
