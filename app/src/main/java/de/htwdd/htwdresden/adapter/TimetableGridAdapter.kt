package de.htwdd.htwdresden.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.utils.extensions.getDaysOfWeek
import de.htwdd.htwdresden.utils.extensions.toRowNumber
import java.lang.Exception
import java.text.DateFormatSymbols
import java.util.*

class TimetableGridAdapter(val context: Context, val isCurrentWeek: Boolean, function: (timetable: Timetable) -> Unit) : BaseAdapter() {

    companion object {
        const val HEADER_ITEM = 0
        const val DEFAULT_ITEM = 1
    }

    override fun getCount() = 145

    override fun getItem(p0: Int) = null

    override fun getItemId(p0: Int) = p0.toLong()

    override fun getItemViewType(position: Int): Int {
        return when {
            position.toRowNumber() == 0 -> HEADER_ITEM
            else -> DEFAULT_ITEM
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
            else -> {
                inflatedView = LayoutInflater.from(context)
                    .inflate(R.layout.default_grid_item, parent, false)
//                inflatedView.setOnClickListener {
//
//                }
            }
        }

        return inflatedView
    }

    private inner class HeaderViewHolder(view: View, position: Int) {
        private val title: TextView = view.findViewById(R.id.timetableHeaderItemTitle)
        private val day: TextView = view.findViewById(R.id.timetableHeaderItemDay)

        init {
            try {
                title.text = DateFormatSymbols.getInstance().shortWeekdays[position + 2]
                val days = getDaysOfWeek()
                days[position]?.let {
                    Calendar.getInstance().apply {
                        time = it
                        day.text = get(Calendar.DAY_OF_MONTH).toString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}