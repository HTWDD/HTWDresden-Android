package de.htwdd.htwdresden.ui.models

import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.db.OccupancyRealm
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.StringHolder
import java.util.*
import kotlin.collections.ArrayList

//-------------------------------------------------------------------------------------------------- Protocols
interface DetailRoomOccupancable: Identifiable<DetailRoomOccupancableModels>
interface DetailRoomOccupancableModels: Modelable

//-------------------------------------------------------------------------------------------------- Item
class DetailRoomOccupancyItem(private val item: OccupancyRealm): DetailRoomOccupancable, Comparable<DetailRoomOccupancyItem> {

    override val viewType: Int
        get() = R.layout.list_item_detail_room_occupancy_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, DetailRoomOccupancableModels>>().apply {
            add(BR.detailRoomOccupancyModel to model)
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

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.hashCode()
}

//-------------------------------------------------------------------------------------------------- Header Item
class DetailRoomOccupancyHeaderItem(private val header: String, private val subheader: String): DetailRoomOccupancable {

    override val viewType: Int
        get() = R.layout.list_item_room_occupancy_detail_header_bindable


    override val bindings by lazy {
        ArrayList<Pair<Int, DetailRoomOccupancableModels>>().apply {
            add(BR.detailRoomOccupancyHeaderModel to model)
        }
    }

    private val model = DetailRoomOccupancyHeaderModel()

    init {
        model.apply {
            header.set(this@DetailRoomOccupancyHeaderItem.header)
            subheader.set(this@DetailRoomOccupancyHeaderItem.subheader)
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + subheader.hashCode()
        return result
    }
}

//-------------------------------------------------------------------------------------------------- Modelable
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