package de.htwdd.htwdresden.adapter

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.RoomOccupancable
import de.htwdd.htwdresden.ui.models.RoomOccupancableModels
import de.htwdd.htwdresden.utils.extensions.click
import de.htwdd.htwdresden.utils.extensions.contentDeepEquals
import de.htwdd.htwdresden.utils.extensions.dp
import de.htwdd.htwdresden.utils.holders.ResourceHolder
import io.realm.OrderedCollectionChangeSet


typealias Occupancies           = ArrayList<RoomOccupancable>
typealias OccupancyBindables    = ArrayList<Pair<Int, RoomOccupancableModels>>

//-------------------------------------------------------------------------------------------------- Item Adapter
class RoomOccupancyItemAdapter(private val items: Occupancies): RecyclerView.Adapter<RoomOccupancyItemAdapter.ViewHolder>() {

    private var clickClosure: (item: RoomOccupancable) -> Unit = {}
    private var emptyItemsClosure: (isEmpty: Boolean) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = item(position).let {
        item -> holder.bind(item.bindingTypes(), item)
    }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun update(items: Occupancies) {
        emptyItemsClosure(items.isEmpty())
        if (!(this.items contentDeepEquals items)) {
            this.items.apply {
                clear()
                addAll(items)
            }
            notifyDataSetChanged()

        }
    }

    fun remove(range: OrderedCollectionChangeSet.Range) {
        emptyItemsClosure(items.isEmpty())
        notifyItemRangeRemoved(range.startIndex, range.length)
    }

    fun insert(range: OrderedCollectionChangeSet.Range){
        emptyItemsClosure(items.isEmpty())
        notifyItemRangeInserted(range.startIndex, range.length)
    }

    fun modify(range: OrderedCollectionChangeSet.Range) {
        emptyItemsClosure(items.isEmpty())
        notifyItemRangeChanged(range.startIndex, range.length)
    }

    fun addOnItemClickListener(callback: (item: RoomOccupancable) -> Unit) {
        clickClosure = callback
    }

    fun onEmpty(callback: (isEmpty: Boolean) -> Unit) {
        emptyItemsClosure = callback
    }

    internal fun getMovementFlags(position: Int) = try { item(position).getMovementFlags() } catch (e: Exception) { 0 }

    internal fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
       item(viewHolder.adapterPosition).let { item ->
           when (swipeDirection) {
               LEFT -> item.onLeftSwiped { item.removeFromDb() }
           }
       }
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        internal fun bind(bindingTypes: OccupancyBindables, item: RoomOccupancable) {
            bindingTypes.map { binding.setVariable(it.first, it.second) }
            binding.executePendingBindings()
            itemView.click { clickClosure(item) }
        }
    }
}

//-------------------------------------------------------------------------------------------------- Swipe gesture
class RoomOccupancySwipeController(private val adapter: RoomOccupancyItemAdapter): ItemTouchHelper.Callback() {
    private val icon: Drawable? by lazy {
        ResourceHolder.instance.getDrawable(R.drawable.ic_garbage)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) = makeMovementFlags(0, adapter.getMovementFlags(viewHolder.adapterPosition))

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = adapter.onSwiped(viewHolder, direction)

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (dX < 0) {
            viewHolder.itemView.also {
                val iconSize = 32.dp
                val padding = 8.dp
                val viewMid = (it.bottom - it.top) / 2
                icon?.apply {
                    setBounds(it.right - (iconSize + padding), it.top + (viewMid - iconSize / 2), it.right - padding, it.bottom - (viewMid - iconSize / 2))
                }
            }
            icon?.draw(c)
        }
    }
}