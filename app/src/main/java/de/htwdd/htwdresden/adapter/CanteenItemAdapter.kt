package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.ui.models.Canteenable
import de.htwdd.htwdresden.ui.models.CanteenableModel
import de.htwdd.htwdresden.utils.extensions.click
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals

typealias Canteens          = ArrayList<Canteenable>
typealias CanteensBindables = ArrayList<Pair<Int, CanteenableModel>>

class CanteenItemAdapter(private val items: Canteens): RecyclerView.Adapter<CanteenItemAdapter.ViewHolder>() {

    private var clickClosure: (item: Canteenable) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) = item(position).let { item -> holder.bind(item.bindingTypes(), item) }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun update(items: Canteens) {
        if (!(items contentDeepEquals this.items)) {
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()
        }
    }

    fun onItemClick(callback: (item: Canteenable) -> Unit) {
        clickClosure = callback
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        internal fun bind(bindingTypes: CanteensBindables, item: Canteenable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
            itemView.click { clickClosure(item) }
        }
    }
}