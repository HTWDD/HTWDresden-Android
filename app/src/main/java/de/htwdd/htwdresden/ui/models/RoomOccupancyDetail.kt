package de.htwdd.htwdresden.ui.models

import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.DetailOccupancyBindables
import de.htwdd.htwdresden.db.OccupancyRealm
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.StringHolder
import java.util.*

//-------------------------------------------------------------------------------------------------- Interface
interface DetailRoomOccupancable: Identifiable<DetailOccupancyBindables>
interface DetailRoomOccupancableModels

//-------------------------------------------------------------------------------------------------- Item
class DetailRoomOccupancyItem(private val item: OccupancyRealm): DetailRoomOccupancable, Comparable<DetailRoomOccupancyItem> {


    private val bindingTypes: DetailOccupancyBindables by lazy {
        DetailOccupancyBindables().apply {
            add(Pair(BR.detailRoomOccupancyModel, model))
        }
    }
    private val model = DetailRoomOccupancyModel()
    private val sh: StringHolder by lazy { StringHolder.instance }

    init {
        model.apply {
            name.set(item.name.defaultWhenNull(sh.getString(R.string.no_lesson)))
            professor.set(item.professor.defaultWhenNull(sh.getString(R.string.no_professor)))
            type.set(with(item.type) {
                when {
                    startsWith("v", true) -> sh.getString(R.string.lecture)
                    startsWith("ü", true) -> sh.getString(R.string.excersise)
                    startsWith("p", true) -> sh.getString(R.string.practical)
                    startsWith("b", true) -> sh.getString(R.string.block)
                    startsWith("r", true) -> sh.getString(R.string.requested)
                    else -> sh.getString(R.string.unknown)
                }
            })

            beginTime.set(item.beginTime.toDate("HH:mm:ss")?.format("HH:mm"))
            endTime.set(item.endTime.toDate("HH:mm:ss")?.format("HH:mm"))

            val colors = sh.getStringArray(R.array.timetableColors)
            val colorPosition = Integer.parseInt("${item.name} - ${item.professor}".toSHA256().subSequence(0..5).toString(), 16) % colors.size
            lessonColor.set(colors[colorPosition].toColor())

        }
    }

    override fun itemViewType() = R.layout.list_item_detail_room_occupancy_bindable

    override fun bindingTypes() = bindingTypes

    fun addRooms(layout: LinearLayout) {
        layout.removeAllViews()
        val calendar = GregorianCalendar.getInstance(Locale.GERMANY)
        calendar.set(Calendar.DAY_OF_WEEK, (item.day % 7) + 1)
        item.weeksOnly.split(",").map {
            calendar.set(Calendar.WEEK_OF_YEAR, it.toInt())
            calendar.time.format("dd.MM.")
        }.forEach {
            layout.addView(TextView(ContextThemeWrapper(layout.context, R.style.HTW_BadgeLabel_Small_Blue)).apply {
                text = it
            })
        }
    }

    override fun compareTo(other: DetailRoomOccupancyItem) = item.beginTime.compareTo(other.item.beginTime)
}

class DetailRoomOccupancyHeaderItem(private val header: String, private val subheader: String): DetailRoomOccupancable {

    private val bindingTypes: DetailOccupancyBindables by lazy {
        DetailOccupancyBindables().apply {
            add(Pair(BR.detailRoomOccupancyHeaderModel, model))
        }
    }
    private val model = DetailRoomOccupancyHeaderModel()

    init {
        model.apply {
            header.set(this@DetailRoomOccupancyHeaderItem.header)
            subheader.set(this@DetailRoomOccupancyHeaderItem.subheader)
        }
    }

    override fun itemViewType() = R.layout.list_item_room_occupancy_detail_header_bindable

    override fun bindingTypes() = bindingTypes
}

//-------------------------------------------------------------------------------------------------- Model
class DetailRoomOccupancyModel: DetailRoomOccupancableModels {

    val name            = ObservableField<String>()
    val professor       = ObservableField<String>()
    val type            = ObservableField<String>()
    val beginTime       = ObservableField<String>()
    val endTime         = ObservableField<String>()
    val rooms           = ObservableField<String>()
    val hasRooms        = ObservableField<Boolean>()
    val lessonColor     = ObservableField<Int>()


    fun setRooms(list: List<String>) {
        hasRooms.set(!list.isNullOrEmpty())
        rooms.set(list.joinToString(", "))
    }
}

class DetailRoomOccupancyHeaderModel: DetailRoomOccupancableModels {
    val header      = ObservableField<String>()
    val subheader   = ObservableField<String>()
}