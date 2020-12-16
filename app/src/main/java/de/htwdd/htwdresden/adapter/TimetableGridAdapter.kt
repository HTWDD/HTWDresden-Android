package de.htwdd.htwdresden.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.ui.models.getLessonNumber
import de.htwdd.htwdresden.utils.ViewUtils
import de.htwdd.htwdresden.utils.extensions.calendar
import de.htwdd.htwdresden.utils.extensions.configureForBreak
import de.htwdd.htwdresden.utils.extensions.toDate
import de.htwdd.htwdresden.utils.extensions.toRowNumber
import java.text.DateFormatSymbols
import java.util.*

class TimetableGridAdapter(val context: Context, val items: ArrayList<Timetable>) : BaseAdapter() {

    companion object {
        const val HEADER_ITEM = 0
        const val TIMESTAMP_ITEM = 1
        const val LESSON_ITEM = 2
        const val BREAK_ITEM = 3
    }

    override fun getCount() = 96

    override fun getItem(p0: Int) = null

    override fun getItemId(p0: Int) = p0.toLong()

    override fun getItemViewType(position: Int): Int {
        return when {
            position.toRowNumber() == 0 -> HEADER_ITEM
            position.toRowNumber() % 2 == 0 -> BREAK_ITEM
            position % 6 == 0 -> TIMESTAMP_ITEM
            else -> LESSON_ITEM
        }
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflatedView: View
        when (getItemViewType(position)) {
            HEADER_ITEM -> {
                inflatedView = LayoutInflater.from(context)
                    .inflate(R.layout.timetable_grid_header_item, parent, false)
                HeaderViewHolder(inflatedView, position)
            }
            BREAK_ITEM -> {
                inflatedView = LayoutInflater.from(context)
                    .inflate(R.layout.timetable_grid_break_item, parent, false)
                BreakViewHolder(inflatedView, position)
            }
            TIMESTAMP_ITEM -> {
                inflatedView = LayoutInflater.from(context)
                    .inflate(R.layout.timetable_grid_timestamp_item, parent, false)
                TimestampViewHolder(inflatedView, position)
            }
            else -> {
                inflatedView = LayoutInflater.from(context)
                    .inflate(R.layout.timetable_grid_lesson_item, parent, false)
                LessonViewHolder(inflatedView, position)
            }
        }

        return inflatedView
    }

    private inner class LessonViewHolder(view: View, position: Int) {
        val type: TextView = view.findViewById(R.id.timetableType)
        val tag: TextView = view.findViewById(R.id.timetableTag)
        val room: TextView = view.findViewById(R.id.timetableRoom)
        val more: TextView = view.findViewById(R.id.timetableMoreLessons)
        val kw: TextView = view.findViewById(R.id.timetableOnlyKW)
        val lessonItemRoot: ConstraintLayout = view.findViewById(R.id.lessonItemRoot)
        val lessonItemContainer: LinearLayout = view.findViewById(R.id.lessonItemContainer)

        //7,8,9,10,11 for Monday to Friday

        init {
            val lessonsForThisDay = items.filter { it.day == position.toLong() % 6 }
            val lesson = lessonsForThisDay.find { it.getLessonNumber() == ((position.toRowNumber() + 1) / 2) }
            if (lesson != null) {
                tag.text = lesson.lessonTag
                type.text = lesson.type
                room.text = lesson.rooms.firstOrNull() ?: ""
            }
        }
    }

    private inner class BreakViewHolder(view: View, position: Int) {
        private val breakItemRoot = view.findViewById<ConstraintLayout>(R.id.breakItemRoot)
        init {
            breakItemRoot.configureForBreak(position.toRowNumber())
        }
    }

    private inner class HeaderViewHolder(view: View, position: Int) {
        private val title: TextView = view.findViewById(R.id.timetableHeaderItemTitle)

        init {
            title.text =
                if (position != 0) DateFormatSymbols.getInstance().shortWeekdays[position + 1] else ""
        }
    }

    private inner class TimestampViewHolder(view: View, position: Int) {
        private val beginTime = view.findViewById<TextView>(R.id.timestampBeginTime)
        private val endTime = view.findViewById<TextView>(R.id.timestampEndTime)

        init {
            ViewUtils.setBeginAndEndTime(beginTime, endTime, position.toRowNumber(), context)
        }
    }

}