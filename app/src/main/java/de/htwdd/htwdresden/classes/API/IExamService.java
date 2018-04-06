package de.htwdd.htwdresden.classes.API;

import java.util.List;

import de.htwdd.htwdresden.types.exams.ExamDate;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API für Prüfungen. Nutzung über {@link Retrofit2Rubu}
 *
 * @author Kay Förster
 */
public interface IExamService {

    @GET("v0/GetExams.php")
    Call<List<ExamDate>> getExamSchedule(@Query("StgJhr") final int year, @Query("Stg") final String major, @Query("AbSc") final char graduation, @Query("Stgri") final String direction);

    @GET("v0/GetExams.php")
    Call<List<ExamDate>> getExamSchedule(@Query("Prof") final String professor);
}
