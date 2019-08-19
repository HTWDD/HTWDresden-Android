package de.htwdd.htwdresden.utils.holders

import android.content.Context
import androidx.annotation.StringRes
import kotlin.properties.Delegates

class StringHolder private constructor() {

    // region - Properties
    private var ctx: Context by Delegates.notNull()
    private object Holder { val INSTANCE = StringHolder() }
    // endregion

    companion object {
        val instance: StringHolder by lazy { Holder.INSTANCE }
        fun init(context: Context) {
            instance.ctx = context.applicationContext
        }
    }

    fun getString(@StringRes id: Int) = ctx.getString(id)

    fun getString(@StringRes id: Int, vararg params: Any) = ctx.getString(id, *params)
}