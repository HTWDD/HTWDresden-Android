package de.htwdd.htwdresden.adapter

import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.children
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.custom.LessonView
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.ui.views.fragments.ClickListener
import de.htwdd.htwdresden.utils.extensions.convertDpToPixel
import de.htwdd.htwdresden.utils.extensions.timeInDpForCalendar
import kotlin.math.floor

object DataBindingAdapters {

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageResource(imageView: ImageView, resource: Int) = imageView.setImageResource(resource)
}

@BindingAdapter(value = ["lessons", "listener"], requireAll = true)
@Suppress("UNCHECKED_CAST")
fun addLessonsToLayout(layout: RelativeLayout, items: List<Timetable>, listener: ClickListener) {
    if(layout.childCount>1) {
        layout.removeViewsInLayout(1,layout.childCount-1)
        layout.invalidate()
    }
    items.forEach {timetable ->
        val grid = layout.findViewById<GridView>(R.id.timetableCalendar)
        grid?.columnWidth ?: return@forEach

        val lessonView = LessonView(layout.context, null, 0, timetable)
        lessonView.setOnClickListener {
            listener.onLessonClick(timetable)
        }

        val start = timetable.beginTime.timeInDpForCalendar
        val end = timetable.endTime.timeInDpForCalendar

        val defaultLessonHeight = 60f
        val divider =  layout.context.resources.getDimension(R.dimen.calendar_divider)
        val defaultTopMargin = layout.context.resources.getDimension(R.dimen.calendar_header_height_plus_space)
        val marginStart = (timetable.day-1)*grid.columnWidth + (timetable.day-1) * divider
        val lessonDuration = (end-start).toFloat()

        val params = RelativeLayout.LayoutParams(
            grid.columnWidth,
            layout.context.convertDpToPixel(lessonDuration).toInt() + (divider * floor(lessonDuration/defaultLessonHeight)).toInt()
        )

        params.topMargin = (defaultTopMargin + layout.context.convertDpToPixel(start.toFloat()) +divider * floor(start/defaultLessonHeight)).toInt()
        params.marginStart = marginStart.toInt()
        lessonView.layoutParams = params

        lessonView.id = View.generateViewId()
        layout.addView(lessonView)
    }
    layout.invalidate()
}

@BindingAdapter("app:goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("app:isEnabled")
fun enableSwipeRefresh(view: SwipeRefreshLayout, isEnabled: Boolean) {
    view.isEnabled = isEnabled
}