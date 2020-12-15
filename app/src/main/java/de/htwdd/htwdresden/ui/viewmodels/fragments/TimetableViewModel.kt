package de.htwdd.htwdresden.ui.viewmodels.fragments

import android.util.Log.d
import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.TAG
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.toDate
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.Observable
import io.realm.Realm
import java.util.*

class TimetableViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }
    private val realm: Realm by lazy { Realm.getDefaultInstance() }
    private val currentWeekLessons = ArrayList<Timetable>()
    private val nextWeekLessons = ArrayList<Timetable>()

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Timetables> {
        val auth = cph.getStudyAuth() ?: return Observable.error(Exception("No Credentials"))


        return RestApi.timetableEndpoint.timetable(auth.group, auth.major, auth.studyYear)
            .runInThread()
            .map { jTimetables -> jTimetables.map { Timetable.from(it) } }
            .map { it.sortedWith(compareBy { c -> c }) }
            .map { timetableList ->                                                                    // Grouping to lesson days and lessons

                deleteAllTimetable()
                timetableList.forEach { TimetableRealm().update(it) }

                val timetables = realm.where(TimetableRealm::class.java).findAll().map { TimetableRealm.toTimetable(
                    it
                ) }
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
                        .sortedBy { it.beginTime }
                        .map { filteredItem -> TimetableItem(filteredItem) })
                }
                setWeekOverviewData(it.second)
                result
            }
    }

    private fun setWeekOverviewData(timetables: MutableSet<Timetable>) {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar[Calendar.WEEK_OF_YEAR]
        val currentYear = calendar[Calendar.YEAR]
        currentWeekLessons.clear()
        nextWeekLessons.clear()

        timetables.forEach { timetable ->
            timetable.lessonDays.forEach {
                val date = it.toDate("MM-dd-yyyy")
                if(date!=null) {
                    val targetCalender = Calendar.getInstance()
                    targetCalender.time = date
                    if(targetCalender.get(Calendar.WEEK_OF_YEAR)==currentWeek && targetCalender.get(Calendar.YEAR)==currentYear) {
                        currentWeekLessons.add(timetable)
                    } else if (targetCalender.get(Calendar.WEEK_OF_YEAR)==currentWeek+1 && targetCalender.get(Calendar.YEAR)==currentYear) {
                        nextWeekLessons.add(timetable)
                    }
                }
            }
        }
    }

    private fun dateStringToHeaderItem(date: String): TimetableHeaderItem {
        d(TAG(), date)
        val d = date.toDate("MM-dd-yyyy")
        return TimetableHeaderItem(d?.format("EEEE") ?: "", d ?: Date())
    }
}