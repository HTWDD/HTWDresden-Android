package de.htwdd.htwdresden.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import de.htwdd.htwdresden.R

class TimestampAdapter(val context: Context) : BaseAdapter() {

    override fun getCount() = 15

    override fun getItem(p0: Int) = null

    override fun getItemId(p0: Int) = p0.toLong()

    private val timestamps = context.getStringArray(R.array.hours)

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflatedView: View
        if (position == 0) {
            inflatedView = LayoutInflater.from(context)
                .inflate(R.layout.empty_grid_item, parent, false)
        } else {
            inflatedView = LayoutInflater.from(context)
                .inflate(R.layout.timestamp_grid_item, parent, false)
            TimestampViewHolder(inflatedView, position)
        }

        return inflatedView
    }

    private inner class TimestampViewHolder(view: View, position: Int) {
        private val title: TextView = view.findViewById(R.id.timestampGridItem)

        init {
            title.text = timestamps[position-1]
        }
    }
}