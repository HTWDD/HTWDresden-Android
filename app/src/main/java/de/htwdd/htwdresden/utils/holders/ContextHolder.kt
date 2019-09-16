package de.htwdd.htwdresden.utils.holders

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.utils.extensions.toast
import kotlin.properties.Delegates

class ContextHolder private constructor() {

    private var ctx: Context by Delegates.notNull()
    private object Holder { val INSTANCE = ContextHolder() }

    companion object {
        val instance: ContextHolder by lazy { Holder.INSTANCE }
        fun init(context: Context) {
            instance.ctx = context
        }
    }

    fun openUrl(url: String?) = try {
        url?.let {
            ctx.startActivity(Intent(ACTION_VIEW, Uri.parse(it)).apply {
                flags = FLAG_ACTIVITY_NEW_TASK
            })
        }
    } catch (e: Exception) {
        ctx.toast(R.string.info_error)
    }

    fun packageManager(): PackageManager = ctx.packageManager

    fun startActivity(intent: Intent) = ctx.startActivity(intent)
}