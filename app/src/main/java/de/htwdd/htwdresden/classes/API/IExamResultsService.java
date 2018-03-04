package de.htwdd.htwdresden.classes.API;

import java.util.List;

import de.htwdd.htwdresden.types.exams.ExamResult;
import de.htwdd.htwdresden.types.exams.Course;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * API für die Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public interface IExamResultsService {
    @GET("/appservice/v2/getcourses")
    Call<List<Course>> getCourses(@Header("Authorization") String credentials);

    @GET("/appservice/v2/getgrades")
    Call<List<ExamResult>> getGrades(@Header("Authorization") String credentials, @Query("AbschlNr") String abschlussNr, @Query("StgNr") String studiengangsNr, @Query("POVersion") String pOVersion);
}
