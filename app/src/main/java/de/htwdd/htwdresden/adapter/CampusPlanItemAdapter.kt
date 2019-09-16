package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.CampusPlanItem
import de.htwdd.htwdresden.ui.models.CampusPlanModels
import de.htwdd.htwdresden.ui.models.CampusPlanable
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals

typealias CampusPlans           = ArrayList<CampusPlanable>
typealias CampusPlanBindables   = ArrayList<Pair<Int, CampusPlanModels>>

class CampusPlanItemAdapter(private val items: CampusPlans): RecyclerView.Adapter<CampusPlanItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = item(position).let { item -> holder.bind(item.bindingTypes(), item) }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun update(items: CampusPlans) {
        if (!(items contentDeepEquals this.items)) {
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        internal fun bind(bindingTypes: CampusPlanBindables, item: CampusPlanable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
            when (item) {
                is CampusPlanItem -> {
                    item.addBuildings(binding.root.findViewById(R.id.llBuildings))
                }
                else -> {}
            }
        }
    }
}