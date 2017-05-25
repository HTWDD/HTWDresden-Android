package de.htwdd.htwdresden.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.service.VolumeControllerService;
import de.htwdd.htwdresden.types.Lesson2;
import io.realm.Realm;
import io.realm.RealmResults;


public class VolumeController {
    private static final String PREFERENCE_FILE_VOLUME_CTRL = "de.htwdd.htwdresden.PREFERENCE_FILE_VOLUME_CTRL";
    private AudioManager amanager;
    private Context context;

    public VolumeController(Context context) {
        this.context = context;
        amanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Wenn es aktuell eine Vorlesung gibt, wird das Handy stummgeschaltet
     */
    public void turnSoundOff() {
        // Stunde bestimmen
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final int currentDs = TimetableHelper.getCurrentDS(TimetableHelper.getMinutesSinceMidnight(calendar));

        // Aktuell Vorlesungszeit?, wenn nein return
        if (currentDs <= 0 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return;

        // Lautstärke ist bereits ausgeschaltet
        if (amanager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            return;

        try (final Realm realm = Realm.getDefaultInstance()) {
            // Nach aktueller Veranstaltung suchen
            final RealmResults<Lesson2> lessons = TimetableHelper.getLessonsByDateAndDs(realm, calendar, currentDs, true, false);

            // Gibt es aktuell eine Lehrveranstaltung?, wenn nein return
            if (lessons.size() == 0)
                return;
        }

        // Setze Audio-Ausgabe auf Silent
        amanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        // Status-bit setzen
        setVolumeChangedStatus(true);

        //Log.i("turnSoundOff","trying to set turnoffAlarm new");
        //SETTING THE TURNON ALARM UP. Turn sound on after the class(+90min)
        final Calendar calendar2 = VolumeControllerService.setCalendar(Const.Timetable.endDS[currentDs - 1]);
        final Intent intent2 = VolumeControllerService.getIntentSoundSwitch(context, "turnSoundOn", calendar2);
        final PendingIntent pendingIntent2 = PendingIntent.getService(context, currentDs - 1 + Const.Timetable.beginDS.length, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent2);
    }

    /**
     * Schaltet das Handy wieder in den Normalen-Modus
     */
    public void turnSoundOn() {
        if (!getVolumeChangedStatus())
            return;

        // Stellt den normale Lautstärke wieder ein.
        amanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        //control bit set to default value (FROM 1:SILENT MODE TO -> 0: NORMAL MODE).
        setVolumeChangedStatus(false);
    }

    /**
     * Speichert lokal den Status ob die Lautstärke durch die App angepasst wurde
     *
     * @param volumeMode Modus ob Lautstärke angepasst wurde
     */
    public void setVolumeChangedStatus(final boolean volumeMode) {
        final SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_VOLUME_CTRL, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Const.preferencesKey.PREFERENCES_AUTO_MUTE_MODE, volumeMode);
        editor.apply();
    }

    /**
     * Überprüft ob Lautstärke durch die App angepasst wurde
     *
     * @return true= wenn ja, sonst false
     */
    private boolean getVolumeChangedStatus() {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_VOLUME_CTRL, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Const.preferencesKey.PREFERENCES_AUTO_MUTE_MODE, true);
    }
}
