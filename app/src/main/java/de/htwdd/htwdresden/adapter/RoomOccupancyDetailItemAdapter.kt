package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.DetailRoomOccupancable
import de.htwdd.htwdresden.ui.models.DetailRoomOccupancableModels
import de.htwdd.htwdresden.ui.models.DetailRoomOccupancyItem
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals

typealias DetailOccupancies           = ArrayList<DetailRoomOccupancable>
typealias DetailOccupancyBindables    = ArrayList<Pair<Int, DetailRoomOccupancableModels>>

class RoomOccupancyDetailItemAdapter(private val items: DetailOccupancies): RecyclerView.Adapter<RoomOccupancyDetailItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = item(position).let { holder.bind(it.bindingTypes(), it) }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun update(items: DetailOccupancies) {
        if (!(this.items contentDeepEquals items)) {
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bindingTypes: DetailOccupancyBindables, item: DetailRoomOccupancable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
            when (item) {
                is DetailRoomOccupancyItem -> {
                    item.addRooms(binding.root.findViewById(R.id.llRooms))
                }
                else -> {}
            }
        }
    }
}