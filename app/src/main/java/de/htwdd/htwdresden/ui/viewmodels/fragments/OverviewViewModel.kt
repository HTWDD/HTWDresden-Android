package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.Overviews
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.nullWhenEmpty
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import de.htwdd.htwdresden.utils.holders.StringHolder
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class OverviewViewModel: ViewModel() {

    private val sh by lazy { StringHolder.instance }

    fun request(): Observable<Overviews> {
        return Observables.combineLatest(
            requestScheduleForToday().runInThread(),
            requestMealsForToday().runInThread(),
            requestGrades().runInThread()
        ) { s, m, g ->
            Overviews().apply {
                addAll(s)
                addAll(g)
                addAll(m)
            }
        }
    }

    private fun requestScheduleForToday(): Observable<Overviews> {
        val result = Overviews()
        result.add(OverviewHeaderItem(sh.getString(R.string.navi_timetable), Date().format("EEEE, dd. MMMM")))

        val auth = CryptoSharedPreferencesHolder.instance.getStudyAuth() ?: return Observable.defer {
            result.add(OverviewStudyGroupItem())
            Observable.just(result)
        }

        return RestApi
            .timetableEndpoint
            .timetable(auth.group, auth.major, auth.studyYear)
            .map { it.map { jTimetable -> Timetable.from(jTimetable) } }
            .map { it.sortedWith(compareBy { c -> c }) }
            .map {
                val hiddenEventsIds = getHiddenTimetables()
                deleteAllIfNotCreatedByUser()
                it.forEach { timetable ->
                    if(hiddenEventsIds.contains(timetable.id)) timetable.isHidden = true
                    TimetableRealm().update(timetable) {}
                }
                getNotHiddenTimetables()
            }
            .map { it.filter { timetable -> timetable.lessonDays.contains(Date().format("MM-dd-yyyy")) } }
            .map { it.sortedWith(compareBy { c -> c }) }
            .map {
                result.apply {
                    addAll(it.map { TimetableItem(it) })
                    if (it.isEmpty()) {
                        add(OverviewFreeDayItem())
                    }
                }
            }
            .onErrorReturn { Overviews().apply {
                val timetablesFromDB = getTimetablesFromDb()
                if(timetablesFromDB.isNotEmpty()) {
                    add(OverviewHeaderItem(sh.getString(R.string.navi_timetable), Date().format("EEEE, dd. MMMM")))
                    addAll(getTimetablesFromDb())
                }
            } }
    }

    private fun getTimetablesFromDb(): List<TimetableItem> {
        val timetables = getNotHiddenTimetables()
        return if(timetables.isEmpty()) emptyList()
        else timetables.filter { timetable -> timetable.lessonDays.contains(Date().format("MM-dd-yyyy")) }.sortedWith(compareBy { c -> c }).map { TimetableItem(it) }
    }

    private fun requestMealsForToday(): Observable<Overviews> {
        return RestApi
            .canteenEndpoint
            .getMeals("80", Date().format("yyyy-MM-dd"))
            .runInThread(Schedulers.io())
            .map { it.map { jMeal -> Meal.from(jMeal) } }
            .map { meals ->
                val result = Overviews()
                if (meals.isNotEmpty()) {
                    result.add(OverviewHeaderItem(sh.getString(R.string.mensa), sh.getString(R.string.mensa_reichenbach)))
                }
                result.addAll(meals.map { OverviewMensaItem(it) })
                result
            }
            .onErrorReturn { Overviews() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun requestGrades(): Observable<Overviews> {
        val result = Overviews().apply {
            //bug 21007 average grades turned off
            add(OverviewHeaderItem(sh.getString(R.string.navi_exams), sh.getString(R.string.exams_grade_average, 0.0), false))
        }

        val auth = CryptoSharedPreferencesHolder.instance.getAuthToken()?.nullWhenEmpty ?: return Observable.defer {
            result.add(OverviewLoginItem())
            Observable.just(result)
        }

        return requestCourses(auth)
            .runInThread()
            .flatMap {  courses ->
                val requests = courses.map { course -> requestGrades(course, auth) }
                Observable.combineLatest(requests) { it.toCollection(ArrayList()) as ArrayList<List<Grade>> }
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
                val holeCredits = pair.second.map { it.credits }.sum()
                val holeGrades  = pair.second.map { it.credits * (it.grade?.div(100f) ?: 0f) }.sum()
                val avg =  if (holeGrades > 0) { holeGrades / holeCredits } else { 0f }
                //bug 21007 average grades turned off
                val headerItem = result[0] as OverviewHeaderItem
                headerItem.credits = sh.getString(R.string.exams_grade_average, avg)
                result.add(OverviewGradeItem(pair.second.filter { it.grade != null }.size.toString(), holeCredits))
                result
            }
            .onErrorReturn { Overviews() }
    }

    private fun requestCourses(auth: String): Observable<List<Course>> {
        return RestApi
            .courseEndpoint
            .getCourses("Basic $auth")
            .runInThread(Schedulers.io())
            .map { it.map { jCourse -> Course.from(jCourse) } }
    }

    private fun requestGrades(forCourse: Course, auth: String): Observable<List<Grade>> {
        return RestApi
            .gradeEndpoint
            .getGrades(
                "Basic $auth",
                forCourse.examinationRegulations.toString(),
                forCourse.majorNumber,
                forCourse.graduationNumber)
            .runInThread(Schedulers.io())
            .map { it.map { jGrade -> Grade.from(jGrade) } }
    }
}