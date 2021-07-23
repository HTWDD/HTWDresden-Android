package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.click
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals
import io.realm.OrderedCollectionChangeSet

//-------------------------------------------------------------------------------------------------- Types
typealias Overviews             = ArrayList<Overviewable>
typealias Timetables            = ArrayList<Identifiable<Modelable>>
typealias Grades                = ArrayList<Gradable>
typealias Exams                 = ArrayList<Examable>
typealias Occupancies           = ArrayList<RoomOccupancable>
typealias DetailOccupancies     = ArrayList<DetailRoomOccupancable>
typealias Canteens              = ArrayList<Canteenable>
typealias Meals                 = ArrayList<Mealable>
typealias CampusPlans           = ArrayList<CampusPlanable>
typealias Managements           = ArrayList<Managementable>

//-------------------------------------------------------------------------------------------------- Generic Recycler
abstract class GenericAdapter<T: Identifiable<K>, K: Modelable>(private val items: ArrayList<T>):
    RecyclerView.Adapter<GenericAdapter<T, K>.ViewHolder<T, K>>() {

    private var clickClosure: ((item: T) -> Unit)? = null
    private var emptyClosure: (isEmpty: Boolean) -> Unit = {}
    private var itemsLoadedClosure: () -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder<T, K>(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), viewType, parent, false))

    override fun onBindViewHolder(holder: ViewHolder<T, K>, position: Int) = getItem(position).let {
        holder.run {
            afterBind { item, view, position ->
                if (clickClosure != null) {
                    view.click {
                        clickClosure?.invoke(item)
                    }
                }
                this@GenericAdapter.afterBind(item, view, position)
            }
            bind(it)
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).viewType

    override fun getItemCount() = items.size

    private fun getItem(position: Int) = items[position]

    fun update(items: ArrayList<T>) {
        emptyClosure(items.isEmpty())
        if (!(items contentDeepEquals this.items)) {
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()
            itemsLoadedClosure.invoke()
        }
    }

    fun insert(range: OrderedCollectionChangeSet.Range){
        emptyClosure(items.isEmpty())
        notifyItemRangeInserted(range.startIndex, range.length)
    }

    fun modify(range: OrderedCollectionChangeSet.Range) {
        emptyClosure(items.isEmpty())
        notifyItemRangeChanged(range.startIndex, range.length)
    }

    fun remove(range: OrderedCollectionChangeSet.Range) {
        emptyClosure(items.isEmpty())
        notifyItemRangeRemoved(range.startIndex, range.length)
    }

    fun onItemsLoaded(callback: () -> Unit) {
        itemsLoadedClosure = callback
    }

    fun onItemClick(callback: (item: T) -> Unit) {
        clickClosure = callback
    }

    fun onEmpty(callback: (isEmpty: Boolean) -> Unit) {
        emptyClosure = callback
    }

    internal fun getMovementFlags(position: Int) = try { getItem(position).movementFlags } catch (e: Exception) { 0 }

    internal fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
        getItem(viewHolder.adapterPosition).let { item ->
            when (swipeDirection) {
                LEFT -> item.onLeftSwiped { item.leftAction() }
                RIGHT -> item.onRightSwiped { item.rightAction() }
            }
        }
    }

    abstract fun afterBind(item: T, view: View, position: Int)

    inner class ViewHolder<T: Identifiable<K>, K: Modelable>(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        private var afterBindClosure: (item: T, view: View, position: Int) -> Unit = { _, _, _ -> }

        fun bind(item: T) {
            item.bindings.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
            afterBindClosure(item, itemView, adapterPosition)
        }

        fun afterBind(callback: (item: T, view: View, position: Int) -> Unit) {
            afterBindClosure = callback
        }
    }
}