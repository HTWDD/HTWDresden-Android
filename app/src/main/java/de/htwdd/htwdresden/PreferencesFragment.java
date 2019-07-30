package de.htwdd.htwdresden;


import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.service.ExamAutoUpdateService;
import de.htwdd.htwdresden.service.TimetableProfessorSyncService;
import de.htwdd.htwdresden.service.VolumeControllerService;

/**
 * Fragment für die Einstellungen
 *
 * @author Kay Förster
 */
public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String LOG_TAG = "PreferencesFragment";
    private final static int PERMISSIONS_REQUEST_NOTIFICATION_SERVICE = 1;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.white));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final Context context = getActivity();

        switch (key) {
            // Automatisches Muten bei Lehrveranstaltungen
            case Const.preferencesKey.PREFERENCES_AUTO_MUTE:
                // Aber Android 7.0 muss nach den access notification policy gefragt werden
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (mNotificationManager == null) {
                        Log.d(LOG_TAG, "NotificationManager is null");
                        return;
                    } else if (!mNotificationManager.isNotificationPolicyAccessGranted() && sharedPreferences.getBoolean(key, false)) {
                        Log.d(LOG_TAG, "Fehlende Berechtigung anfordern");
                        sharedPreferences.edit().putBoolean(key, false).apply();
                        ((CheckBoxPreference) findPreference(key)).setChecked(false);

                        startActivityForResult(new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), PERMISSIONS_REQUEST_NOTIFICATION_SERVICE);
                        return;
                    }
                }
                manageVolumeService();
                break;
            // Automatische Aktualisierung der Noten
            case Const.preferencesKey.PREFERENCES_AUTO_EXAM_UPDATE:
                final long updateInterval = ExamsHelper.getUpdateInterval(sharedPreferences.getString(key, "0"));
                if (updateInterval == 0) {
                    ExamAutoUpdateService.cancelAutoUpdate(context);
                } else {
                    ExamAutoUpdateService.startAutoUpdate(context, updateInterval);
                }
                break;
            case Const.preferencesKey.PREFERENCES_TIMETABLE_PROFESSOR:
                // Stundenplan aktualisieren
                context.startService(new Intent(context, TimetableProfessorSyncService.class));
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_NOTIFICATION_SERVICE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    final Context context = getActivity();
                    final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    // Überprüfe ob Berechtigung gesetzt wurde
                    final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (mNotificationManager == null) {
                        Log.d(LOG_TAG, "NotificationManager is null");
                        return;
                    } else if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                        manageVolumeService();
                        editor.putBoolean(Const.preferencesKey.PREFERENCES_AUTO_MUTE, true);
                        ((CheckBoxPreference) findPreference(Const.preferencesKey.PREFERENCES_AUTO_MUTE)).setChecked(true);

                        Log.d(LOG_TAG, "onActivityResult - Berechtigung erhalten");
                    } else {
                        Log.d(LOG_TAG, "onActivityResult - keine Berechtigung");
                        editor.putBoolean(Const.preferencesKey.PREFERENCES_AUTO_MUTE, false);
                    }
                    editor.apply();
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Service zum automatischen Muten des Gerätes starten oder beenden
     */
    private void manageVolumeService() {
        final Context context = getActivity();
        final PackageManager packageManager = context.getPackageManager();
        final VolumeControllerService volumeControllerService = new VolumeControllerService();
        final ComponentName receiver = new ComponentName(context, VolumeControllerService.HtwddBootReceiver.class);

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Const.preferencesKey.PREFERENCES_AUTO_MUTE, false)) {
            // Starte Background Service
            Log.d(LOG_TAG, "Starte Background Service");
            volumeControllerService.startMultiAlarmVolumeController(context);

            //enable a receiver -> starts alarms on reboot
            packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            // Beende Background Service
            Log.d(LOG_TAG, "Beende Service");
            volumeControllerService.cancelMultiAlarmVolumeController(context);
            volumeControllerService.resetSettingsFile(context);

            //disable a receiver, alarms will not be set on reboot
            packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }
}
