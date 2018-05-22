package de.htwdd.htwdresden;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.heinrichreimer.canteenbalance.cardreader.CardBalance;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraDialog;
import org.acra.annotation.AcraMailSender;

import de.htwdd.htwdresden.classes.DatabaseMigrations;
import de.htwdd.htwdresden.classes.PreferencesMigrations;
import de.htwdd.htwdresden.service.MensaCreditReceiver;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@AcraCore(
        buildConfigClass = BuildConfig.class,
        resReportSendSuccessToast = R.string.crash_dialog_ok_toast,
        reportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.STACK_TRACE, ReportField.LOGCAT, ReportField.USER_COMMENT}
)
@AcraDialog(
        resTitle = R.string.app_name,
        resText = R.string.crash_dialog_text,
        resIcon = R.drawable.ic_warning_24dp,
        resTheme = R.style.AppTheme,
        resCommentPrompt = R.string.crash_dialog_comment_prompt
)
@AcraMailSender(mailTo = "app@htw-dresden.de", resSubject = R.string.app_name)
public class HTWDresdenApplication extends Application {
    private MensaCreditReceiver mensaCreditReceiver;

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Migrationen durchführen
        final PreferencesMigrations preferencesMigrations = new PreferencesMigrations(getApplicationContext());
        preferencesMigrations.migrate();

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
