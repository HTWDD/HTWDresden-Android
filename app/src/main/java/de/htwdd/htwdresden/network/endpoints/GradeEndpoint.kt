package de.htwdd.htwdresden.network.endpoints

import de.htwdd.htwdresden.ui.models.JGrade
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GradeEndpoint {

    @GET("getgrades")
    fun getGrades(
        @Header("Authorization") authToken: String,
        @Query("POVersion") examinationRegulations: String,
        @Query("StgNr") majorNumber: String,
        @Query("AbschlNr") graduationNumber: String
    ): Observable<List<JGrade>>
}