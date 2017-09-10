package de.htwdd.htwdresden;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import de.htwdd.htwdresden.classes.DatabaseMigrations;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@ReportsCrashes(
        mode = ReportingInteractionMode.DIALOG,
        mailTo = "app@htw-dresden.de",
        resDialogTitle = R.string.app_name,
        resDialogIcon = R.drawable.ic_warning_24dp,
        resDialogText = R.string.crash_dialog_text,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast, // optional. displays a Toast message when the user accepts to send a report.
        resDialogTheme = R.style.AppTheme,
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.STACK_TRACE,
                ReportField.LOGCAT,
                ReportField.USER_COMMENT
        },
        sharedPreferencesName = "de.htwdd.htwdresden_preferences"
)
public class HTWDresdenApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Setze Einstellungen beim ersten Starten der App
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean("FIRST_RUN", true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("FIRST_RUN", false);
            editor.putBoolean("acra.enable", true);
            editor.apply();
        }

        // ACRA starten
        ACRA.init(this);

        // Realm initialisieren
        Realm.init(this);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .migration(new DatabaseMigrations())
                .schemaVersion(3)
                .build();
        Realm.setDefaultConfiguration(configuration);

        // Updates laden
        final Thread thread = new Thread(new CheckUpdates(getApplicationContext()));
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }
}
