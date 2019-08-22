package de.htwdd.htwdresden.utils.extensions

import java.text.SimpleDateFormat
import java.util.*

val String?.nullWhenEmpty: String?
    get() {
        this.guard { return null }
        return if (isNullOrBlank()) {
            null
        } else {
            this!!
        }
    }

fun String?.defaultWhenNull(default: String): String {
    this.nullWhenEmpty.guard { return default }
    return this!!
}

fun String.toDate(withFormat: String = "yyyy-MM-dd"): Date? {
    return try {
        val sdf = SimpleDateFormat(withFormat, Locale.US)
        sdf.parse(this)
    } catch (e: Exception) {
        error(e)
        null
    }
}