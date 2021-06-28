package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.Grades
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import de.htwdd.htwdresden.utils.holders.StringHolder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class GradesViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }
    private val sh by lazy { StringHolder.instance }

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Grades> {
        return requestCourses()
            .runInThread()
            .flatMap { courses ->
                val requests = courses.map { course ->
                    requestGrades(course).map { it.map { jGrade -> Grade.from(jGrade) } }
                }
                Observable.combineLatest(requests) {
                    it.toCollection(ArrayList()) as ArrayList<List<Grade>>
                }
            }
            .map { it.flatten() }
            .map { grades ->
                val sortedKeys      = mutableSetOf<Long>()
                val sortedValues    = mutableSetOf<Grade>()
                grades.groupBy { it.semester }.apply {
                    keys.sortedDescending().forEach { sortedKeys.add(it) }
                    values.forEach { v -> v.forEach { sortedValues.add(it) } }
                }
                sortedKeys to sortedValues
            }
            .map { pair ->
                val result = Grades()

                // hole average & credits
                val holeCredits = pair.second.map { it.credits }.sum()
                val holeGrades  = pair.second.map { it.credits * (it.grade?.div(100f) ?: 0f) }.sum()
                //bug 21007 average grades turned off
                result.add(GradeAverageItem(try { if (holeGrades > 0) { holeGrades / holeCredits } else { 0f } } catch (e: Exception) { 0f }, holeCredits))

                // flatten list and converting to header and grade item
                pair.first.forEach { key ->
                    val gradeValues = pair.second.filter { f -> f.semester == key }.sortedWith(compareBy { it })
                    val credits = gradeValues.map { it.credits }.sum()
                    val grades = gradeValues.map { it.credits *  (it.grade?.div(100f) ?: 0f) }.sum()
                    val gradeAverage = try { if (grades > 0) { grades / credits } else { 0f } } catch (e: Exception) { 0f }
                    //bug 21007 average grades turned off
                    //result.add(GradeHeaderItem(getSemester(key), "${sh.getString(R.string.exams_grade_average, gradeAverage)} (${sh.getString(R.string.exams_stats_count_credits, credits)})"))
                    result.add(GradeHeaderItem(getSemester(key),
                        sh.getString(R.string.exams_stats_count_credits, credits)
                    ))
                    result.addAll(gradeValues.map { v -> GradeItem(v) })
                }
                result
            }
    }

    private fun requestCourses(): Observable<List<Course>> {
        return RestApi
            .courseEndpoint
            .getCourses("Basic ${cph.getAuthToken()}")
            .runInThread(Schedulers.io())
            .map { it.map { jCourse -> Course.from(jCourse) } }
    }

    private fun requestGrades(forCourse: Course): Observable<List<JGrade>> {
        return RestApi
            .gradeEndpoint
            .getGrades(
                "Basic ${cph.getAuthToken()}",
                forCourse.examinationRegulations.toString(),
                forCourse.majorNumber,
                forCourse.graduationNumber)
            .runInThread(Schedulers.io())
    }

    private fun getSemester(semester: Long): String {
        val s = "$semester"
        return if (s.count() < 5) {
            s
        } else {
            when {
                s.endsWith("1") -> "${sh.getString(R.string.academic_year_summer)} ${s.substring(0 until s.count() - 1)}"
                s.endsWith("2") -> "${sh.getString(R.string.academic_year_winter)} ${s.substring(0 until s.count() - 1)}"
                else -> s
            }
        }
    }
}