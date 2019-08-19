package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.ui.models.Examable
import de.htwdd.htwdresden.ui.models.ExamableModels
import de.htwdd.htwdresden.utils.extensions.debug
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.runInUiThread
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

typealias Exams = ArrayList<Examable>

class ExamItemAdapter(private val items: Exams): RecyclerView.Adapter<ExamItemAdapter.ViewHolder>() {

    // region - Properties
    private val disposable = CompositeDisposable()
    // endregion

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = item(position).let {
            item -> holder.bind(item.bindingTypes(), item)
    }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun update(items: Exams) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    // region ViewHolder
    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        internal fun bind(bindingItems: ArrayList<Pair<Int, ExamableModels>>, item: Examable) {
            Observable
                .fromArray(bindingItems)
                .debug()
                .runInThread()
                .map { bindingItem ->
                    bindingItem.map { binding.setVariable(it.first, it.second) }
                }
                .runInUiThread()
                .subscribe {
                    binding.executePendingBindings()
                }.addTo(disposable)
        }

    }
    // endregion

}