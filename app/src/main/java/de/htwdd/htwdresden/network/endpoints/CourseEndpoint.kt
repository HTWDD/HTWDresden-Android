package de.htwdd.htwdresden.network.endpoints

import de.htwdd.htwdresden.ui.models.JCourse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header

interface CourseEndpoint {

    @GET("getcourses")
    fun getCourses(@Header("Authorization") authToken: String): Observable<List<JCourse>>
}