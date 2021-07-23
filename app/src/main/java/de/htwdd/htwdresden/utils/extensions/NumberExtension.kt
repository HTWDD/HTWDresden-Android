package de.htwdd.htwdresden.utils.extensions

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.utils.holders.StringHolder
import kotlin.math.floor
import kotlin.math.roundToInt


inline val Int.dp: Int
    get() = this.toFloat().dp.roundToInt()

inline val Float.dp: Float
    get() = this.toDouble().dp.toFloat()

inline val Double.dp: Double
    get() = Resources.getSystem().displayMetrics.density * this



fun Int.toRowNumber() = floor(this.toDouble() / 5).toInt()

fun Long.convertDayToString(sh: StringHolder): String{
    return when (this) {
        1L -> sh.getString(R.string.monday)
        2L -> sh.getString(R.string.tuesday)
        3L -> sh.getString(R.string.wednesday)
        4L -> sh.getString(R.string.thursday)
        5L -> sh.getString(R.string.friday)
        6L -> sh.getString(R.string.saturday)
        7L -> sh.getString(R.string.sunday)
        else -> sh.getString(R.string.unknown)
    }
}