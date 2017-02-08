package de.htwdd.htwdresden;


import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            case Const.preferencesKey.PREFERENCES_AUTO_MUTE:
                final boolean value1 = sharedPreferences.getBoolean(key, false);
                final PackageManager packageManager = context.getPackageManager();
                final VolumeControllerService volumeControllerService = new VolumeControllerService();
                final ComponentName receiver = new ComponentName(context, VolumeControllerService.HtwddBootReceiver.class);

                if (value1) {
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
                break;
            case Const.preferencesKey.PREFERENCES_AUTO_EXAM_UPDATE:
                final long updateInterval = ExamsHelper.getUpdateInterval(sharedPreferences.getString(key, "0"));
                if (updateInterval == 0) {
                    ExamAutoUpdateService.cancelAutoUpdate(context);
                } else ExamAutoUpdateService.startAutoUpdate(context, updateInterval);

                break;
        }
    }
}
