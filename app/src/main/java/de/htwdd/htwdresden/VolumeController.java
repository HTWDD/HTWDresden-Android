package de.htwdd.htwdresden;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.types.Lesson;


public class VolumeController {
    public static void main(String[] args) {
        for (int i = 0; i < Const.Timetable.beginDS.length; i++) {
            int time  = Const.Timetable.getMinutsBeginDS(i);
            int timee  = Const.Timetable.getMinutsEndDS(i);
            System.out.println(time+"\t"+timee);
        }
    }
    private AudioManager amanager;
    private Context context;

    public VolumeController(Context context) {
        this.context = context;
        amanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public int getVolumeChangedStatus() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                VolumeControllerService.PREFERENCE_FILE_VOLUME_CTRL,
                Context.MODE_PRIVATE
        );

        return sharedPref.getInt(
                VolumeControllerService.PREFERENCE_MODE,
                VolumeControllerService.PREFERENCE_MODE_CHANGED_SILENT
        );
    }

    public void setVolumeChangedStatus(final int volumeMode) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                VolumeControllerService.PREFERENCE_FILE_VOLUME_CTRL,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        //set control "bit" as false -> sound was turned off by our app.
        // We don't wont to  turn sound on , if it was not changed by us
        sharedPrefEditor.putInt(
                VolumeControllerService.PREFERENCE_MODE,
                volumeMode
        );
        sharedPrefEditor.apply();
    }

    /**
     * Wenn es aktuell eine Vorlesung gibt, wird das Handy stummgeschaltet
     */
    public void turnSoundOff() {
        // Stunde bestimmen
        Calendar calendar = GregorianCalendar.getInstance();
        int current_time = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int current_ds = Const.Timetable.getCurrentDS(null);
        //Log.i("INTENT TURN OFF","TURN OFF AT "+calendar.get(Calendar.HOUR_OF_DAY)+"HOUR AND "+calendar.get(Calendar.MINUTE) + " MIN");

        // Aktuell Vorlesungszeit?, wenn nein return
        if (current_ds == 0 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return;
        }

        // Stundenplan Anbindung
        DatabaseManager databaseManager = new DatabaseManager(context);
        TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);
        ArrayList<Lesson> lessons = timetableUserDAO.getByDS(week,
                calendar.get(Calendar.DAY_OF_WEEK) - 1,
                current_ds);

        // Gibt es aktuell eine Lehrveranstaltung?, wenn nein return
        if (lessons.size() == 0)
            return;

        int mode = amanager.getRingerMode();

        //if we don't changed the audio mode and it is in silent mode,
        //volumeStatus stays in Normal mode, so we don't turn it at the end of class
        if (mode == AudioManager.RINGER_MODE_SILENT)
            return;

        //Status-bit setzen
        setVolumeChangedStatus(VolumeControllerService.PREFERENCE_MODE_CHANGED_SILENT);

        // // Setze Audio-Ausgabe auf Silent, Setze Benachrichtungsmodus
        amanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        //Log.i("turnSoundOff","trying to set turnoffAlarm new");
        //SETTING THE TURNON ALARM UP. Turn sound on after the class(+90min)
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar2 = VolumeControllerService.setCalendar(Const.Timetable.getMinutsEndDS(current_ds - 1));
        Intent intent2 = VolumeControllerService.getIntentSoundSwitch(context, "turnSoundOn", calendar2);
        PendingIntent pendingIntent2 = PendingIntent.getService(context, current_ds-1 + Const.Timetable.beginDS.length, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent2);
    }

    /**
     * Schaltet das Handy wieder in den Normalen-Modus
     */
    public void turnSoundOn() {
        //Log.i("turnSoundOn","Entered");
        //LOAD, DID WE CHANGE VOLUME MODE OR NOT
        int mode = getVolumeChangedStatus();

        //if we didn't change the audio mode, we don't have to change it back to normal
        if (mode == VolumeControllerService.PREFERENCE_MODE_DEFAULT_NORMAL)
            return;

        //control bit set to default value (FROM 1:SILENT MODE TO -> 0: NORMAL MODE).
        setVolumeChangedStatus(VolumeControllerService.PREFERENCE_MODE_DEFAULT_NORMAL);

        // Stellt den normalen Benachrichtungsmodus wieder ein.
        amanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
}
