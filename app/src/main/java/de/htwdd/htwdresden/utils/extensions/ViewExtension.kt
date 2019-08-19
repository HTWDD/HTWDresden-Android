package de.htwdd.htwdresden.utils.extensions

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

// region - View
fun View.show() {
    if (visibility != VISIBLE) {
        visibility = VISIBLE
    }
}

fun View.hide() {
    if (visibility != GONE) {
        visibility = GONE
    }
}

fun View.toggle(): View {
    if (visibility == VISIBLE) {
        hide()
    } else {
        show()
    }

    return this
}

fun View.toggle(condition: Boolean): View {
    return this.apply {
        if (condition) {
            show()
        } else {
            hide()
        }
    }
}
// endregion