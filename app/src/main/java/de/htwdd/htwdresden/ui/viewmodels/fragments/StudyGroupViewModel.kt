package de.htwdd.htwdresden.ui.viewmodels.fragments

import android.util.Base64
import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.StudyYear
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.nio.charset.Charset

class StudyGroupViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    fun request(): Observable<List<StudyYear>> {
        return RestApi
            .generalService
            .studyGroups()
            .runInThread(Schedulers.io())
            .map { jItems -> jItems.map { StudyYear.from(it) } }
    }


    fun saveToken(token: String) {
        cph.putStudyToken(Base64.encodeToString(token.toByteArray(Charset.forName("UTF-8")), Base64.DEFAULT))
    }
}