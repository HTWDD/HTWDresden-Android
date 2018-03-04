package de.htwdd.htwdresden.classes.API;

import java.util.List;

import de.htwdd.htwdresden.types.LessonRoom;
import de.htwdd.htwdresden.types.LessonUser;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API zum Abruf des Stundenplans
 *
 * @author Kay Förster
 */
public interface ITimetableService {
    @GET("v0/studentTimetable.php")
    Call<List<LessonUser>> getStudentTimetable(@Query("StgJhr") final int year, @Query("Stg") final String major, @Query("StgGrp") final String group);

    @GET("v0/professorTimetable.php")
    Call<List<LessonUser>> getProfessorTimetable(@Query("key") final String key);

    @GET("v0/roomTimetable.php")
    Call<List<LessonRoom>> getRoomTimetable(@Query("room") final String key);
}
