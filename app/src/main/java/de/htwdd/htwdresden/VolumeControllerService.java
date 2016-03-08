package de.htwdd.htwdresden;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Calendar;

import de.htwdd.htwdresden.classes.Const;

public class VolumeControllerService extends IntentService {
    static final String EXTRA_TIME = "de.htwdd.htwdresden.EXTRA_TIME";
    static final String PREFERENCE_FILE_VOLUME_CTRL = "de.htwdd.htwdresden.PREFERENCE_FILE_VOLUME_CTRL";
    static final String PREFERENCE_MODE = "de.htwdd.htwdresden.MODE";
    static final int PREFERENCE_MODE_DEFAULT_NORMAL = 0;
    static final int PREFERENCE_MODE_CHANGED_SILENT = 1;

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

    public void StartMultiAlarmVolumeController(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        // Loop counter `i` is used as a `requestCode`
        for (int i = 0; i < Const.Timetable.beginDS.length; i++) {
            //SETTING THE TURNOFF ALARMS UP
            Calendar calendar = VolumeControllerService.setCalendar(Const.Timetable.getMinutsBeginDS(i));
            //Log.e("START_VOL_CONTRL","TURN OFF AT "+calendar.get(Calendar.HOUR_OF_DAY)+"HOUR AND "+calendar.get(Calendar.MINUTE) + " MIN");
            Intent intent = getIntentSoundSwitch(context, "turnSoundOff", calendar);
            PendingIntent pendingIntent = PendingIntent.getService(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public void cancelMultiAlarmVolumeController(Context context) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        for (int i = 0; i < Const.Timetable.beginDS.length; i++) {
            Calendar calendar = VolumeControllerService.setCalendar(Const.Timetable.getMinutsBeginDS(i));
            Intent intent = getIntentSoundSwitch(context, "turnSoundOff", calendar);
            PendingIntent pendingIntent = PendingIntent.getService(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        for (int i = Const.Timetable.beginDS.length, l = 0; i < Const.Timetable.endDS.length + Const.Timetable.beginDS.length; i++, l++) {
            Calendar calendar = VolumeControllerService.setCalendar(Const.Timetable.getMinutsEndDS(l));
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

    static protected Calendar setCalendar(int timeInMinutes) {
        //Set Calender at time specified by timeInMinutes
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timeInMinutes / 60);
        calendar.set(Calendar.MINUTE, timeInMinutes % 60);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public void resetSettingsFile(Context context) {
        VolumeController volumeController = new VolumeController(context);
        //control bit set to default value (FROM 1:SILENT MODE TO -> 0: NORMAL MODE).
        volumeController.setVolumeChangedStatus(VolumeControllerService.PREFERENCE_MODE_DEFAULT_NORMAL);
    }

    public static class HtwddBootReceiver extends BroadcastReceiver {
        public HtwddBootReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                VolumeControllerService volumeControllerService = new VolumeControllerService();
                volumeControllerService.StartMultiAlarmVolumeController(context);
            }
        }
    }
}