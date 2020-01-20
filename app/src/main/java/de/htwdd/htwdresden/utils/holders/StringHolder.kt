package de.htwdd.htwdresden.utils.holders

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import kotlin.properties.Delegates

class StringHolder private constructor() {

    private var ctx: Context by Delegates.notNull()
    private object Holder { val INSTANCE = StringHolder() }

    companion object {
        val instance: StringHolder by lazy { Holder.INSTANCE }
        fun init(context: Context) {
            instance.ctx = context.applicationContext
        }
    }

    fun getString(@StringRes id: Int) = ctx.getString(id)

    fun getString(@StringRes id: Int, vararg params: Any) = ctx.getString(id, *params)

    fun getStringArray(@ArrayRes id: Int) = ctx.resources.getStringArray(id)
}