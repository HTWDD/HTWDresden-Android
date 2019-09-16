package de.htwdd.htwdresden.utils.extensions

import android.content.res.Resources
import kotlin.math.roundToInt

inline val Int.dp: Int
    get() = this.toFloat().dp.roundToInt()

inline val Float.dp: Float
    get() = this.toDouble().dp.toFloat()

inline val Double.dp: Double
    get() = Resources.getSystem().displayMetrics.density * this