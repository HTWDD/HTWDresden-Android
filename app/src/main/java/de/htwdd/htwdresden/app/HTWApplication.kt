package de.htwdd.htwdresden.app

import android.app.Application
import android.content.IntentFilter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.heinrichreimer.canteenbalance.cardreader.CardBalance
import de.htwdd.htwdresden.classes.DatabaseMigrations
import de.htwdd.htwdresden.receivers.MensaCardReceiver
import de.htwdd.htwdresden.utils.extensions.handleCrashlyticsChange
import de.htwdd.htwdresden.utils.holders.*
import io.realm.Realm
import io.realm.RealmConfiguration

@Suppress("unused")
class HTWApplication: Application() {

    private val mensaCardReceiver by lazy { MensaCardReceiver() }

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
        initializeRealm()
        initializeHolders()
        initializeGoogleServices()
    }

    private fun registerReceivers() {
        registerReceiver(mensaCardReceiver, IntentFilter(CardBalance.ACTION_CARD_BALANCE))
    }

    private fun initializeRealm() {
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .migration(DatabaseMigrations())
            .schemaVersion(7)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }

    private fun initializeHolders() {
        StringHolder.init(this)
        ResourceHolder.init(this)
        ContextHolder.init(this)
        ColorHolder.init(this)
        CryptoSharedPreferencesHolder.init(this)
    }

    private fun initializeGoogleServices() {
        handleCrashlyticsChange()
    }

    override fun onTerminate() {
        unregisterReceiver(mensaCardReceiver)
        super.onTerminate()
    }
}