package de.htwdd.htwdresden.adapter

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.dp
import de.htwdd.htwdresden.utils.holders.ResourceHolder

//-------------------------------------------------------------------------------------------------- Overview
class OverviewItemAdapter(items: Overviews):
    GenericAdapter<Overviewable, Modelable>(items) {
    override fun afterBind(item: Overviewable, view: View, position: Int) {}
}

//-------------------------------------------------------------------------------------------------- Timetable
class TimetableItemAdapter(val items: Timetables):
    GenericAdapter<Identifiable<Modelable>, Modelable>(items) {
    override fun afterBind(item: Identifiable<Modelable>, view: View, position: Int) {}
}

//-------------------------------------------------------------------------------------------------- Grade
class GradeItemAdapter(items: Grades):
    GenericAdapter<Gradable, GradableModels>(items) {
    override fun afterBind(item: Gradable, view: View, position: Int) {
        when (item) {
            is GradeItem -> {
                item.run {
                    setChevron(view.findViewById(R.id.ivChevron))
                    onToggle { notifyItemChanged(position) }
                }
            }
        }
    }
}

//-------------------------------------------------------------------------------------------------- Exam
class ExamItemAdapter(items: Exams):
    GenericAdapter<Examable, ExamableModels>(items) {
    override fun afterBind(item: Examable, view: View, position: Int) {}
}

//-------------------------------------------------------------------------------------------------- Room Occupancies
class RoomOccupancyItemAdapter(items: Occupancies):
    GenericAdapter<RoomOccupancable, RoomOccupancableModels>(items) {
    override fun afterBind(item: RoomOccupancable, view: View, position: Int) {}
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

//-------------------------------------------------------------------------------------------------- Room Occupancies Detail
class RoomOccupancyDetailItemAdapter(items: DetailOccupancies):
    GenericAdapter<DetailRoomOccupancable, DetailRoomOccupancableModels>(items) {
    override fun afterBind(item: DetailRoomOccupancable, view: View, position: Int) {
        when (item) {
            is DetailRoomOccupancyItem -> {
                item.addRooms(view.findViewById(R.id.llRooms))
            }
        }
    }
}

//-------------------------------------------------------------------------------------------------- Canteen
class CanteenItemAdapter(items: Canteens):
    GenericAdapter<Canteenable, CanteenableModels>(items) {
    override fun afterBind(item: Canteenable, view: View, position: Int) {}
}

//-------------------------------------------------------------------------------------------------- Meals
class MealItemAdapter(items: Meals):
    GenericAdapter<Mealable, MealableModel>(items) {
    override fun afterBind(item: Mealable, view: View, position: Int) {}
}

//-------------------------------------------------------------------------------------------------- Campus plan
class CampusPlanItemAdapter(items: CampusPlans):
    GenericAdapter<CampusPlanable, CampusPlanModels>(items) {
    override fun afterBind(item: CampusPlanable, view: View, position: Int) {
        when (item) {
            is CampusPlanItem -> item.addBuildings(view.findViewById(R.id.llBuildings))
        }
    }
}

//-------------------------------------------------------------------------------------------------- Management
class ManagementItemAdapter(items: Managements):
    GenericAdapter<Managementable, ManagementableModels>(items) {
    override fun afterBind(item: Managementable, view: View, position: Int) {
        when (item) {
            is SemesterPlanItem -> item.addAdditionalInfo(view.findViewById(R.id.llAdditionalInfo))
            is ManagementItem -> {
                item.run {
                    offers(view.findViewById(R.id.llOffers))
                    times(view.findViewById(R.id.llTimes))
                }
            }
        }
    }
}