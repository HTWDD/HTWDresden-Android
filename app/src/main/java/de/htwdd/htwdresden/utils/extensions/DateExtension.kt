package de.htwdd.htwdresden.utils.extensions

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.WEEK_OF_YEAR
import kotlin.collections.ArrayList

//-------------------------------------------------------------------------------------------------- Date
fun Date.format(pattern: String): String = SimpleDateFormat(pattern, Locale.getDefault()).format(this)

val Date.week: Int
    get() {
        val calendar = GregorianCalendar.getInstance(Locale.GERMAN)
        calendar.time = this
        return calendar.get(WEEK_OF_YEAR)
    }

val Date.calendar: Calendar
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar
    }

//-------------------------------------------------------------------------------------------------- Calendar
val Calendar.datesOfCurrentWeek: ArrayList<Date>
    get() {
        val result = mutableListOf<Date>()
        val delta = -get(DAY_OF_WEEK) + 2
        add(DAY_OF_WEEK, delta)
        for (i in 0..4) {
            result.add(time)
            add(DAY_OF_WEEK, 1)
        }
        return result.toCollection(ArrayList())
    }

val Calendar.datesOfNextWeek: ArrayList<Date>
    get() {
        val result = mutableListOf<Date>()
        val delta = -get(DAY_OF_WEEK) + 2
        add(DAY_OF_WEEK, delta + 7)
        for (i in 0..4) {
            result.add(time)
            add(DAY_OF_WEEK, 1)
        }
        return result.toCollection(ArrayList())
    }

val Date.timeInDpForCalendar: Int
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        val dp = calendar.get(Calendar.MINUTE) + 60 * (calendar.get(Calendar.HOUR_OF_DAY) - 7)
        return if (dp < 0) 0 else dp
    }

val Calendar.getStartDateForLesson: Date
    get() {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        return time
    }

val Calendar.getEndDateForLesson: Date
    get() {
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        return time
    }

fun Calendar.addTime(date: Date): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    add(Calendar.HOUR_OF_DAY, date.calendar[Calendar.HOUR_OF_DAY])
    add(Calendar.MINUTE, date.calendar[Calendar.MINUTE])
    return this
}

fun Date.getDaysBetween(endDate: Date): ArrayList<Date> {
    val dates = ArrayList<Date>()
    val startCalendar = calendar
    while (startCalendar <= endDate.calendar) {
        dates.add(startCalendar.time)
        startCalendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return dates
}