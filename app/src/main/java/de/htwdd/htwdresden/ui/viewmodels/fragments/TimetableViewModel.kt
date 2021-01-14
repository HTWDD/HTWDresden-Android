package de.htwdd.htwdresden.ui.viewmodels.fragments

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.CalendarContract
import android.util.Log.d
import android.util.Log.e
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import kotlin.collections.HashMap

class TimetableViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Timetables> {
        val auth = cph.getStudyAuth() ?: return Observable.error(Exception("No Credentials"))
        val timetables = RestApi.timetableEndpoint.timetable(auth.group, auth.major, auth.studyYear)
            .runInThread()
            .map { jTimetables -> jTimetables.map { Timetable.from(it) } }
            .map { it.sortedWith(compareBy { c -> c }) }
        return handleTimetableResult(timetables)
    }

    private fun handleTimetableResult(timetables: Observable<List<Timetable>>): Observable<Timetables> {
        return timetables.map { timetableList ->                                                    // Grouping to lesson days and lessons
            deleteAllIfNotCreatedByUser()
            timetableList.forEach { TimetableRealm().update(it) {} }
            val sortedKeySet = mutableSetOf<String>()
            val sortedValueSet = mutableSetOf<Timetable>()
            getAllTimetables().groupBy { it.lessonDays }.apply {
                keys.forEach { k ->
                    k.sorted().forEach { sortedKeySet.add(it) }
                }                                                                                   // Lesson days
                values.forEach { v -> v.forEach { sortedValueSet.add(it) } }                        // Lessons
            }
            Pair(sortedKeySet.sortedWith(compareBy { it }), sortedValueSet)
        }
            .map {                                                                                  // Pair -> Single List -> Lesson Days[ Lessons ]
                val result = Timetables()
                it.first.sortedWith(compareBy { it.toDate("MM-dd-yyyy") }).forEach { dateKey ->
                    result.add(dateStringToHeaderItem(dateKey))
                    result.addAll(it.second.filter { p -> p.lessonDays.contains(dateKey) }
                        .sortedBy { it.beginTime }
                        .map { filteredItem -> TimetableItem(filteredItem) })
                }
                result
            }
    }


    fun getTimetablesFromDb(): Observable<Timetables> {
        val timetables = getAllTimetables()
        if(timetables.isEmpty()) return Observable.error(Exception("No Credentials"))
        else return handleTimetableResult(Observable.just(timetables))
    }

    private fun dateStringToHeaderItem(date: String): TimetableHeaderItem {
        d(TAG(), date)
        val d = date.toDate("MM-dd-yyyy")
        return TimetableHeaderItem(d?.format("EEEE") ?: "", d ?: Date())
    }

    fun exportCalendar(contentResolver: ContentResolver, index: Int, calendarId: Long) {
        val timetables = getAllTimetables().toCollection(ArrayList())
        val eventsToExport = ArrayList<Pair<Date, Timetable>>()

        when(index) {
            0 -> {
                timetables.forEach { timetable -> getLessonDaysAsDates(timetable.lessonDays).forEach {
                    if(it[Calendar.WEEK_OF_YEAR]==currentWeek) eventsToExport.add(Pair(it.time, timetable))
                } }
            }
            1 -> {
                timetables.forEach { timetable -> getLessonDaysAsDates(timetable.lessonDays).forEach {
                    if(it[Calendar.WEEK_OF_YEAR]==currentWeek+1) eventsToExport.add(Pair(it.time, timetable))
                } }
            }
            2 -> {
                timetables.forEach { timetable -> getLessonDaysAsDates(timetable.lessonDays).forEach {
                    eventsToExport.add(Pair(it.time, timetable))
                } }
            }
        }

        eventsToExport.forEach {
            val values = ContentValues().apply {
                val startTime = it.first.calendar.addTime(it.second.beginTime)
                val endTime = it.first.calendar.addTime(it.second.endTime)
                put(CalendarContract.Events.DTSTART, startTime.timeInMillis)
                put(CalendarContract.Events.DTEND, endTime.timeInMillis)
                put(CalendarContract.Events.TITLE, it.second.name)
                put(CalendarContract.Events.DESCRIPTION, it.second.createDescriptionForCalendar())
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(
                    CalendarContract.Events.EVENT_TIMEZONE,
                    it.first.calendar.timeZone.toString()
                )
            }
            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }
    }

    private fun getLessonDaysAsDates(lessonDays: List<String>) : List<Calendar> =
        lessonDays.mapNotNull { it.toDate("MM-dd-yyyy")}.map { it.calendar }

}