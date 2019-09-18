package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.ui.models.TimetableHeaderItem
import de.htwdd.htwdresden.ui.models.TimetableItem
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.toDate
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.Observable
import java.util.*


class TimetableViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Timetables> {
        val auth = cph.getStudyAuth() ?: return Observable.error(Exception("No Credentials"))
        return RestApi.timetableEndpoint.timetable(auth.group, auth.major, auth.studyYear)
            .runInThread()
            .map { jTimetables -> jTimetables.map { Timetable.from(it) } }
            .map { it.sortedWith(compareBy { c -> c }) }
            .map { timetables ->                                                                    // Grouping to lesson days and lessons
                val sortedKeySet = mutableSetOf<String>()
                val sortedValueSet = mutableSetOf<Timetable>()
                timetables.groupBy { it.lessonDays }.apply {
                    keys.forEach { k -> k.sorted().forEach { sortedKeySet.add(it) } }               // Lesson days
                    values.forEach { v -> v.forEach { sortedValueSet.add(it) } }                    // Lessons
                }
                Pair(sortedKeySet.sortedWith(compareBy { it }), sortedValueSet)
            }
            .map {                                                                                  // Pair -> Single List -> Lesson Days[ Lessons ]
                val result = Timetables()
                it.first.sortedWith(compareBy { it.toDate("MM-dd-yyyy") }).forEach { dateKey ->
                    result.add(dateStringToHeaderItem(dateKey))
                    result.addAll(it.second.filter { p -> p.lessonDays.contains(dateKey) }
                        .map { filteredItem -> TimetableItem(filteredItem) })
                }
                result
            }
    }

    private fun dateStringToHeaderItem(date: String): TimetableHeaderItem {
        val d = date.toDate("MM-dd-yyyy")
        return TimetableHeaderItem(d?.format("EEEE") ?: "", d ?: Date())
    }
}