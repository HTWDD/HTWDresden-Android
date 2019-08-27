package de.htwdd.htwdresden.utils.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import de.htwdd.htwdresden.utils.AutoDisposableUtil
import java.lang.ref.WeakReference

/**
 * Creates a Weak Reference for Fragments.
 * The Block-Code will only run, when weak ref. is not collected from gc.
 *
 * usage:
 *
 * weak { self: Fragment ->
 *  <b>code comes hier</b>
 * }
 */


fun <T: Fragment> T.weak(block: (fragment: T) -> Unit) {
    val weakReference = WeakReference(this)
    weakReference.get()?.let {
        block(it)
    }
}

private fun <T: Fragment> T.disposeBag(): AutoDisposableUtil {
    return AutoDisposableUtil().apply {
        bindTo(this@disposeBag.lifecycle)
    }
}

val <T: Fragment> T.disposeBag: AutoDisposableUtil
    get() = disposeBag()

inline fun <reified T: ViewModel> Fragment.getViewModel(): T = ViewModelProviders.of(this).get(T::class.java)