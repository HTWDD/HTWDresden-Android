package de.htwdd.htwdresden.utils.extensions

import java.util.*

fun <T: Any> T.TAG() = this::class.java.simpleName

inline fun <T> T.guard(block: T.() -> Unit): T {
    if (this == null) {
        block()
    }
    return this
}

fun <T: Any> T.verbose(message: Any?) = de.htwdd.htwdresden.utils.verbose(TAG(), message)

fun <T: Any> T.debug(message: Any?) = de.htwdd.htwdresden.utils.debug(TAG(), message)

fun <T: Any> T.info(message: Any?) = de.htwdd.htwdresden.utils.info(TAG(), message)

fun <T: Any> T.warn(message: Any?) = de.htwdd.htwdresden.utils.warn(TAG(), message)

fun <T: Any> T.error(message: Any?) = de.htwdd.htwdresden.utils.error(TAG(), message)

val Any.currentYear: Int
        get() {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.YEAR]
        }

val Any.currentWeek: Int
    get() {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.WEEK_OF_YEAR]
    }

fun Any.getDaysOfWeek(): Array<Date?> {
    val refCalendar = Calendar.getInstance()
    refCalendar.add(Calendar.DAY_OF_WEEK, Calendar.getInstance().firstDayOfWeek)
    val calendar = Calendar.getInstance()
    calendar.time =  refCalendar.time
    calendar[Calendar.DAY_OF_WEEK] = Calendar.getInstance().firstDayOfWeek
    val daysOfWeek = arrayOfNulls<Date>(5)
    for (i in 0..4) {
        daysOfWeek[i] = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return daysOfWeek
}

