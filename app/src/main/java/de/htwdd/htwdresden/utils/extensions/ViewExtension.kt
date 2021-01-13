package de.htwdd.htwdresden.utils.extensions

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridView
import androidx.annotation.AnimRes
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import de.htwdd.htwdresden.R
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import kotlin.random.Random

fun View.show() {
    if (visibility != VISIBLE) {
        visibility = VISIBLE
    }
}

fun View.hide(condition: Boolean = true) {
    if (condition && visibility != GONE) {
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

fun CardView.setColorForLessonType(lessonType: String) {
    val color = with(lessonType) {
        when {
            startsWith('V', true) -> R.color.red_300
            startsWith('Ü', true) -> R.color.green_300
            startsWith('B', true) -> R.color.blue_grey_300
            else -> R.color.indigo_400
        }
    }
    setCardBackgroundColor(ContextCompat.getColor(context, color))
}

fun ConstraintLayout.configureForBreak(rowNumber: Int) {
    layoutParams = when(rowNumber) {
        6 -> ConstraintLayout.LayoutParams(GridView.AUTO_FIT, 120)
        12, 14 -> ConstraintLayout.LayoutParams(GridView.AUTO_FIT, 30)
        else -> ConstraintLayout.LayoutParams(GridView.AUTO_FIT, 60)
    }
}

inline fun <reified T: View> T.click(crossinline  block: (T) -> Unit) = setOnClickListener { block(it as T) }

inline fun View?.playAnimation(@AnimRes id: Int, crossinline block: () -> Unit = {}) = this?.let {
    val animation = AnimationUtils.loadAnimation(context, id)
    animation.setAnimationListener(object: Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {}
        override fun onAnimationEnd(p0: Animation?) { block() }
        override fun onAnimationStart(p0: Animation?) {}
    })
    startAnimation(animation)
}

//-------------------------------------------------------------------------------------------------- Konfetti
fun KonfettiView.emit() {
    build()
        .addColors(resources.getStringArray(R.array.timetableColors).map { it.toColor() })
        .setDirection(0.0, 359.0)
        .setSpeed(Random.nextDouble(0.5, 1.0).toFloat(), Random.nextDouble(3.5, 5.0).toFloat())
        .setFadeOutEnabled(true)
        .setTimeToLive(Random.nextLong(800L, 1500L))
        .addShapes(Shape.RECT, Shape.CIRCLE)
        .addSizes(Size(Random.nextInt(6, 10)))
        .setPosition(-50f, width + 50f, -50f, -50f)
        .streamFor(Random.nextInt(150, 300), Random.nextLong(1800L, 4200L))
}