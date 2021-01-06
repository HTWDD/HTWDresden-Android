package de.htwdd.htwdresden.utils.extensions

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import kotlin.math.floor
import kotlin.math.roundToInt


inline val Int.dp: Int
    get() = this.toFloat().dp.roundToInt()

inline val Float.dp: Float
    get() = this.toDouble().dp.toFloat()

inline val Double.dp: Double
    get() = Resources.getSystem().displayMetrics.density * this



fun Int.toRowNumber() = floor(this.toDouble() / 5).toInt()
