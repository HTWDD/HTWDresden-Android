package de.htwdd.htwdresden.utils.holders

import android.content.Context
import kotlin.properties.Delegates

class AssetHolder private constructor() {

    private var ctx: Context by Delegates.notNull()
    private object Holder { val INSTANCE = AssetHolder() }

    companion object {
        val instance: AssetHolder by lazy { Holder.INSTANCE }
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
}