package de.htwdd.htwdresden.utils.extensions

import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.StringRes

fun Context?.toast(message: String, length: Int = Toast.LENGTH_SHORT) = this?.let { Toast.makeText(this, message, length).show() }

fun Context?.toast(@StringRes messageId: Int, length: Int = Toast.LENGTH_SHORT) = this?.let { Toast.makeText(this, messageId, length).show() }

fun Context.convertDpToPixel(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi/ DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.convertPixelsToDp(px: Float): Float {
    return px / (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}