package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.ui.models.Examable
import de.htwdd.htwdresden.ui.models.ExamableModels
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals

typealias Exams         = ArrayList<Examable>
typealias ExamBindables = ArrayList<Pair<Int, ExamableModels>>

class ExamItemAdapter(private val items: Exams): RecyclerView.Adapter<ExamItemAdapter.ViewHolder>() {

    private var emptyItemsClosure: (isEmpty: Boolean) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = item(position).let {
            item -> holder.bind(item.bindingTypes(), item)
    }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun onEmpty(callback: (isEmpty: Boolean) -> Unit) {
        emptyItemsClosure = callback
    }

    fun update(items: Exams) {
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
        internal fun bind(bindingTypes: ExamBindables, item: Examable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
        }

    }
}