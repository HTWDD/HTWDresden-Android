package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.OverviewBindables
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.toColor
import de.htwdd.htwdresden.utils.extensions.toSHA256
import de.htwdd.htwdresden.utils.holders.StringHolder

//-------------------------------------------------------------------------------------------------- Protocols
interface Overviewable: Identifiable<OverviewBindables>
interface OverviewableModel

//-------------------------------------------------------------------------------------------------- Schedule Item
class OverviewScheduleItem(private val item: Timetable): Overviewable, Comparable<OverviewScheduleItem> {

    private val bindingTypes by lazy {
        OverviewBindables().apply {
            add(BR.overviewScheduleModel to model)
        }
    }
    private val model = OverviewScheduleModel()
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

    override fun itemViewType() = R.layout.list_item_overview_schedule_bindable

    override fun bindingTypes() = bindingTypes

    override fun compareTo(other: OverviewScheduleItem) = compareValuesBy(this, other, { it.item.day }, { it.item.beginTime })

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.id.hashCode() * 31
}

//-------------------------------------------------------------------------------------------------- FreeDay Item
class OverviewFreeDayItem: Overviewable {

    override fun itemViewType() = R.layout.list_item_overview_free_day_bindable

    override fun bindingTypes() = OverviewBindables()
}

//-------------------------------------------------------------------------------------------------- Mensa Item
class OverviewMensaItem(private val item: Meal): Overviewable {

    private val bindingTypes by lazy {
        OverviewBindables().apply {
            add(BR.overviewMensaModel to model)
        }
    }
    private val model = OverviewMensaModel()

    init {
        model.apply {
            name.set(item.name)
        }
    }

    override fun itemViewType() = R.layout.list_item_overview_mensa_bindable

    override fun bindingTypes() = bindingTypes
}

//-------------------------------------------------------------------------------------------------- Grade Item
class OverviewGradeItem(private val grades: String, private val credits: Float): Overviewable {

    private val bindingTypes by lazy {
        OverviewBindables().apply {
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

    override fun itemViewType() = R.layout.list_item_overview_grade_bindable

    override fun bindingTypes() = bindingTypes
}

//-------------------------------------------------------------------------------------------------- Header Item
class OverviewHeaderItem(private val header: String, private val subheader: String): Overviewable {

    private val bindingTypes by lazy {
        OverviewBindables().apply {
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

    override fun bindingTypes() = bindingTypes
    override fun itemViewType() = R.layout.list_item_overview_header_bindable
}

//-------------------------------------------------------------------------------------------------- StudyGroup Item
class OverviewStudyGroupItem: Overviewable {
    override fun bindingTypes() = OverviewBindables()
    override fun itemViewType() = R.layout.list_item_overview_no_studygroup_bindable
}

//-------------------------------------------------------------------------------------------------- Login Item
class OverviewLoginItem: Overviewable {
    override fun itemViewType() = R.layout.list_item_overview_no_login_bindable
    override fun bindingTypes() = OverviewBindables()
}

//-------------------------------------------------------------------------------------------------- Schedule Model
class OverviewScheduleModel: OverviewableModel {
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

//-------------------------------------------------------------------------------------------------- Mensa Model
class OverviewMensaModel: OverviewableModel {
    val name = ObservableField<String>()
}

//-------------------------------------------------------------------------------------------------- Grade Model
class OverviewGradeModel: OverviewableModel {
    val grades = ObservableField<String>()
    val credits = ObservableField<String>()
}


//-------------------------------------------------------------------------------------------------- Header Model
class OverviewHeaderModel: OverviewableModel {
    val header      = ObservableField<String>()
    val subheader   = ObservableField<String>()
}