package de.htwdd.htwdresden.utils.holders

import android.content.Context
import androidx.annotation.DrawableRes
import java.io.File
import kotlin.properties.Delegates

class ResourceHolder private constructor() {

    private var ctx: Context by Delegates.notNull()
    private object Holder { val INSTANCE = ResourceHolder() }

    companion object {
        val instance: ResourceHolder by lazy { Holder.INSTANCE }
        fun init(context: Context) {
            instance.ctx = context.applicationContext
        }
    }

    fun readJsonData(fromFile: String): String {
        var content = ""
        ctx.assets.open(fromFile).use {
            val size = it.available()
            val buffer = ByteArray(size)
            it.read(buffer)
            content = String(buffer)
        }
        return content
    }

    fun getCacheDirectory(): File = ctx.applicationContext.cacheDir

    fun getDrawable(@DrawableRes id: Int) = ctx.getDrawable(id)
}