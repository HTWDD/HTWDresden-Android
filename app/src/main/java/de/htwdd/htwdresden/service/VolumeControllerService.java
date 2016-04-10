package de.htwdd.htwdresden.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Calendar;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.VolumeController;


public class VolumeControllerService extends IntentService {
    static final String EXTRA_TIME = "de.htwdd.htwdresden.EXTRA_TIME";

    public VolumeControllerService() {
        super("volumeControllerService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        VolumeController volumeController = new VolumeController(getApplicationContext());

        if (intent.getStringExtra("Mode").equals("turnSoundOff"))
            volumeController.turnSoundOff();
        else if (intent.getStringExtra("Mode").equals("turnSoundOn"))
            volumeController.turnSoundOn();
    }

    /**
     * Startet AlarmManeger zu Begin einer jeden Lehrveranstaltung
     *
     * @param context aktueller App-Context
     */
    public void startMultiAlarmVolumeController(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        // Loop counter `i` is used as a `requestCode`
        for (int i = 0; i < Const.Timetable.beginDS.length; i++) {
            //SETTING THE TURNOFF ALARMS UP
            Calendar calendar = VolumeControllerService.setCalendar(Const.Timetable.beginDS[i]);
            Intent intent = getIntentSoundSwitch(context, "turnSoundOff", calendar);
            PendingIntent pendingIntent = PendingIntent.getService(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    /**
     * Beendet alle laufenden AlarmManager
     *
     * @param context aktueller App-Context
     */
    public void cancelMultiAlarmVolumeController(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        // Alle welche auf den Beginn einer Lehrveranstaltung warten
        for (int i = 0; i < Const.Timetable.beginDS.length; i++) {
            Calendar calendar = setCalendar(Const.Timetable.beginDS[i]);
            Intent intent = getIntentSoundSwitch(context, "turnSoundOff", calendar);
            PendingIntent pendingIntent = PendingIntent.getService(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        // Alle welche auf das Ende einer Lehrveranstaltung warten
        for (int i = Const.Timetable.beginDS.length, l = 0; i < Const.Timetable.endDS.length + Const.Timetable.beginDS.length; i++, l++) {
            Calendar calendar = setCalendar(Const.Timetable.endDS[l]);
            Intent intent = getIntentSoundSwitch(context, "turnSoundOn", calendar);
            PendingIntent pendingIntent = PendingIntent.getService(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    @NonNull
    public static Intent getIntentSoundSwitch(Context context, String turnOnOff, Calendar calendar) {
        Intent intent = new Intent(context, VolumeControllerService.class);
        intent.putExtra("Mode", turnOnOff);
        String currentTime = "Hour: " + calendar.get(Calendar.HOUR_OF_DAY) + " min: " + calendar.get(Calendar.MINUTE);
        intent.putExtra(VolumeControllerService.EXTRA_TIME, currentTime);
        return intent;
    }

    public static Calendar setCalendar(final int minutesSinceMidnight) {
        //Set Calender at time specified by timeInMinutes
        Calendar calendar = Const.Timetable.getCalendar(minutesSinceMidnight);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public void resetSettingsFile(Context context) {
        VolumeController volumeController = new VolumeController(context);
        //control bit set to default value (FROM 1:SILENT MODE TO -> 0: NORMAL MODE).
        volumeController.setVolumeChangedStatus(false);
    }

    public static class HtwddBootReceiver extends BroadcastReceiver {
        public HtwddBootReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                VolumeControllerService volumeControllerService = new VolumeControllerService();
                volumeControllerService.startMultiAlarmVolumeController(context);
            }
        }
    }
}
