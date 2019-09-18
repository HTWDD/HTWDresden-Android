package de.htwdd.htwdresden.network.endpoints

import de.htwdd.htwdresden.ui.models.JCanteen
import de.htwdd.htwdresden.ui.models.JMeal
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface CanteenEnpoint {

    @GET("canteens?near[lat]=51.058583&near[lng]=13.738208&near[dist]=20")
    fun getCanteens(): Observable<List<JCanteen>>

    @GET("canteens/{id}/days/{date}/meals")
    fun getMeals(
        @Path("id") id: String,
        @Path("date") date: String
    ): Observable<List<JMeal>>
}