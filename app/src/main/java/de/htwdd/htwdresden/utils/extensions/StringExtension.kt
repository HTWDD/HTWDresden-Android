package de.htwdd.htwdresden.utils.extensions

import android.graphics.Color
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.utils.holders.StringHolder
import java.security.MessageDigest
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

fun String.toSHA256(): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(toByteArray())
    return digest.fold("", { str, byte -> str + "%02x".format(byte) })
}

private fun String.toMD5(): String {
    val digest = MessageDigest.getInstance("MD5").digest(toByteArray())
    return digest.fold("", { str, byte -> str + "%02x".format(byte) })
}

val String.uid: String
    get() = this.toMD5().mapIndexed { index, c ->
        if (index % 8 == 0 && index != 0) {
            "$c-"
        } else {
            "$c"
        }
    }.joinToString("")

fun String.toColor() = Color.parseColor(if (startsWith("#")) { this } else { "#$this" })

val String.fullLessonType: String
    get() {
        val stringHolder = StringHolder.instance
        return (with(this) {
            when {
                startsWith("v", true) -> stringHolder.getString(R.string.lecture)
                startsWith("ü", true) -> stringHolder.getString(R.string.excersise)
                startsWith("p", true) -> stringHolder.getString(R.string.practical)
                startsWith("b", true) -> stringHolder.getString(R.string.block)
                startsWith("r", true) -> stringHolder.getString(R.string.requested)
                else -> stringHolder.getString(R.string.unknown)
            }
        })
    }

fun String.isElective() = this.contains("w")

