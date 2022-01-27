package de.htwdd.htwdresden.network.endpoints

import de.htwdd.htwdresden.ui.models.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface DocsEndpoint {

    @GET("semesterplan")
    fun semesterPlan(@Query("language") language: String): Observable<List<JSemesterPlan>>

    @GET("semesterplan")
    suspend fun semesterPlanSuspend(): List<JSemesterPlan>

    @GET("campusplan")
    fun campusPlan(@Query("language") language: String): Observable<List<JCampusPlan>>

    @GET("principalexamoffice")
    fun principalExamOffice(@Query("language") language: String): Observable<JManagement>

    @GET("studentadministration")
    fun administration(@Query("language") language: String): Observable<JManagement>

    @GET("sturahtw")
    fun sturaHTW(@Query("language") language: String): Observable<JManagement>

    @GET("notes")
    fun notes(@Query("language") language: String): Observable<JNotes>

}