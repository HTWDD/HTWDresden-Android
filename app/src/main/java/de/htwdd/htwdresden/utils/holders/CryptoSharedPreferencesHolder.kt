package de.htwdd.htwdresden.utils.holders

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import de.htwdd.htwdresden.utils.extensions.guard
import io.reactivex.subjects.BehaviorSubject
import java.nio.charset.Charset

class CryptoSharedPreferencesHolder private constructor() {

    private object Holder { val INSTANCE = CryptoSharedPreferencesHolder() }
    private lateinit var masterKeyAlias: String
    private lateinit var sharedPreferences: SharedPreferences

    sealed class SubscribeType {
        object StudyToken: SubscribeType()
        object AuthToken: SubscribeType()
        object Crashlytics: SubscribeType()
    }

    companion object {
        private val subject = BehaviorSubject.create<SubscribeType>()
        val instance: CryptoSharedPreferencesHolder by lazy { Holder.INSTANCE }
        fun init(context: Context) {
            instance.masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            instance.sharedPreferences = EncryptedSharedPreferences.create(
                "htw_encrypted_shared_prefs",
                instance.masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
        }

        private const val STUDY_TOKEN       = "STUDY_TOKEN"
        private const val AUTH_TOKEN        = "AUTH_TOKEN"
        private const val IS_FIRST_RUN      = "IS_FIRST_RUN"
        private const val HAS_CRASHLYTICS   = "HAS_CRASHLYTICS"
    }

    fun putStudyToken(studyToken: String) {
        sharedPreferences.edit {
            subject.onNext(SubscribeType.StudyToken)
            putString(STUDY_TOKEN, studyToken)
        }
    }

    fun getStudyToken() = sharedPreferences.getString(STUDY_TOKEN, "")

    fun getStudyAuth() = readAuthToken(getStudyToken())

    fun putAuthToken(authToken: String) {
        sharedPreferences.edit {
            subject.onNext(SubscribeType.AuthToken)
            putString(AUTH_TOKEN, authToken)
        }
    }

    fun getAuthToken() = sharedPreferences.getString(AUTH_TOKEN, "")

    fun setOnboarding(isNeeded: Boolean) = sharedPreferences.edit { putBoolean(IS_FIRST_RUN, isNeeded) }

    fun needsOnboarding() = sharedPreferences.getBoolean(IS_FIRST_RUN, true)

    fun hasCrashlytics() = sharedPreferences.getBoolean(HAS_CRASHLYTICS, false)

    fun setCrashlytics(active: Boolean) {
        sharedPreferences.edit {
            subject.onNext(SubscribeType.Crashlytics)
            putBoolean(HAS_CRASHLYTICS, active)
        }
    }

    fun onChanged() = subject

    fun clear() {
        sharedPreferences.edit {
            remove(STUDY_TOKEN)
            subject.onNext(SubscribeType.StudyToken)
            remove(AUTH_TOKEN)
            subject.onNext(SubscribeType.AuthToken)
            remove(HAS_CRASHLYTICS)
            subject.onNext(SubscribeType.Crashlytics)
            remove(IS_FIRST_RUN)
        }
    }

    private fun readAuthToken(token: String?): StudyAuth? {
        token.guard { return null }
        val rawToken = String(Base64.decode(token, Base64.DEFAULT), Charset.forName("UTF-8"))
        val chunks = rawToken.split(":")
        if (chunks.size < 4) { return null }
        return StudyAuth(
            chunks[0],
            chunks[1],
            chunks[2],
            chunks[3].first().toString()
        )
    }

    //---------------------------------------------------------------------------------------------- Data Class
    data class StudyAuth(val studyYear: String, val major: String, val group: String, val graduation: String)
}