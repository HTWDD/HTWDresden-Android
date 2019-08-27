package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.ui.models.TimetableHeaderItem
import de.htwdd.htwdresden.ui.models.TimetableModels
import de.htwdd.htwdresden.ui.models.Timetableable
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.runInUiThread
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

typealias Timetables            = ArrayList<Timetableable>
typealias TimetableBindables    = ArrayList<Pair<Int, TimetableModels>>

class TimetableItemAdapter(private val items: Timetables): RecyclerView.Adapter<TimetableItemAdapter.ViewHolder>() {

    private val disposable = CompositeDisposable()
    private var itemsLoadedClosure: () -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = item(position).let { item -> holder.bind(item.bindingTypes(), item) }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun update(items: Timetables) {
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
        internal fun bind(bindingTypes: TimetableBindables, item: Timetableable) {
            Observable
                .fromArray(bindingTypes)
                .runInThread()
                .map { bItem -> bItem.map { binding.setVariable(it.first, it.second) } }
                .runInUiThread()
                .subscribe { binding.executePendingBindings() }
                .addTo(disposable)
        }
    }
}