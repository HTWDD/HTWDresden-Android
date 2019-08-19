package de.htwdd.htwdresden.utils.extensions

import androidx.fragment.app.Fragment
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