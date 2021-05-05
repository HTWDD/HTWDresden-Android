package de.htwdd.htwdresden.adapter

import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.custom.LessonView
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.ui.views.fragments.ClickListener
import de.htwdd.htwdresden.utils.extensions.convertDpToPixel
import de.htwdd.htwdresden.utils.extensions.getColorForLessonType
import de.htwdd.htwdresden.utils.extensions.timeInDpForCalendar
import kotlin.math.floor

object DataBindingAdapters {

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageResource(imageView: ImageView, resource: Int) = imageView.setImageResource(resource)
}

private fun drawLessons(layout: RelativeLayout, listener: ClickListener, root: Timetable, rootConflicts: ArrayList<Timetable>, conflictPosition: Int = -1, rootWidth: Int = 0) {
    val grid = layout.findViewById<GridView>(R.id.timetableCalendar)
    grid?.columnWidth ?: return

    val lessonView = LessonView(layout.context, null, 0, root)
    lessonView.setOnClickListener {
        listener.onLessonClick(root)
    }

    val start = root.beginTime.timeInDpForCalendar
    val end = root.endTime.timeInDpForCalendar

    val width =
        if (rootConflicts.size > 2) grid.columnWidth / 3 else if(rootConflicts.isEmpty()) rootWidth else grid.columnWidth / (rootConflicts.size+1)
    val defaultLessonHeight = 60f
    val divider =  layout.context.resources.getDimension(R.dimen.calendar_divider)
    val defaultTopMargin = layout.context.resources.getDimension(R.dimen.calendar_header_height_plus_space)
    val conflictMargin = if(conflictPosition>=0) width*conflictPosition else 0
    val marginStart = ((root.day-1)*grid.columnWidth + (root.day-1) * divider) + conflictMargin
    val lessonDuration = (end-start).toFloat()

    val params = RelativeLayout.LayoutParams(width,
        layout.context.convertDpToPixel(lessonDuration).toInt() + (divider * floor(lessonDuration/defaultLessonHeight)).toInt()
    )

    params.topMargin = (defaultTopMargin + layout.context.convertDpToPixel(start.toFloat()) +divider * floor(start/defaultLessonHeight)).toInt()
    params.marginStart = marginStart.toInt()
    lessonView.layoutParams = params

    lessonView.id = View.generateViewId()
    layout.addView(lessonView)
    rootConflicts.forEachIndexed { index, timetable ->
        if(index < 2) drawLessons(layout, listener, timetable, ArrayList(), index+1, width)
    }
}

@BindingAdapter(value = ["lessons", "listener"], requireAll = true)
@Suppress("UNCHECKED_CAST")
fun addLessonsToLayout(layout: RelativeLayout, items: List<Timetable>, listener: ClickListener) {
    if(layout.childCount>1) {
        layout.removeViews(1,layout.childCount-1)
        layout.requestLayout()
        layout.invalidate()
    }
    val days = ArrayList<Pair<Long, List<Timetable>>>()
    val itemsWithoutConflicts = items.toCollection(ArrayList())
    for(i in 1..5L) {
        days.add(Pair(i, items.filter { it.day==i }))
    }
    items.forEach { root ->
        val rootConflicts = items.filter{ it.id != root.id && it.day == root.day }.filter { (it.beginTime.after(root.beginTime) && it.beginTime.before(root.endTime)) || it.beginTime == root.beginTime}.toCollection(ArrayList())
        if(rootConflicts.isNotEmpty()) {
            if(itemsWithoutConflicts.contains(root))
                drawLessons(layout,listener,root, rootConflicts)
            itemsWithoutConflicts.remove(root)
            itemsWithoutConflicts.removeAll(rootConflicts)
        }
    }

    itemsWithoutConflicts.forEach {timetable ->
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
        if (lessonDuration > 0) layout.addView(lessonView)
    }
    layout.invalidate()
}

@BindingAdapter("app:goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("app:setBackgroundTint")
fun setBackgroundTint(view: View, color: Int) {
    view.setBackgroundColor(ContextCompat.getColor(view.context, color))
}


@BindingAdapter("app:isEnabled")
fun enableSwipeRefresh(view: SwipeRefreshLayout, isEnabled: Boolean) {
    view.isEnabled = isEnabled
}