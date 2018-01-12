package de.htwdd.htwdresden;

import android.app.Application;
import android.content.IntentFilter;

import com.heinrichreimer.canteenbalance.cardreader.CardBalance;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import de.htwdd.htwdresden.classes.DatabaseMigrations;
import de.htwdd.htwdresden.classes.PreferencesMigrations;
import de.htwdd.htwdresden.service.MensaCreditReceiver;
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
    private MensaCreditReceiver mensaCreditReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        // Migrationen durchführen
        final PreferencesMigrations preferencesMigrations = new PreferencesMigrations(getApplicationContext());
        preferencesMigrations.migrate();

        // ACRA starten
        ACRA.init(this);

        // Realm initialisieren
        Realm.init(this);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .migration(new DatabaseMigrations())
                .schemaVersion(4)
                .build();
        Realm.setDefaultConfiguration(configuration);

        // Updates laden
        final Thread thread = new Thread(new CheckUpdates(getApplicationContext()));
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

        // Mensa Guthaben
        mensaCreditReceiver = new MensaCreditReceiver();
        registerReceiver(mensaCreditReceiver, new IntentFilter(CardBalance.ACTION_CARD_BALANCE));
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(mensaCreditReceiver);
        super.onTerminate();
    }
}
