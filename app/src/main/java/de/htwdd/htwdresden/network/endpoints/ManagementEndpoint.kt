package de.htwdd.htwdresden.network.endpoints

import de.htwdd.htwdresden.ui.models.JSemesterPlan
import io.reactivex.Observable
import retrofit2.http.GET

interface ManagementEndpoint {

    @GET("semesterplan.json")
    fun semesterPlan(): Observable<List<JSemesterPlan>>

    @GET("semesterplan.json")
    suspend fun semesterPlanSuspend(): List<JSemesterPlan>
}