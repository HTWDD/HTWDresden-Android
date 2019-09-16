package de.htwdd.htwdresden.network.services

import de.htwdd.htwdresden.ui.models.JExam
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ExamService {

    @GET("GetExams.php")
    fun exams(
        @Query("AbSc") graduation: String,
        @Query("Stg") major: String,
        @Query("StgJhr") year: String,
        @Query("StgNr") direction: String
    ): Observable<List<JExam>>
}