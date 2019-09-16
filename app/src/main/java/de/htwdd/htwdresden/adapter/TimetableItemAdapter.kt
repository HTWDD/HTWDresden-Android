package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.ui.models.TimetableModels
import de.htwdd.htwdresden.ui.models.Timetableable
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals

typealias Timetables            = ArrayList<Timetableable>
typealias TimetableBindables    = ArrayList<Pair<Int, TimetableModels>>

class TimetableItemAdapter(private val items: Timetables): RecyclerView.Adapter<TimetableItemAdapter.ViewHolder>() {

    private var itemsLoadedClosure: () -> Unit = {}
    private var emptyItemsClosure: (isEmpty: Boolean) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = item(position).let { item -> holder.bind(item.bindingTypes(), item) }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun onEmpty(callback: (isEmpty: Boolean) -> Unit) {
        emptyItemsClosure = callback
    }

    fun update(items: Timetables) {
        emptyItemsClosure(items.isEmpty())
        if (!(items contentDeepEquals this.items)) {
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()
            itemsLoadedClosure()
        }
    }

    fun onItemsLoaded(callback: () -> Unit) {
        itemsLoadedClosure = callback
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        @Suppress("UNUSED_PARAMETER")
        internal fun bind(bindingTypes: TimetableBindables, item: Timetableable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
        }
    }
}