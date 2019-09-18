package de.htwdd.htwdresden.network.endpoints

import de.htwdd.htwdresden.ui.models.JStudyYear
import io.reactivex.Observable
import retrofit2.http.GET

interface GeneralEndpoint {
    @GET("studyGroups.php")
    fun studyGroups(): Observable<List<JStudyYear>>
}