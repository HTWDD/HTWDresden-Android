package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.utils.extensions.convertDayToString
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.toColor
import de.htwdd.htwdresden.utils.extensions.toSHA256
import de.htwdd.htwdresden.utils.holders.StringHolder

//-------------------------------------------------------------------------------------------------- Protocols
interface Overviewable: Identifiable<Modelable>

//-------------------------------------------------------------------------------------------------- Schedule Item
class OverviewScheduleItem(val item: Timetable, val addElective: Boolean = false): Overviewable {

    override val viewType: Int
        get() = R.layout.list_item_overview_schedule_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, Modelable>>().apply {
            add(BR.overviewScheduleModel to model)
        }
    }

    private val model = OverviewScheduleModel()

    private val sh: StringHolder by lazy { StringHolder.instance }

    init {
        model.apply {
            name.set(item.name)
            setProfessor(item.professor)
            studiumIntegrale.set(item.studiumIntegrale)
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

            if (addElective){
                showDay.set(true)
                day.set(
                    item.day.convertDayToString(sh)
                )
            }

            setRooms(item.rooms)
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.hashCode()
}

//-------------------------------------------------------------------------------------------------- FreeDay Item
class OverviewFreeDayItem: Overviewable {

    override val viewType: Int
        get() = R.layout.list_item_overview_free_day_bindable

    override val bindings: ArrayList<Pair<Int, Modelable>>
        get() = ArrayList()

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = viewType * 31
}

//-------------------------------------------------------------------------------------------------- Mensa Item
class OverviewMensaItem(private val item: Meal): Overviewable {

    override val viewType: Int
        get() = R.layout.list_item_overview_mensa_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, Modelable>>().apply {
            add(BR.overviewMensaModel to model)
        }
    }

    private val model = OverviewMensaModel()

    init {
        model.apply {
            name.set(item.name)
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.hashCode()
}

//-------------------------------------------------------------------------------------------------- Grade Item
class OverviewGradeItem(private val grades: String, private val credits: Float): Overviewable {

    override val viewType: Int
        get() = R.layout.list_item_overview_grade_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, Modelable>>().apply {
            add(BR.overviewGradeModel to model)
        }
    }

    private val model = OverviewGradeModel()

    private val sh by lazy { StringHolder.instance }

    init {
        model.apply {
            grades.set(sh.getString(R.string.grades, this@OverviewGradeItem.grades))
            credits.set(sh.getString(R.string.exams_stats_count_credits, this@OverviewGradeItem.credits))
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = grades.hashCode()
        result = 31 * result + credits.hashCode()
        return result
    }
}

//-------------------------------------------------------------------------------------------------- Header Item
class OverviewHeaderItem(private val header: String, private val subheader: String): Overviewable {

    override val viewType: Int
        get() = R.layout.list_item_overview_header_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, Modelable>>().apply {
            add(BR.overviewHeaderModel to model)
        }
    }

    private val model = OverviewHeaderModel()

    var credits: String
        get() = model.subheader.get() ?: ""
        set(value) = model.subheader.set(value)

    init {
        model.apply {
            header.set(this@OverviewHeaderItem.header)
            subheader.set(this@OverviewHeaderItem.subheader)
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + subheader.hashCode()
        return result
    }
}

//-------------------------------------------------------------------------------------------------- StudyGroup Item
class OverviewStudyGroupItem: Overviewable {

    override val viewType: Int
        get() = R.layout.list_item_overview_no_studygroup_bindable

    override val bindings: ArrayList<Pair<Int, Modelable>>
        get() = ArrayList()

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = 31 * viewType
}

//-------------------------------------------------------------------------------------------------- Login Item
class OverviewLoginItem: Overviewable {

    override val viewType: Int
        get() = R.layout.list_item_overview_no_login_bindable

    override val bindings: ArrayList<Pair<Int, Modelable>>
        get() = ArrayList()

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = 31 * viewType
}

//-------------------------------------------------------------------------------------------------- Schedule Model
class OverviewScheduleModel: Modelable {
    val name            = ObservableField<String>()
    val professor       = ObservableField<String>()
    val type            = ObservableField<String>()
    val beginTime       = ObservableField<String>()
    val endTime         = ObservableField<String>()
    val rooms           = ObservableField<String>()
    val hasProfessor    = ObservableField<Boolean>()
    val hasRooms        = ObservableField<Boolean>()
    val showDay        = ObservableField<Boolean>()
    val day       = ObservableField<String>()
    val studiumIntegrale        = ObservableField<Boolean>()
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

//-------------------------------------------------------------------------------------------------- Mensa Model
class OverviewMensaModel: Modelable {
    val name = ObservableField<String>()
}

//-------------------------------------------------------------------------------------------------- Grade Model
class OverviewGradeModel: Modelable {
    val grades = ObservableField<String>()
    val credits = ObservableField<String>()
}


//-------------------------------------------------------------------------------------------------- Header Model
class OverviewHeaderModel: Modelable {
    val header      = ObservableField<String>()
    val subheader   = ObservableField<String>()
}