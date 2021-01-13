package de.htwdd.htwdresden.ui.views.fragments

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.StringHolder
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CalenderAddEventViewModel(private val lessonId: String) : ViewModel() {
    val lessonName = ObservableField<String>()
    val lessonTag = ObservableField<String>()
    val lessonProf = ObservableField<String>()
    val lessonType = ObservableField<String>()
    val lessonRoom = ObservableField<String>()
    val lessonRotation = ObservableField<String>()
    val lessonWeekDay = ObservableField<String>()
    val lessonDay = ObservableField(today)
    var lessonExactDay: Date? = null
    val lessonStart = ObservableField("08:00")
    var lessonDateStart: Date? = null
    val lessonEnd = ObservableField("09:00")
    var lessonDateEnd: Date? = null
    val lessonWeekDayVisible = ObservableField(true)
    val isEditable = ObservableField(true)
    var timetable: Timetable? = null

    val lessonNameError  = ObservableField<String?>()
    val lessonRotationError  = ObservableField<String?>()
    val lessonWeekDayError  = ObservableField<String?>()
    val errorViewVisible  = ObservableField(false)

    private val lessonDatePattern = "dd.MM.yyyy"

    private val sh: StringHolder by lazy { StringHolder.instance }
    private val lessonTimePattern = sh.getString(R.string.time_format)

    init {
        if (lessonId.isNotEmpty()) {
            viewModelScope.launch {
                timetable = getTimetableById(lessonId)
                timetable?.let {
                    lessonName.set(it.name)
                    lessonTag.set(it.lessonTag)
                    lessonProf.set(it.professor)
                    handleLessonType(it.type)

                    if (it.rooms.isNotEmpty()) {
                        lessonRoom.set(it.rooms[0])
                    }

                    setupWeekRotation(it.weekRotation)
                    handleLessonWeekDay(it.day)

                    lessonStart.set(it.beginTime.format(lessonTimePattern))
                    lessonEnd.set(it.endTime.format(lessonTimePattern))
                    lessonDateStart = it.beginTime
                    lessonDateEnd = it.endTime
                    if(it.weeksOnly.size==1) {
                        lessonWeekDayVisible.set(false)
                        lessonDay.set(it.exactDay?.format(lessonDatePattern))
                        lessonExactDay = it.exactDay
                    }
                }
            }
        } else {
            lessonDateStart = Calendar.getInstance().getStartDateForLesson
            lessonDateEnd = Calendar.getInstance().getEndDateForLesson
            lessonStart.set(lessonDateStart?.format(lessonTimePattern))
            lessonEnd.set(lessonDateEnd?.format(lessonTimePattern))
        }
        if(timetable?.createdByUser==false) {
            isEditable.set(false)
        }
    }

    private fun setupWeekRotation(weekRotation: String?) {
        if(weekRotation!=null) {
            lessonRotation.set(weekRotation)
        }
    }

    fun handleTimeChange(isStartTime: Boolean, calendar: Calendar) {
        val formattedTime = calendar.time.format(lessonTimePattern)
        if(isStartTime) {
            lessonStart.set(formattedTime)
            lessonDateStart = calendar.time
        } else {
            lessonEnd.set(formattedTime)
            lessonDateEnd = calendar.time
        }
    }

    fun handleDateChange(calendar: Calendar) {
        val formattedTime = calendar.time.format(lessonDatePattern)
        lessonDay.set(formattedTime)
    }

    fun handleWeekRotation(text: CharSequence, isOneTimeOption: Boolean) {
        lessonRotationError.set(null)
        lessonRotation.set(text.toString())
        if(isOneTimeOption) {
            lessonWeekDayVisible.set(false)
        } else {
            lessonWeekDayVisible.set(true)
        }
    }

    fun handleWeekDay(text: CharSequence) {
        lessonWeekDayError.set(null)
        lessonWeekDay.set(text.toString())
    }

    private fun handleLessonType(type: String) {
        lessonType.set(type.fullLessonType)
    }

    private fun handleLessonWeekDay(day: Long) {
        val days = sh.getStringArray(R.array.days)
        try {
            if(day!=0L) lessonWeekDay.set(days[day.toInt()-1])
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createLesson(navController: NavController) {
        val timetableTag = lessonTag.getOrEmpty()
        val timetableName = lessonName.getOrEmpty()
        val timetableType =  createLessonType()
        var timetableWeekDay = sh.getStringArray(R.array.days).indexOf(lessonWeekDay.get())+1.toLong()
        val timetableStartTime = lessonDateStart
        val timetableEndTime = lessonDateEnd
        val timetableWeeksOnly = createWeeksOnly()
        val timetableProfessor = lessonProf.getOrEmpty()
        val timetableRooms = createRoomList()
        val timetableLessonDays = Timetable.lessonDays(timetableWeekDay, timetableWeeksOnly)
        val timetableExactDay = lessonDay.get()?.toDate(lessonDatePattern)
        val timetableLessonRotation = lessonRotation.get()

        var errorExists = false
        val errorMessage = sh.getString(R.string.empty_field_error)

        if(timetableName.isEmpty()) {
            lessonNameError.setAndResetOld(errorMessage)
            errorExists = true
        }
        if(timetableLessonRotation.isNullOrBlank()) {
            lessonRotationError.set(errorMessage)
            errorExists = true
        }
        if(timetableWeeksOnly.isEmpty() &&  timetableWeekDay<1) {
            lessonWeekDayError.set(errorMessage)
            errorExists = true
        } else if(timetableWeeksOnly.size > 1 && timetableWeekDay < 1) {
            lessonWeekDayError.set(errorMessage)
            errorExists = true
        }

        if(errorExists) {
            errorViewVisible.set(true)
        } else {
            if(timetableWeeksOnly.size==1) {
                val dayOfWeek = timetableExactDay?.calendar?.get(Calendar.DAY_OF_WEEK) ?: 1
                timetableWeekDay = dayOfWeek-1.toLong()
            }
            if(timetable==null) {
                timetable = Timetable(UUID.randomUUID().toString(),null, timetableTag, timetableName,timetableType, timetableWeekDay,
                    timetableStartTime!!, timetableEndTime!!,0L, timetableWeeksOnly ,timetableProfessor, timetableRooms,"",
                    timetableLessonDays,true, timetableExactDay,timetableLessonRotation )
            } else {
                timetable?.apply {
                    lessonTag = timetableTag
                    name = timetableName
                    type = timetableType
                    day = timetableWeekDay
                    beginTime = timetableStartTime!!
                    endTime = timetableEndTime!!
                    weeksOnly = timetableWeeksOnly
                    professor = timetableProfessor
                    rooms = timetableRooms
                    lessonDays = timetableLessonDays
                    createdByUser = true
                    exactDay = timetableExactDay
                    weekRotation = timetableLessonRotation
                }
            }
            timetable?.let {
                TimetableRealm().update(it)
                navController.popBackStack()
            }
        }
    }

    fun removeEvent() {
        timetable?.let {
            deleteById(it.id)
        }
    }

    private fun createRoomList(): List<String> {
        val lessonRoom = lessonRoom.getOrEmpty()
        return if(lessonRoom.isBlank()) {
            emptyList()
        } else {
            listOf(lessonRoom)
        }
    }

    private fun createLessonType() : String {
        return when(lessonType.get()) {
            sh.getString(R.string.lecture) -> "v"
            sh.getString(R.string.excersise)-> "ü"
            sh.getString(R.string.practical)-> "p"
            sh.getString(R.string.block)-> "b"
            sh.getString(R.string.requested)-> "r"
            else -> sh.getString(R.string.unknown)
        }
    }

    private fun createWeeksOnly(): ArrayList<Long> {
        val weeksOfYear = Calendar.getInstance().getActualMaximum(Calendar.WEEK_OF_YEAR);
        val weeksOnly = arrayListOf<Long>()
        when (sh.getStringArray(R.array.lesson_week).indexOf(lessonRotation.get())) {
            0 -> {
                for(i in 1..weeksOfYear) {
                    weeksOnly.add(i.toLong())
                }
            }
            1 -> {
                for(i in 2..weeksOfYear step 2) {
                    weeksOnly.add(i.toLong())
                }
            }
            2 -> {
                for(i in 1..weeksOfYear step 2) {
                    weeksOnly.add(i.toLong())
                }
            }
            3 -> {
                lessonDay.get()?.toDate(lessonDatePattern)?.let {
                    weeksOnly.add(it.week.toLong())
                }
            }
            else -> emptyList<Long>()
        }
        return weeksOnly
    }
}