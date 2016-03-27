package de.htwdd.htwdresden.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.service.VolumeControllerService;
import de.htwdd.htwdresden.types.Lesson;


public class VolumeController {
    public static final String PREFERENCE_FILE_VOLUME_CTRL = "de.htwdd.htwdresden.PREFERENCE_FILE_VOLUME_CTRL";
    private AudioManager amanager;
    private Context context;

    public VolumeController(Context context) {
        this.context = context;
        amanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Überprüft ob Lautstärke durch die App angepasst wurde
     *
     * @return true= wenn ja, sonst false
     */
    public boolean getVolumeChangedStatus() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_VOLUME_CTRL, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Const.preferencesKey.PREFERENCES_AUTO_MUTE_MODE, true);
    }

    /**
     * Speichert lokal den Status ob die Lautstärke durch die App angepasst wurde
     *
     * @param volumeMode Mouds ob Lautstärke angepasst wurde
     */
    public void setVolumeChangedStatus(final boolean volumeMode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_VOLUME_CTRL, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Const.preferencesKey.PREFERENCES_AUTO_MUTE_MODE, volumeMode);
        editor.apply();
    }

    /**
     * Wenn es aktuell eine Vorlesung gibt, wird das Handy stummgeschaltet
     */
    public void turnSoundOff() {
        // Stunde bestimmen
        Calendar calendar = GregorianCalendar.getInstance();
        int current_ds = Const.Timetable.getCurrentDS(Const.Timetable.getMillisecondsWithoutDate(calendar));

        // Aktuell Vorlesungszeit?, wenn nein return
        if (current_ds == 0 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return;

        // Lautstärke ist bereits ausgeschaltet
        if (amanager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            return;

        // Nach aktueller Veranstaltung suchen
        TimetableUserDAO dao = new TimetableUserDAO(new DatabaseManager(context));
        ArrayList<Lesson> lessons = dao.getByDS(calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.DAY_OF_WEEK) - 1, current_ds);

        // Gibt es aktuell eine Lehrveranstaltung?, wenn nein return
        if (lessons.size() == 0)
            return;

        // Überprüfe Lehrveranstaltungen genenuer
        if (LessonHelper.searchLesson(lessons, calendar.get(Calendar.WEEK_OF_YEAR)).getCode() == Const.Timetable.NO_LESSON_FOUND)
            return;

        // Setze Audio-Ausgabe auf Silent
        amanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        // Status-bit setzen
        setVolumeChangedStatus(true);

        //Log.i("turnSoundOff","trying to set turnoffAlarm new");
        //SETTING THE TURNON ALARM UP. Turn sound on after the class(+90min)
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar2 = VolumeControllerService.setCalendar(Const.Timetable.endDS[current_ds - 1]);
        Intent intent2 = VolumeControllerService.getIntentSoundSwitch(context, "turnSoundOn", calendar2);
        PendingIntent pendingIntent2 = PendingIntent.getService(context, current_ds - 1 + Const.Timetable.beginDS.length, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
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
}
