package de.htwdd.htwdresden.utils.extensions

import com.google.firebase.crashlytics.FirebaseCrashlytics
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import java.util.*

fun <T: Any> T.TAG() = this::class.java.simpleName

inline fun <T> T.guard(block: T.() -> Unit): T {
    if (this == null) {
        block()
    }
    return this
}

fun <T: Any> T.verbose(message: Any?) = de.htwdd.htwdresden.utils.verbose(TAG(), message)

fun <T: Any> T.debug(message: Any?) = de.htwdd.htwdresden.utils.debug(TAG(), message)

fun <T: Any> T.info(message: Any?) = de.htwdd.htwdresden.utils.info(TAG(), message)

fun <T: Any> T.warn(message: Any?) = de.htwdd.htwdresden.utils.warn(TAG(), message)

fun <T: Any> T.error(message: Any?) = de.htwdd.htwdresden.utils.error(TAG(), message)

fun Any.handleCrashlyticsChange() {
    if (CryptoSharedPreferencesHolder.instance.hasCrashlytics()) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    } else {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
    }
}