package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.StringHolder
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*
import java.util.Calendar.*
import kotlin.collections.ArrayList

//-------------------------------------------------------------------------------------------------- Protocols
interface Timetableable: Identifiable<Modelable>


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
    val studiumIntegrale: Boolean,
    val lastChanged: String
)

//-------------------------------------------------------------------------------------------------- Concrete Model
class Timetable(
    val id: String,
    val moduleId: String? = null,
    var lessonTag: String,
    var name: String,
    var type: String,
    var day: Long,
    var beginTime: Date,
    var endTime: Date,
    var week: Long,
    var weeksOnly: List<Long>,
    var professor: String? = null,
    var rooms: List<String>,
    val lastChanged: String,
    var lessonDays: List<String>,
    var studiumIntegrale: Boolean = false,
    var createdByUser: Boolean = false,
    var exactDay: Date? = null,
    var weekRotation: String? = null,
    var isHidden: Boolean = false
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
                lessonDays(json.day, json.weeksOnly),
                json.studiumIntegrale
            )
        }

        fun lessonDays(dayOfWeek: Long, weeksOnly: List<Long>): List<String> {
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
        result = 31 * result + studiumIntegrale.hashCode()
        return result
    }
}

open class TimetableRealm(
    @PrimaryKey
    var id: String = "",
    var moduleId: String? = null,
    var lessonTag: String = "",
    var name: String = "",
    var type: String = "",
    var day: Long = 0,
    var beginTime: Date? = null,
    var endTime: Date? = null,
    var week: Long = 0,
    var weeksOnly: RealmList<Long> = RealmList(),
    var professor: String? = null,
    var rooms: RealmList<String> = RealmList(),
    var lastChanged: String = "",
    var lessonDays: RealmList<String> = RealmList(),
    var studiumIntegrale: Boolean = false,
    var createdByUser: Boolean = false,
    var exactDay: Date? = null,
    var weekRotation: String? = null,
    var isHidden: Boolean = false
) : RealmObject() {

    companion object {
        fun fromTimetable(timetable: Timetable) = with(timetable) {
            TimetableRealm(id,
                moduleId,
                lessonTag,
                name,
                type,
                day,
                beginTime,
                endTime,
                week,
                RealmList<Long>().apply {
                    addAll(
                        weeksOnly
                    )
                },
                professor,
                RealmList<String>().apply { addAll(rooms) },
                lastChanged,
                RealmList<String>().apply {
                    addAll(
                        lessonDays
                    )
                },
                studiumIntegrale,
                createdByUser,
                exactDay,
                weekRotation,
                isHidden = isHidden
            )
        }

        fun toTimetable(timetableRealm: TimetableRealm) = with(timetableRealm) {
            Timetable(
                id,
                moduleId,
                lessonTag,
                name,
                type,
                day,
                beginTime!!,
                endTime!!,
                week,
                weeksOnly.toCollection(
                    ArrayList()
                ),
                professor,
                rooms.toCollection(ArrayList()),
                lastChanged,
                lessonDays.toCollection(ArrayList()),
                studiumIntegrale,
                createdByUser,
                exactDay,
                weekRotation,
                isHidden = isHidden
            )
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
class TimetableItem(val item: Timetable): Overviewable {

    override val viewType: Int
        get() = R.layout.list_item_timetable_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, Modelable>>().apply {
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
            lessonColor.set(getColorForLessonType(item.type))
            studiumIntegrale.set(item.studiumIntegrale)
            isElective.set(item.type.isElective())
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
        ArrayList<Pair<Int, Modelable>>().apply {
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
        ArrayList<Pair<Int, Modelable>>().apply {
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
class TimetableModel: Modelable {
    val name            = ObservableField<String>()
    val professor       = ObservableField<String>()
    val type            = ObservableField<String>()
    val beginTime       = ObservableField<String>()
    val endTime         = ObservableField<String>()
    val rooms           = ObservableField<String>()
    val hasProfessor    = ObservableField<Boolean>()
    val hasRooms        = ObservableField<Boolean>()
    val isElective      = ObservableField<Boolean>(false)
    val studiumIntegrale      = ObservableField<Boolean>(false)
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

class TimetableHeaderModel: Modelable {
    val header      = ObservableField<String>()
    val subheader   = ObservableField<String>()
}

class TimetableFreeModel: Modelable {
    val freeDayText = ObservableField<String>()
}

fun TimetableRealm.update(timetable: Timetable, callback: (() -> Unit)?) {
    verbose("update($timetable)")
    val realm = Realm.getDefaultInstance()
    realm.use { r ->
        r.executeTransaction { transaction ->
            var result = TimetableRealm.fromTimetable(timetable)
            r.insertOrUpdate(result)
            callback?.invoke()
        }
    }
//    return timetableRealm
}

fun TimetableRealm.updateAsync(timetable: Timetable, callback: (() -> Unit)?) {
    verbose("update($timetable)")
    val realm = Realm.getDefaultInstance()
    // Asynchronously update objects on a background thread
    realm.executeTransactionAsync({ bgRealm ->
        val timetableRealm = bgRealm.copyToRealmOrUpdate(TimetableRealm.fromTimetable(timetable))
    }, Realm.Transaction.OnSuccess {
        callback?.invoke()
    })
}

fun TimetableRealm.delete() {
    val realm = Realm.getDefaultInstance()
    realm.use {
        it.executeTransaction {
            this.deleteFromRealm()
        }
    }
}

fun Any.getTimetableById(id: String) : Timetable? {
    val realm = Realm.getDefaultInstance()
    var timetableRealm: TimetableRealm? = null
    timetableRealm = realm.where(TimetableRealm::class.java).equalTo("id", id).findFirst()
    var timetable: Timetable? = null
    timetableRealm?.let {
        timetable = TimetableRealm.toTimetable(it)
    }
    return timetable
}

fun Any.deleteAllTimetable() {
    val realm = Realm.getDefaultInstance()
    realm.use {
        it.executeTransaction {
            it.delete(TimetableRealm::class.java)
        }
    }
}

fun Any.deleteAllIfNotCreatedByUser() {
    val realm = Realm.getDefaultInstance()
    realm.use {
        it.executeTransaction {
            val result = realm.where(TimetableRealm::class.java).equalTo("createdByUser", false).findAll()
            result.deleteAllFromRealm()
        }
    }
}

fun Any.deleteAllElectives() {
    val realm = Realm.getDefaultInstance()
    realm.use {
        it.executeTransaction {
            val result = realm.where(TimetableRealm::class.java)
                .contains("type", "w")
                .contains("type", "Modul(SI)")
                .findAll()
            result.deleteAllFromRealm()
        }
    }
}

fun Any.deleteById(id: String) {
    val realm = Realm.getDefaultInstance()
    realm.use {
        it.executeTransaction {
            val result = realm.where(TimetableRealm::class.java).equalTo("id", id).findFirst()
            result?.deleteFromRealm()
        }
    }
}

fun Any.getNotHiddenTimetables() : List<Timetable> {
   val list = Realm.getDefaultInstance().where(TimetableRealm::class.java).findAll().map { TimetableRealm.toTimetable(
       it
   ) }
    return list.filter { !it.isHidden }
}

fun Any.getAllTimetables() : List<Timetable> {
    return Realm.getDefaultInstance().where(TimetableRealm::class.java).findAll().map { TimetableRealm.toTimetable(
        it
    ) }
}

fun Any.getHiddenTimetables() : List<String> {
    val list = Realm.getDefaultInstance().where(TimetableRealm::class.java).findAll().map { TimetableRealm.toTimetable(
        it
    ) }
    return list.filter { it.isHidden }.map {it.id}
}

fun Timetable.createDescriptionForCalendar() : String {
    val content = ArrayList<String>()
    if(lessonTag.isNotEmpty()) content.add(lessonTag)
    if(type.isNotEmpty()) content.add(type.fullLessonType)
    if(!professor.isNullOrEmpty()) content.add(professor ?: "")
    if(rooms.isNotEmpty()) content.addAll(rooms)
    return content.joinToString(", ")
}