package de.htwdd.htwdresden;


import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.service.VolumeControllerService;

/**
 * Fragment für die Einstellungen
 *
 * @author Kay Förster
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Setze Toolbartitle
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_settings));
        return super.onCreateView(inflater, container, savedInstanceState);
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
    public void onSharedPreferenceChanged(@NonNull final SharedPreferences sharedPreferences, final String s) {
        if (s.equals(Const.preferencesKey.PREFERENCES_AUTO_MUTE)) {
            final Context context = getActivity();
            final VolumeControllerService volumeControllerService = new VolumeControllerService();
            final ComponentName receiver = new ComponentName(context, VolumeControllerService.HtwddBootReceiver.class);
            final PackageManager packageManager = context.getPackageManager();

            if (sharedPreferences.getBoolean(s, false)) {
                // Starte Hintergrundservice
                volumeControllerService.startMultiAlarmVolumeController(context);
                //enable a receiver -> starts alarms on reboot
                packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            } else {
                // Beende Hintergrundservice
                volumeControllerService.cancelMultiAlarmVolumeController(context);
                volumeControllerService.resetSettingsFile(context);
                //disable a receiver, alarms will not be set on reboot
                PackageManager pm = context.getPackageManager();
                pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        }
    }
}
