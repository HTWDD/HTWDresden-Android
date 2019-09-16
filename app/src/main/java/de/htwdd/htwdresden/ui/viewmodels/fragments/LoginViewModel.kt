package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.Course
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class LoginViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    fun login(token: String): Observable<List<Course>> {
        return RestApi
            .courseService
            .getCourses("Basic $token")
            .runInThread(Schedulers.io())
            .map { it.map { jCourse -> Course.from(jCourse) } }
    }

    fun saveToken(token: String) {
        cph.putAuthToken(token)
    }
}