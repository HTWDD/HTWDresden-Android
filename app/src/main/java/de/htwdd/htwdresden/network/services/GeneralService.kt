package de.htwdd.htwdresden.network.services

import de.htwdd.htwdresden.ui.models.JStudyYear
import io.reactivex.Observable
import retrofit2.http.GET

interface GeneralService {
    @GET("studyGroups.php")
    fun studyGroups(): Observable<List<JStudyYear>>
}