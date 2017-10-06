package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.htwdd.htwdresden.service.TimetableRoomSyncService;
import de.htwdd.htwdresden.types.LessonRoom;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Stellt Hilfsmethoden für den Belegungsplan zur Verfügung
 *
 * @author Kay Förster
 */
public class TimetableRoomHelper extends AbstractTimetableHelper {

    public static boolean startSyncService(@NonNull final Context context) {
        context.startService(new Intent(context, TimetableRoomSyncService.class));
        return true;
    }

    @NonNull
    public static JSONObject convertTimetableJsonObject(@NonNull final JSONObject lesson) throws JSONException {
        final JSONObject jsonObject = AbstractTimetableHelper.convertTimetableJsonObject(lesson);

        if (jsonObject.has("studyGroups")) {
            final JSONArray jsonArray = jsonObject.getJSONArray("studyGroups");
            final int count = jsonArray.length();
            String room = "";

            for (int i = 0; i < count; i++) {
                room += jsonArray.getString(i) + "; ";
            }
            room = removeLastComma(room);
            jsonObject.put("studyGroups", room);
        }
        return jsonObject;
    }


    /**
     * Liefert eine List der Lehrveranstaltungen des übergebenen Tages und Ds für einen Raum
     *
     * @param realm             aktuelle Datenbankverbindung
     * @param calendar          Tag für welchen die Lehrveranstaltungen gelistet werden soll
     * @param room              Raum für welchen die Belegung angezeigt werden soll
     * @param ds                Zeit in welcher die Lehrveranstaltungen stattfinden sollen
     * @param filterCurrentWeek Nur Lehrveranstaltungen der aktuellen Kalenderwoche zurückgeben
     * @return Liste von passenden Lehrveranstaltungen
     */
    public static RealmResults<LessonRoom> getLessonsByDateAndDs(@NonNull final Realm realm, @NonNull final Calendar calendar, @NonNull final String room, final int ds,
                                                                 final boolean filterCurrentWeek) {
        final int dsIndex = ds > 0 ? ds - 1 : 0;
        final RealmQuery<LessonRoom> realmQuery = realm.where(LessonRoom.class)
                .equalTo(Const.database.LessonRoom.ROOM, room)
                .equalTo(Const.database.Lesson.DAY, calendar.get(Calendar.DAY_OF_WEEK) - 1)
                // Nach Kalenderwoche einschränken
                .beginGroup()
                .equalTo(Const.database.Lesson.WEEK, getWeekTyp(calendar.get(Calendar.WEEK_OF_YEAR)))
                .or().equalTo(Const.database.Lesson.WEEK, 0)
                .endGroup()
                // Vor dem Ende dieser DS beginnen und länger gehen als DS startet
                .lessThan(Const.database.Lesson.BEGIN_TIME, Const.Timetable.endDS[dsIndex])
                .greaterThan(Const.database.Lesson.END_TIME, Const.Timetable.beginDS[dsIndex]);

        if (filterCurrentWeek) {
            realmQuery.beginGroup()
                    .isEmpty(Const.database.Lesson.WEEKS_ONLY)
                    .or().equalTo(Const.database.Lesson.WEEKS_ONLY + ".weekOfYear", calendar.get(Calendar.WEEK_OF_YEAR))
                    .endGroup();
        }

        return realmQuery.findAll();
    }

    /**
     * Erstellt eine Liste von Lehrveranstaltungen eines Raumes
     *
     * @param context      aktueller App-Context
     * @param realm        aktuelle Datenbankverbindung
     * @param linearLayout {@link LinearLayout} in welchem die Liste eingefügt wird
     * @param day          Tag für welchen die Übersicht erstellt werden soll
     * @param current_ds   aktuelle DS welche hervorgehoben werden soll,sonst 0
     * @param room         Raum für welchen die Übersicht erstellt werden soll
     */
    public static void createSimpleLessonOverview(@NonNull final Context context, final Realm realm, @NonNull final LinearLayout linearLayout, final Calendar day,
                                                  final int current_ds, @NonNull final String room) {
        final int countUnits = Const.Timetable.beginDS.length;
        final List<RealmResults<LessonRoom>> realmResultsList = new ArrayList<>(countUnits);
        for (int i = 0; i < countUnits; i++) {
            realmResultsList.add(getLessonsByDateAndDs(realm, day, room, i + 1, true));
        }
        createSimpleLessonOverview(context, realmResultsList, linearLayout, current_ds);
    }
}
