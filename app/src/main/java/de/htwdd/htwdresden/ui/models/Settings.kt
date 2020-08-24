package de.htwdd.htwdresden.ui.models

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.util.Base64
import androidx.databinding.ObservableField
import de.htwdd.htwdresden.utils.extensions.debug
import de.htwdd.htwdresden.utils.extensions.guard
import de.htwdd.htwdresden.utils.extensions.runInUiThread
import de.htwdd.htwdresden.utils.holders.ContextHolder
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.nio.charset.Charset

class SettingsModel {
    private val ch: ContextHolder by lazy { ContextHolder.instance }
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }
    private val disposable = CompositeDisposable()

    private var onImprintClosure: () -> Unit = {}
    private var onDataProtectionClosure: () -> Unit = {}
    private var onDeleteAllDataClosure: () -> Unit = {}
    private var onStudyGroupClosure: () -> Unit = {}
    private var onLoginClosure: () -> Unit = {}

    val studyGroup      = ObservableField<String>(readStudyToken(cph.getStudyToken()))
    val loginData       = ObservableField<String>(readAuthToken(cph.getAuthToken()))
    val version         = ObservableField<String>()
    val hasCrashlytics  = ObservableField<Boolean>(cph.hasCrashlytics())

    init {
        cph.onChanged().debug().runInUiThread().subscribe {
            when (it) {
                is CryptoSharedPreferencesHolder.SubscribeType.StudyToken -> {
                    studyGroup.set(readStudyToken(cph.getStudyToken()))
                }

                is CryptoSharedPreferencesHolder.SubscribeType.AuthToken -> {
                    loginData.set(readAuthToken(cph.getAuthToken()))
                }

                is CryptoSharedPreferencesHolder.SubscribeType.Crashlytics -> {
                    hasCrashlytics.set(cph.hasCrashlytics())
                }
            }
        }.addTo(disposable)
    }

    fun openGitHub() = ch.openUrl("https://github.com/HTWDD/HTWDresden-Android")

    fun composEmail() {
        val intent = Intent(ACTION_SENDTO).apply {
            data = Uri.parse("mailto:app@htw-dresden.de")
            putExtra(EXTRA_SUBJECT, "Android App")
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ch.packageManager()) != null) {
            ch.startActivity(intent)
        }
    }

    fun openImprint() = onImprintClosure()
    fun openDataProtection() = onDataProtectionClosure()

    fun onImprintClick(callback: () -> Unit) {
        onImprintClosure = callback
    }
    fun onDataProtectionClick(callback: () -> Unit) {
        onDataProtectionClosure = callback
    }

    fun deleteAllData() = onDeleteAllDataClosure()

    fun onDeleteAllDataClick(callback: () -> Unit) {
        onDeleteAllDataClosure = callback
    }

    fun changeStudyGroup() = onStudyGroupClosure()

    fun onStudyGroupClick(callback: () -> Unit) {
        onStudyGroupClosure = callback
    }

    fun onLoginClick(callback: () -> Unit) {
        onLoginClosure = callback
    }

    fun changeLogin() = onLoginClosure()

    fun onCrashlytics(checked: Boolean) = cph.setCrashlytics(checked)

    private fun readStudyToken(token: String?): String {
        token.guard { return "" }
        val rawToken = String(Base64.decode(token, Base64.DEFAULT), Charset.forName("UTF-8"))
        val chunks = rawToken.split(":")
        return chunks.joinToString(" | ")
    }

    private fun readAuthToken(token: String?): String {
        token.guard { return "" }
        val rawToken = String(Base64.decode(token, Base64.DEFAULT), Charset.forName("UTF-8"))
        val chunks = rawToken.split(":")
        return chunks.firstOrNull() ?: ""
    }
}