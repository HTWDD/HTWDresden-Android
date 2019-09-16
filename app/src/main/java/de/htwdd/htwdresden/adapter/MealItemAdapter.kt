package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.ui.models.Mealable
import de.htwdd.htwdresden.ui.models.MealableModel
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals

typealias Meals = ArrayList<Mealable>
typealias MealsBindables = ArrayList<Pair<Int, MealableModel>>

class MealItemAdapter(private val items: Meals): RecyclerView.Adapter<MealItemAdapter.ViewHolder>() {

    private var emptyItemsClosure: (isEmpty: Boolean) -> Unit = {}

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

    fun onEmpty(callback: (isEmpty: Boolean) -> Unit) {
        emptyItemsClosure = callback
    }

    fun update(items: Meals) {
        emptyItemsClosure(items.isEmpty())
        if (!(items contentDeepEquals this.items)) {
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        @Suppress("UNUSED_PARAMETER")
        fun bind(bindingTypes: MealsBindables, item: Mealable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
        }
    }
}