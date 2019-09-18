package de.htwdd.htwdresden.network.endpoints

import de.htwdd.htwdresden.ui.models.JExam
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ExamEndpoint {

    @GET("GetExams.php")
    fun exams(
        @Query("AbSc") graduation: String,
        @Query("Stg") major: String,
        @Query("StgJhr") year: String,
        @Query("StgNr") direction: String
    ): Observable<List<JExam>>
}