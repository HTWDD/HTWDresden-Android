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