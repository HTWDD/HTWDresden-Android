package de.htwdd.htwdresden.utils.extensions

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