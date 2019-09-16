package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.TimetableBindables
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.toColor
import de.htwdd.htwdresden.utils.extensions.toDate
import de.htwdd.htwdresden.utils.extensions.toSHA256
import de.htwdd.htwdresden.utils.holders.StringHolder
import java.util.*
import java.util.Calendar.*

interface Timetableable: Identifiable<TimetableBindables> {
    override fun equals(other: Any?): Boolean
}
interface TimetableModels


data class JTimetable(
    val id: String,
    val moduleId: String? = null,
    val lessonTag: String,
    val name: String,
    val type: String,
    val day: Long,
    val beginTime: String,
    val endTime: String,
    val week: Long,
    val weeksOnly: List<Long>,
    val professor: String? = null,
    val rooms: List<String>,
    val lastChanged: String
)

class Timetable(
    val id: String,
    val moduleId: String? = null,
    val lessonTag: String,
    val name: String,
    val type: String,
    val day: Long,
    val beginTime: Date,
    val endTime: Date,
    val week: Long,
    val weeksOnly: List<Long>,
    val professor: String? = null,
    val rooms: List<String>,
    val lastChanged: String,
    val lessonDays: List<String>) {

    companion object {
        fun from(json: JTimetable): Timetable {
            return Timetable(
                json.id,
                json.moduleId,
                json.lessonTag,
                json.name,
                json.type,
                json.day,
                json.beginTime.toDate("HH:mm:ss")!!,
                json.endTime.toDate("HH:mm:ss")!!,
                json.week,
                json.weeksOnly,
                json.professor,
                json.rooms,
                json.lastChanged,
                lessonDays(json.day, json.weeksOnly)
            )
        }

        private fun lessonDays(dayOfWeek: Long, weeksOnly: List<Long>): List<String> {
            val calendar = GregorianCalendar.getInstance(Locale.GERMANY)
            calendar.set(DAY_OF_WEEK, (dayOfWeek.toInt() % 7) + 1)
            var lastWeek = 0
            return weeksOnly.map {
                calendar.set(WEEK_OF_YEAR, it.toInt())
                if ((lastWeek - it.toInt()) > 1) {
                    calendar.set(YEAR, calendar.get(YEAR) + 1)
                }
                lastWeek = it.toInt()
                calendar.time.format("MM-dd-yyyy")
            }
        }
    }

}

class TimetableItem(private val item: Timetable): Timetableable, Comparable<TimetableItem> {

    private val bindingTypes: TimetableBindables by lazy {
        TimetableBindables().apply {
            add(Pair(BR.timetableModel, model))
        }
    }
    private val model = TimetableModel()
    private val sh: StringHolder by lazy { StringHolder.instance }

    init {
        model.apply {
            name.set(item.name)
            setProfessor(item.professor)
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
            beginTime.set(item.beginTime.format("HH:mm"))
            endTime.set(item.endTime.format("HH:mm"))

            val colors = sh.getStringArray(R.array.timetableColors)
            val colorPosition = Integer.parseInt("${item.name} - ${item.professor}".toSHA256().subSequence(0..5).toString(), 16) % colors.size
            lessonColor.set(colors[colorPosition].toColor())

            setRooms(item.rooms)
        }
    }

    override fun itemViewType() = R.layout.list_item_timetable_bindable

    override fun bindingTypes() = bindingTypes

    override fun compareTo(other: TimetableItem) = compareValuesBy(this, other, { it.item.day }, { it.item.beginTime })

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.id.hashCode() * 31
}

class TimetableHeaderItem(private val header: String, private val subheader: Date): Timetableable {


    private val bindingTypes: TimetableBindables by lazy {
        TimetableBindables().apply {
            add(Pair(BR.timetableHeaderModel, model))
        }
    }
    private val model = TimetableHeaderModel()

    init {
        model.apply {
            header.set(this@TimetableHeaderItem.header)
            subheader.set(this@TimetableHeaderItem.subheader.format("dd. MMMM"))
        }
    }

    fun subheader(): Date = subheader


    override fun itemViewType() = R.layout.list_item_timetable_header_bindable

    override fun bindingTypes() = bindingTypes

    override fun hashCode() = 31 * header.hashCode() + subheader.hashCode()

    override fun equals(other: Any?) = hashCode() == other.hashCode()
}

class TimetableFreeDayItem(private val freeDayText: String): Timetableable {

    private val bindingTypes: TimetableBindables by lazy {
        TimetableBindables().apply {
            add(Pair(BR.timetableFreeModel, model))
        }
    }
    private val model = TimetableFreeModel()

    init {
        model.apply {
            freeDayText.set(this@TimetableFreeDayItem.freeDayText)
        }
    }

    override fun itemViewType() = R.layout.list_item_timetable_freeday_bindable

    override fun bindingTypes() = bindingTypes

    override fun hashCode() = freeDayText.hashCode()

    override fun equals(other: Any?) = freeDayText.hashCode() == other.hashCode()

}

class TimetableModel: TimetableModels {
    val name            = ObservableField<String>()
    val professor       = ObservableField<String>()
    val type            = ObservableField<String>()
    val beginTime       = ObservableField<String>()
    val endTime         = ObservableField<String>()
    val rooms           = ObservableField<String>()
    val hasProfessor    = ObservableField<Boolean>()
    val hasRooms        = ObservableField<Boolean>()
    val lessonColor     = ObservableField<Int>()

    fun setProfessor(professor: String?) {
        hasProfessor.set(!professor.isNullOrEmpty())
        this.professor.set(professor)
    }

    fun setRooms(list: List<String>) {
        hasRooms.set(!list.isNullOrEmpty())
        rooms.set(list.joinToString(", "))
    }
}

class TimetableHeaderModel: TimetableModels {
    val header      = ObservableField<String>()
    val subheader   = ObservableField<String>()
}

class TimetableFreeModel: TimetableModels {
    val freeDayText = ObservableField<String>()
}