package de.htwdd.htwdresden.utils

import android.util.Log
import de.htwdd.htwdresden.BuildConfig

// region - Properties
private val IS_DEBUG: Boolean = BuildConfig.DEBUG
// endregion

// region - Enum with Swag
sealed class LogLevel {
    object Verbose: LogLevel()
    object Debug: LogLevel()
    object Info: LogLevel()
    object Warn: LogLevel()
    object Error: LogLevel()
}
// endregion

private fun printLog(logLevel: LogLevel, tag: String, message: Any?) {
    if (IS_DEBUG) {
        val printableMessage = when (message) {
            is Collection<*> -> message.toTypedArray().joinToString(", ")
            else -> message.toString()
        }

        when (logLevel) {
            is LogLevel.Verbose -> Log.v(tag, printableMessage)
            is LogLevel.Debug   -> Log.d(tag, printableMessage)
            is LogLevel.Info    -> Log.i(tag, printableMessage)
            is LogLevel.Warn    -> Log.w(tag, printableMessage)
            is LogLevel.Error   -> Log.e(tag, printableMessage)
        }
    }
}


fun verbose(tag: String, message: Any?) = printLog(LogLevel.Verbose, tag, message)

fun debug(tag: String, message: Any?) = printLog(LogLevel.Debug, tag, message)

fun info(tag: String, message: Any?) = printLog(LogLevel.Info, tag, message)

fun warn(tag: String, message: Any?) = printLog(LogLevel.Warn, tag, message)

fun error(tag: String, message: Any?) = printLog(LogLevel.Error, tag, message)

fun error(exception: Throwable) {
    error(exception.localizedMessage?.toString() ?: exception.javaClass.simpleName , Log.getStackTraceString(exception))
}