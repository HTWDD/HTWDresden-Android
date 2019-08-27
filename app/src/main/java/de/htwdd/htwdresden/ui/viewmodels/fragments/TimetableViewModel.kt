package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.ui.models.TimetableHeaderItem
import de.htwdd.htwdresden.ui.models.TimetableItem
import de.htwdd.htwdresden.utils.extensions.*
import io.reactivex.Observable
import java.util.*
import java.util.Calendar.WEEK_OF_YEAR
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class TimetableViewModel: ViewModel() {

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Timetables> {
        verbose("Starting request for timetable")
        return RestApi.timetableService.timetable("61", "041", "17")
            .debug()
            .runInThread()
            .map { jTimetables -> jTimetables.map { Timetable.from(it) } }
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
                it.first.forEach { dateKey ->
                    result.add(dateStringToHeaderItem(dateKey))
                    result.addAll(it.second.filter { p -> p.lessonDays.contains(dateKey) }
                        .map { filteredItem -> TimetableItem(filteredItem) }
                        .sortedWith(compareBy { c -> c }))
                }
                result
            }
    }

    private fun dateStringToHeaderItem(date: String): TimetableHeaderItem {
        val d = date.toDate("MM-dd-yyyy")
        return TimetableHeaderItem(d?.format("EEEE") ?: "", d?.format("dd. MMMM") ?: "")
    }
}