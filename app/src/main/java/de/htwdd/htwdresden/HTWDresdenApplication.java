package de.htwdd.htwdresden;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.heinrichreimer.canteenbalance.cardreader.CardBalance;

import de.htwdd.htwdresden.classes.DatabaseMigrations;
import de.htwdd.htwdresden.classes.PreferencesMigrations;
import de.htwdd.htwdresden.service.MensaCreditReceiver;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class HTWDresdenApplication extends Application {
    private MensaCreditReceiver mensaCreditReceiver;
    private FirebaseAnalytics mFirebaseAnalytics;
    Crashlytics mCrashlytics;
    SharedPreferences sharedPreferences;
    Realm realm;

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Migrationen durchführen
        final PreferencesMigrations preferencesMigrations = new PreferencesMigrations(getApplicationContext());
        preferencesMigrations.migrate();

        if (sharedPreferences.getBoolean("firebase_analytics.enable", false)) {
            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

            if(!sharedPreferences.getBoolean("firebase_crashlytics.enable", true)){
                //Deaktiviere Crashlytics
                mCrashlytics = new Crashlytics.Builder().build();
                Fabric.with(this, mCrashlytics);
            }
        }

        // Realm initialisieren
        Realm.init(this);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .migration(new DatabaseMigrations())
                .schemaVersion(5)
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
