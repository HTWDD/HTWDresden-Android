package de.htwdd.htwdresden.network.endpoints

import de.htwdd.htwdresden.ui.models.JTimetable
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface TimetableEndpoint {

    @GET("studentTimetable.php")
    fun timetable(
        @Query("StgGrp") studyGroup: String,
        @Query("Stg") studyMajor: String,
        @Query("StgJhr") studyYear: String
    ): Observable<List<JTimetable>>

    @GET("roomTimetable.php")
    fun roomTimetable(@Query("room") room: String): Observable<List<JTimetable>>
}