package de.htwdd.htwdresden.network.services

import de.htwdd.htwdresden.ui.models.JTimetable
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface TimetableService {

    @GET("studentTimetable.php")
    fun timetable(@Query("StgGrp") studyGroup: String,
                  @Query("Stg") studyMajor: String,
                  @Query("StgJhr") studyYear: String): Observable<List<JTimetable>>

}