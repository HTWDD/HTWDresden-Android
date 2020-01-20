package de.htwdd.htwdresden.utils.holders

import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import de.htwdd.htwdresden.utils.extensions.error
import java.io.File
import kotlin.properties.Delegates

class ResourceHolder private constructor() {

    private var ctx: Context by Delegates.notNull()
    private object Holder { val INSTANCE = ResourceHolder() }

    private val packageManager by lazy { ctx.packageManager }

    val versionName: String
        get() {
            return try {
                packageManager.getPackageInfo(ctx.packageName, 0).versionName
            } catch (e: Exception) {
                error(e)
                "0.0.0"
            }
        }

    val versionCode: String
        get() {
            return try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageManager.getPackageInfo(ctx.packageName, 0).longVersionCode.toString()
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(ctx.packageName, 0).versionCode.toString()
                }
            } catch (e: Exception) {
                error(e)
                "0"
            }
        }

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