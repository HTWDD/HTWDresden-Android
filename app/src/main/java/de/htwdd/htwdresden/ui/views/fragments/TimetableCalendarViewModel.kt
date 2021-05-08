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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TimetableCalendarViewModel(private val calenderType: Int) : ViewModel() {

    val items = ObservableArrayList<Timetable>()

    fun setup() {
        request()
    }

    fun request() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    delay(1000)
                    val realm = Realm.getDefaultInstance()
                    try {
                        val list = realm.where(TimetableRealm::class.java).findAll()
                        val timetableList = list.map{TimetableRealm.toTimetable(it)}.filter { !it.isHidden }
                        setWeekOverviewData(timetableList)
                    } catch (e: Exception) {
                    } finally {
                        realm.close()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
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