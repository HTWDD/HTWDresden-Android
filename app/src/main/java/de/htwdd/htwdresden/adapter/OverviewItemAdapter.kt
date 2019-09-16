package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.ui.models.Overviewable
import de.htwdd.htwdresden.ui.models.OverviewableModel
import de.htwdd.htwdresden.utils.extensions.click
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals

typealias Overviews         = ArrayList<Overviewable>
typealias OverviewBindables = ArrayList<Pair<Int, OverviewableModel>>

class OverviewItemAdapter(private val items: Overviews): RecyclerView.Adapter<OverviewItemAdapter.ViewHolder>() {

    private var clickClosure: (item: Overviewable) -> Unit = {}

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

    fun update(items: Overviews) {

        if (!(items contentDeepEquals this.items)) {
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()
        }
    }

    fun onClick(callback: (item: Overviewable) -> Unit) {
        clickClosure = callback
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bindingTypes: OverviewBindables, item: Overviewable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
            itemView.click { clickClosure(item) }
        }
    }
}