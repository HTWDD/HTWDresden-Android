package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.OccupancyBindables
import de.htwdd.htwdresden.db.RoomRealm
import de.htwdd.htwdresden.db.delete
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.week
import de.htwdd.htwdresden.utils.holders.ColorHolder
import de.htwdd.htwdresden.utils.holders.StringHolder
import java.util.*

//-------------------------------------------------------------------------------------------------- Interface
interface RoomOccupancable: Identifiable<OccupancyBindables> {
    fun getMovementFlags(): Int {
        return when(this) {
            is RoomOccupancyItem -> LEFT
            else -> 0
        }
    }

    fun onLeftSwiped(action: () -> Unit) {
        when (this) {
            is RoomOccupancyItem -> action()
        }
    }

    fun removeFromDb()

    fun id(): String

    fun name(): String
}
interface RoomOccupancableModels

//-------------------------------------------------------------------------------------------------- Item
class RoomOccupancyItem(private val item: RoomRealm): RoomOccupancable {

    private val bindingTypes: OccupancyBindables by lazy {
        OccupancyBindables().apply {
            add(Pair(BR.roomOccupancyModel, model))
        }
    }
    private val model = RoomOccupancyModel()
    private val sh: StringHolder by lazy { StringHolder.instance }
    private val ch: ColorHolder by lazy { ColorHolder.instance }

    init {
        model.apply {
            roomName.set(item.name)
            occupancies.set("${item.occupancies.size}")
            val cLesson = item.occupancies
                            .filter { it.weeksOnly.contains("${Date().week}") }
                            .firstOrNull { Date().format("HH:mm:ss") in it.beginTime..it.endTime }

            if (cLesson != null) {
                currentLesson.set(cLesson.name)
                lessonColor.set(ch.getColor(R.color.red_500))
            } else {
                currentLesson.set(sh.getString(R.string.free))
                lessonColor.set(ch.getColor(R.color.green_500))
            }
        }
    }

    override fun bindingTypes() = bindingTypes

    override fun itemViewType() = R.layout.list_item_room_occupancy_bindable

    override fun removeFromDb() = item.delete()

    override fun id() = item.id

    override fun name() = item.name
}

//-------------------------------------------------------------------------------------------------- Model
class RoomOccupancyModel: RoomOccupancableModels {
    val roomName    = ObservableField<String>()
    val occupancies = ObservableField<String>()
    val currentLesson = ObservableField<String>()
    val lessonColor = ObservableField<Int>()
}