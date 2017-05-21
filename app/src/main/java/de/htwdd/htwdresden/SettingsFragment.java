package de.htwdd.htwdresden;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.service.ExamAutoUpdateService;
import de.htwdd.htwdresden.service.VolumeControllerService;

/**
 * Fragment für die Einstellungen
 *
 * @author Kay Förster
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static int PERMISSIONS_REQUEST_NOTIFICATION_SERVICE = 1;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Setze Title in Toolbar
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_settings));

        final View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                    final Activity activity = getActivity();
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {
                        // Kurze Erklärung anzeigen
                        Toast.makeText(context, R.string.settings_audio_request_permission, Toast.LENGTH_SHORT).show();
                    }
                    // Berechtigung anfragen
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, PERMISSIONS_REQUEST_NOTIFICATION_SERVICE);
                    return;
                }
                manageVolumeService();
                break;
            // Automatische Aktualisierung der Noten
            case Const.preferencesKey.PREFERENCES_AUTO_EXAM_UPDATE:
                final long updateInterval = ExamsHelper.getUpdateInterval(sharedPreferences.getString(key, "0"));
                if (updateInterval == 0) {
                    ExamAutoUpdateService.cancelAutoUpdate(context);
                } else ExamAutoUpdateService.startAutoUpdate(context, updateInterval);

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_NOTIFICATION_SERVICE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    manageVolumeService();
                } else {
                    final Preference preference = findPreference(Const.preferencesKey.PREFERENCES_AUTO_MUTE);
                    if (preference != null) {
                        preference.setEnabled(false);
                    }
                }
            }
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
            volumeControllerService.startMultiAlarmVolumeController(context);

            //enable a receiver -> starts alarms on reboot
            packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            // Beende Background Service
            volumeControllerService.cancelMultiAlarmVolumeController(context);
            volumeControllerService.resetSettingsFile(context);

            //disable a receiver, alarms will not be set on reboot
            packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }
}
