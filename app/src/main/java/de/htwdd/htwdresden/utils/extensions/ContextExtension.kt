package de.htwdd.htwdresden.utils.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context?.toast(message: String, length: Int = Toast.LENGTH_SHORT) = this?.let { Toast.makeText(this, message, length).show() }

fun Context?.toast(@StringRes messageId: Int, length: Int = Toast.LENGTH_SHORT) = this?.let { Toast.makeText(this, messageId, length).show() }