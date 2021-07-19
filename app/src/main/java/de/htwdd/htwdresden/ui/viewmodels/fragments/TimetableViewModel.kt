package de.htwdd.htwdresden.ui.viewmodels.fragments

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.CalendarContract
import androidx.lifecycle.*
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import de.htwdd.htwdresden.utils.holders.StringHolder
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TimetableViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    private val _electives = mutableListOf<Timetable>()
    private val _searchTerm = MutableLiveData<String>("")
    private val _filteredElectives = Transformations.switchMap(_searchTerm){ string->
        MutableLiveData(filterElectivesBySearch(string))
    }
    private val _searchVisible = MutableLiveData(false)
    private val _showError = MutableLiveData(false)

    val filteredElectives: LiveData<List<OverviewScheduleItem>?> = _filteredElectives
    val searchTerm: LiveData<String> = _searchTerm
    val searchVisible: LiveData<Boolean> = _searchVisible
    val showError: LiveData<Boolean> = _showError

    private val sh: StringHolder by lazy { StringHolder.instance }

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
            val hiddenEventsIds = getHiddenTimetables()
            deleteAllIfNotCreatedByUserOrElective()
            timetableList.forEach {
                if(hiddenEventsIds.contains(it.id)) {
                    it.isHidden = true
                }
                TimetableRealm().update(it) {}
            }
            val sortedKeySet = mutableSetOf<String>()
            val sortedValueSet = mutableSetOf<Timetable>()
            getNotHiddenTimetables().groupBy { it.lessonDays }.apply {
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
        val d = date.toDate("MM-dd-yyyy")
        return TimetableHeaderItem(d?.format("EEEE") ?: "", d ?: Date())
    }

    fun exportCalendar(contentResolver: ContentResolver, index: Int, calendarId: Long) {
        val timetables = getNotHiddenTimetables().toCollection(ArrayList())
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

    suspend fun getElectiveTimetables() = withContext(Dispatchers.IO) {
        RestApi.timetableEndpoint.getAllTimetable()
    }


    fun loadElectiveTimetables(){
        viewModelScope.launch {
            val timetables = getElectiveTimetables().map { Timetable.from(it) }.filter { it.type.isElective()}
            if(timetables.isEmpty()) {
                delay(1000)
                _showError.value = true
            } else {
                _electives.clear()
                _electives.apply { addAll(timetables.sortedBy { it.name }.distinct() ) }
                _searchTerm.value = searchTerm.value
            }
        }
    }

    fun filterElectivesBySearch(string: String): List<OverviewScheduleItem>{
        val result = _electives.filter {
            it.name.toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)) ||
                    it.day.convertDayToString(sh).toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT)) ||
                    it.studiumIntegrale && "Studium Integrale".contains(string.toLowerCase(Locale.ROOT)) ||
                    it.professor?.toLowerCase(Locale.ROOT)?.contains(string.toLowerCase(Locale.ROOT)) == true
        }
        return result.map {OverviewScheduleItem(it, true)}
    }


    fun resetShowError() {
        _showError.value = null
    }

    fun setSearchTerm(query: String?){
        _searchTerm.value = query
    }

}