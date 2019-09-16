package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.Gradable
import de.htwdd.htwdresden.ui.models.GradableModel
import de.htwdd.htwdresden.ui.models.GradeItem
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals

typealias Grades            = ArrayList<Gradable>
typealias GradesBindables   = ArrayList<Pair<Int, GradableModel>>

class GradeItemAdapter(private val items: Grades): RecyclerView.Adapter<GradeItemAdapter.ViewHolder>() {

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

    fun update(items: Grades) {
        emptyItemsClosure(items.isEmpty())
        if (!(items contentDeepEquals this.items)){
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bindingTypes: GradesBindables, item: Gradable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()

            when (item) {
                is GradeItem -> {
                    item.setChevron(binding.root.findViewById(R.id.ivChevron))
                    item.onToggle {
                        notifyItemChanged(adapterPosition)
                    }
                }
            }
        }
    }
}