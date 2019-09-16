package de.htwdd.htwdresden.network.services

import de.htwdd.htwdresden.ui.models.JCourse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header

interface CourseService {

    @GET("getcourses")
    fun getCourses(@Header("Authorization") authToken: String): Observable<List<JCourse>>
}