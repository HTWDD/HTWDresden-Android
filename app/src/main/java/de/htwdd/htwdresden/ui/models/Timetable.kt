package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.db.OccupancyRealm
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.StringHolder
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.createObject
import java.util.*
import java.util.Calendar.*
import kotlin.collections.ArrayList

//-------------------------------------------------------------------------------------------------- Protocols
interface Timetableable: Identifiable<TimetableableModels>
interface TimetableableModels: Modelable

//-------------------------------------------------------------------------------------------------- JSON
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

//-------------------------------------------------------------------------------------------------- Concrete Model
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
       val lessonDays: List<String>
) : Comparable<Timetable> {

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
            val calendar = GregorianCalendar.getInstance(Locale.GERMANY).apply {
                set(DAY_OF_WEEK, (dayOfWeek.toInt() % 7) + 1)
            }
            var currentYear = calendar.get(YEAR)
            var lastWeek = getInstance().get(WEEK_OF_YEAR)
            val diffWeeks = weeksOnly.drop(1).zip(weeksOnly).map {
                it.first - it.second
            }.filter { it < 0 }

            return weeksOnly.map {
                if (diffWeeks.isNotEmpty()) {
                    if (lastWeek - it < -10) {
                        currentYear -= 1
                    } else if (lastWeek - it >= kotlin.math.abs(diffWeeks.first())) {
                        currentYear += 1
                    }
                }
                lastWeek = it.toInt()
                calendar.set(WEEK_OF_YEAR, it.toInt())
                calendar.set(YEAR, currentYear)
                calendar.time.format("MM-dd-yyyy")
            }
        }
    }

    override fun compareTo(other: Timetable) =
        compareValuesBy(this, other, { it.day }, { it.beginTime })

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (moduleId?.hashCode() ?: 0)
        result = 31 * result + lessonTag.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + day.hashCode()
        result = 31 * result + beginTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + week.hashCode()
        result = 31 * result + weeksOnly.hashCode()
        result = 31 * result + (professor?.hashCode() ?: 0)
        result = 31 * result + rooms.hashCode()
        result = 31 * result + lastChanged.hashCode()
        result = 31 * result + lessonDays.hashCode()
        return result
    }
}

open class TimetableRealm(
        @PrimaryKey
        var id: String = "",
        var moduleId:  String? = null,
        var lessonTag: String = "",
        var name:  String = "",
        var type:  String = "",
        var day: Long = 0,
        var beginTime: Date? = null,
        var endTime: Date? = null,
        var week: Long = 0,
        var weeksOnly: RealmList<Long> = RealmList(),
        var professor: String? = null,
        var rooms: RealmList<String> = RealmList(),
        var lastChanged: String = "",
        var lessonDays: RealmList<String> = RealmList()
) : RealmObject() {

    companion object {
        fun fromTimetable(timetable: Timetable) = with(timetable) {
            TimetableRealm(id, moduleId, lessonTag, name, type, day, beginTime, endTime, week, RealmList<Long>().apply { addAll(weeksOnly) }, professor,
                    RealmList<String>().apply { addAll(rooms) }, lastChanged, RealmList<String>().apply { addAll(lessonDays) })
        }

        fun toTimetable(timetableRealm: TimetableRealm) = with(timetableRealm) {
            Timetable(id, moduleId, lessonTag, name, type, day, beginTime!!, endTime!!, week, weeksOnly?.toCollection(ArrayList())  ?: emptyList(), professor,
                    rooms?.toCollection(ArrayList())  ?: emptyList(), lastChanged, lessonDays?.toCollection(ArrayList()) ?: emptyList())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimetableRealm
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

//-------------------------------------------------------------------------------------------------- Item
class TimetableItem(val item: Timetable): Timetableable {

    override val viewType: Int
        get() = R.layout.list_item_timetable_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, TimetableableModels>>().apply {
            add(BR.timetableModel to model)
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

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.hashCode()
}

//-------------------------------------------------------------------------------------------------- Header Item
class TimetableHeaderItem(private val header: String, private val subheader: Date): Timetableable {

    override val viewType: Int
        get() =  R.layout.list_item_timetable_header_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, TimetableableModels>>().apply {
            add(BR.timetableHeaderModel to model)
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

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = 31 * header.hashCode() + subheader.hashCode()

}

//-------------------------------------------------------------------------------------------------- Freeday Item
class TimetableFreeDayItem(private val freeDayText: String): Timetableable {

    override val viewType: Int
        get() = R.layout.list_item_timetable_freeday_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, TimetableableModels>>().apply {
            add(BR.timetableFreeModel to model)
        }
    }

    private val model = TimetableFreeModel()

    init {
        model.apply {
            freeDayText.set(this@TimetableFreeDayItem.freeDayText)
        }
    }

    override fun hashCode() = freeDayText.hashCode()

    override fun equals(other: Any?) = freeDayText.hashCode() == other.hashCode()
}

//-------------------------------------------------------------------------------------------------- Modable
class TimetableModel: TimetableableModels {
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

class TimetableHeaderModel: TimetableableModels {
    val header      = ObservableField<String>()
    val subheader   = ObservableField<String>()
}

class TimetableFreeModel: TimetableableModels {
    val freeDayText = ObservableField<String>()
}

fun TimetableRealm.update(timetable: Timetable) {
    verbose("update($timetable)")
    val realm = Realm.getDefaultInstance()
    realm.use { r ->
        r.executeTransaction { transaction ->
            var result = TimetableRealm.fromTimetable(timetable)
            r.insertOrUpdate(result)
        }
    }
//    return timetableRealm
}

fun TimetableRealm.delete() {
    val realm = Realm.getDefaultInstance()
    realm.use {
        it.executeTransaction {
            this.deleteFromRealm()
        }
    }
}

fun Any.deleteAllTimetable() {
    val realm = Realm.getDefaultInstance()
    realm.use {
        it.executeTransaction {
            it.delete(TimetableRealm::class.java)
        }
    }
}