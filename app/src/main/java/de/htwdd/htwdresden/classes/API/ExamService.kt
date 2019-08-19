package de.htwdd.htwdresden.classes.API

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import de.htwdd.htwdresden.ui.models.JExam
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ExamService {

    // region Requests
    @GET("GetExams.php")
    fun exams(@Query("AbSc") graduation: String, @Query("Stg") major: String,
              @Query("StgJhr") year: String, @Query("StgNr") direction: String): Observable<List<JExam>>
    // endregion

    // region Retrofit
    companion object {
        val instance: ExamService by lazy {
            val gson = GsonBuilder().apply {
                setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            }.create()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://www2.htw-dresden.de/~app/API/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            retrofit.create(ExamService::class.java)
        }
    }
    //endregion
}