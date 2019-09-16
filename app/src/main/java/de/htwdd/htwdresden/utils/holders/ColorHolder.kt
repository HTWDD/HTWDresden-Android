package de.htwdd.htwdresden.utils.holders

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

class ColorHolder private constructor() {

    private var ctx: Context by Delegates.notNull()
    private object Holder { val INSTACNE = ColorHolder() }

    companion object {
        val instance: ColorHolder by lazy { Holder.INSTACNE }
        fun init(context: Context) {
            instance.ctx = context
        }
    }

    fun getColor(@ColorRes id: Int, theme: Resources.Theme? = null) = ResourcesCompat.getColor(ctx.resources, id, theme)

}