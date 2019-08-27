package de.htwdd.htwdresden.network.services

import de.htwdd.htwdresden.ui.models.JSemesterPlan
import io.reactivex.Observable
import retrofit2.http.GET

interface ManagementService {

    @GET("semesterplan.json")
    fun semesterPlan(): Observable<List<JSemesterPlan>>
}