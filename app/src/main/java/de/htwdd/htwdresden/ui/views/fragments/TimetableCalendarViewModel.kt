package de.htwdd.htwdresden.ui.views.fragments

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.ui.views.fragments.TimetableCalendarFragment.Companion.CALENDAR_CURRENT_WEEK
import de.htwdd.htwdresden.ui.views.fragments.TimetableCalendarFragment.Companion.CALENDAR_NEXT_WEEK
import de.htwdd.htwdresden.utils.extensions.currentWeek
import de.htwdd.htwdresden.utils.extensions.currentYear
import de.htwdd.htwdresden.utils.extensions.toDate
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TimetableCalendarViewModel(private val calenderType: Int) : ViewModel() {

    val items = ObservableArrayList<Timetable>()
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }
    private val realm: Realm by lazy { Realm.getDefaultInstance() }

    fun setup() {
        request()
    }

    @Suppress("UNCHECKED_CAST")
    fun request() {
        viewModelScope.launch {
            try {
//                val auth = cph.getStudyAuth() ?: return@launch
                withContext(Dispatchers.IO) {
//                    val timetableList = RestApi.timetableEndpoint.getTimetableList(auth.group, auth.major, auth.studyYear).map { Timetable.from(it) }
                    val timetableList = getAllTimetables()
                    setWeekOverviewData(timetableList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                val test = true
            }
        }
    }

    private fun setWeekOverviewData(timetables: List<Timetable>) {
        val timetableList = ArrayList<Timetable>()
        timetables.forEach { timetable ->
            timetable.lessonDays.forEach {
                val date = it.toDate("MM-dd-yyyy")
                if(date!=null) {
                    val targetCalender = Calendar.getInstance()
                    targetCalender.time = date
                    if(calenderType == CALENDAR_CURRENT_WEEK && targetCalender.get(Calendar.WEEK_OF_YEAR)==currentWeek && targetCalender.get(
                            Calendar.YEAR)==currentYear) {
                        timetableList.add(timetable)
                    } else if (calenderType == CALENDAR_NEXT_WEEK && targetCalender.get(Calendar.WEEK_OF_YEAR)==currentWeek+1 && targetCalender.get(
                            Calendar.YEAR)==currentYear) {
                        timetableList.add(timetable)
                    }
                }
            }
        }
        items.clear()
        items.addAll(timetableList)
    }
}