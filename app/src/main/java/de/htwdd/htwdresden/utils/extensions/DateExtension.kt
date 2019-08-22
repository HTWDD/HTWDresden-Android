package de.htwdd.htwdresden.utils.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String): String = SimpleDateFormat(pattern, Locale.US).format(this)