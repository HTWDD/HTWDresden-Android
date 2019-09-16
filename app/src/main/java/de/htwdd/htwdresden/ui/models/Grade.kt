package de.htwdd.htwdresden.ui.models

import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.GradesBindables
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.playAnimation
import de.htwdd.htwdresden.utils.extensions.toDate
import de.htwdd.htwdresden.utils.holders.ColorHolder
import de.htwdd.htwdresden.utils.holders.StringHolder
import java.util.*

//-------------------------------------------------------------------------------------------------- Protocols
interface Gradable: Identifiable<GradesBindables>
interface GradableModel

//-------------------------------------------------------------------------------------------------- JSON
data class JGrade(
    val tries: Long,
    val note: String? = null,
    val nr: Long,
    val examDate: String? = null,
    val form: String,
    val credits: Float,
    val grade: Long? = null,
    val semester: Long,
    val text: String,
    val state: String,
    val id: Long
)

//-------------------------------------------------------------------------------------------------- Grade
class Grade (
    val tries: Long,
    val remark: GradeRemark,
    val examNumber: Long,
    val examDate: Date? = null,
    val typeOfExamination: String,
    val credits: Float,
    val grade: Long? = null,
    val semester: Long,
    val examination: String,
    val state: GradeState,
    val id: Long
): Comparable<Grade> {

    override fun compareTo(other: Grade): Int {
        return if (examDate != null) {
            if (other.examDate != null) {
               other.examDate.compareTo(examDate)
            } else {
                -1
            }
        } else {
            1
        }
    }

    companion object {
        fun from(json: JGrade): Grade {
            return Grade(
                json.tries,
                when (json.note) {
                    "a" -> GradeRemark.Recognised(R.string.exams_result_note_recognized)
                    "e" -> GradeRemark.Unsubscribed(R.string.exams_result_note_sign_off)
                    "g" -> GradeRemark.Blocked(R.string.exams_result_note_blocked)
                    "k" -> GradeRemark.Ill(R.string.exams_result_note_ill)
                    "nz" -> GradeRemark.NotPermitted(R.string.exams_result_note_not_allowed)
                    "5ue" -> GradeRemark.MissedWithoutExcuse(R.string.exams_result_note_unexcused_missing)
                    "5na" -> GradeRemark.NotEntered(R.string.exams_result_note_not_started)
                    "kA" -> GradeRemark.NotSecondRequested(R.string.exams_result_note_no_retest)
                    "PFV" -> GradeRemark.FreeTrail(R.string.exams_result_note_free_try)
                    "mE" -> GradeRemark.Successfully(R.string.exams_result_note_with_success)
                    "N" -> GradeRemark.Failed(R.string.exams_result_note_failed)
                    "VPo" -> GradeRemark.PrepraticalOpen(R.string.exams_result_note_pre_placement)
                    "f" -> GradeRemark.VolutaryDateNotMet(R.string.exams_result_note_voluntary_appointment)
                    "uV" -> GradeRemark.Conditionally(R.string.exams_result_note_conditional)
                    "TA" -> GradeRemark.Cheated(R.string.exams_result_note_attempt)
                    else -> GradeRemark.NoRemark(R.string.exams_result_no_remark)
                },
                json.nr,
                json.examDate?.toDate("yyyy-MM-dd'T'HH:mm'Z'"),
                json.form,
                json.credits,
                json.grade,
                json.semester,
                json.text,
                when (json.state) {
                    "AN" -> GradeState.Enrolled(R.string.exams_result_enrolled)
                    "BE" -> GradeState.Passed(R.string.exams_result_passed)
                    "NB" -> GradeState.Failed(R.string.exams_result_failed)
                    "EN" -> GradeState.FinalFailed(R.string.exams_result_final_failed)
                    else -> GradeState.Unknown(R.string.exams_result_note_unkown)
                },
                json.id
            )
        }
    }
}

sealed class GradeState {
    data class Enrolled(@StringRes val state: Int): GradeState()
    data class Passed(@StringRes val state: Int): GradeState()
    data class Failed(@StringRes val state: Int): GradeState()
    data class FinalFailed(@StringRes val state: Int): GradeState()
    data class Unknown(@StringRes val state: Int): GradeState()
}

sealed class GradeRemark(@StringRes val resId: Int) {
    data class Recognised(@StringRes val id: Int): GradeRemark(id)
    data class Unsubscribed(@StringRes val id: Int): GradeRemark(id)
    data class Blocked(@StringRes val id: Int): GradeRemark(id)
    data class Ill(@StringRes val id: Int): GradeRemark(id)
    data class NotPermitted(@StringRes val id: Int): GradeRemark(id)
    data class MissedWithoutExcuse(@StringRes val id: Int): GradeRemark(id)
    data class NotEntered(@StringRes val id: Int): GradeRemark(id)
    data class NotSecondRequested(@StringRes val id: Int): GradeRemark(id)
    data class FreeTrail(@StringRes val id: Int): GradeRemark(id)
    data class Successfully(@StringRes val id: Int): GradeRemark(id)
    data class Failed(@StringRes val id: Int): GradeRemark(id)
    data class PrepraticalOpen(@StringRes val id: Int): GradeRemark(id)
    data class VolutaryDateNotMet(@StringRes val id: Int): GradeRemark(id)
    data class Conditionally(@StringRes val id: Int): GradeRemark(id)
    data class Cheated(@StringRes val id: Int): GradeRemark(id)
    data class NoRemark(@StringRes val id: Int): GradeRemark(id)
}

//-------------------------------------------------------------------------------------------------- Grade Item
class GradeItem(private val item: Grade): Gradable {

    private val bindingTypes by lazy {
        GradesBindables().apply {
            add(BR.gradeModel to model)
        }
    }
    private val model = GradeModel()
    private val ch by lazy { ColorHolder.instance }
    private val sh by lazy { StringHolder.instance }
    private var chevron: ImageView? = null
    private var onToggledClosure: () -> Unit = {}


    init {
        model.apply {
            examination.set(item.examination)
            if (item.grade != null) {
                grade.set("${item.grade.div(100f)}")
                gradeColor.set(ch.getColor(R.color.dark_gray))
            } else {
                grade.set("-/-")
                gradeColor.set(ch.getColor(R.color.light_grey))
            }
            when (item.state) {
                is GradeState.Enrolled -> {
                    stateColor.set(ch.getColor(R.color.light_blue))
                    state.set(sh.getString(item.state.state))
                }
                is GradeState.Passed -> {
                    stateColor.set(ch.getColor(R.color.green_500))
                    state.set(sh.getString(item.state.state))
                }
                is GradeState.Failed -> {
                    stateColor.set(ch.getColor(R.color.red_500))
                    state.set(sh.getString(item.state.state))
                }
                is GradeState.FinalFailed -> {
                    stateColor.set(ch.getColor(R.color.black))
                    state.set(sh.getString(item.state.state))
                }
                is GradeState.Unknown -> {
                    stateColor.set(ch.getColor(R.color.htw_orange))
                    state.set(sh.getString(item.state.state))
                }
            }
            credits.set(sh.getString(R.string.exams_stats_count_credits, item.credits))
            typeOfExamination.set(item.typeOfExamination)

            tries.set(sh.getString(R.string.exams_result_tries, item.tries))
            triesColor.set(ch.getColor(when(item.tries) {
                1L      -> R.color.green_500
                2L      -> R.color.htw_orange
                else    -> R.color.red_500
            }))

            examDate.set(if (item.examDate != null) {
                item.examDate.format("dd.MM.yyyy")
            } else {
                sh.getString(R.string.exams_result_no_date)
            })

            remark.set(sh.getString(item.remark.resId))

            chevron.set(if (showAdditionalInfos.get() == true) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down)
            onToggleInfo {
                this@GradeItem.chevron.playAnimation(if (it) R.anim.rotate_clockwise else R.anim.rotate_anticlockwise) {
                    chevron.set(if (it) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down)
                }
                onToggledClosure()
            }
        }
    }

    override fun itemViewType() = R.layout.list_item_grade_bindable

    override fun bindingTypes() = bindingTypes

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    fun setChevron(imageView: ImageView) {
        chevron = imageView
    }

    override fun hashCode() =  37 * item.id.hashCode()

    fun onToggle(callback: () -> Unit) {
        onToggledClosure = callback
    }
}

//-------------------------------------------------------------------------------------------------- Grade Item Header
class GradeHeaderItem(private val header: String, private val subheader: String): Gradable {

    private val bindingTypes by lazy {
        GradesBindables().apply {
            add(BR.gradeHeaderModel to model)
        }
    }
    private val model = GradeHeaderModel()

    init {
        model.apply {
            header.set(this@GradeHeaderItem.header)
            subheader.set(this@GradeHeaderItem.subheader)
        }
    }

    override fun itemViewType() = R.layout.list_item_grade_header_bindable

    override fun bindingTypes() = bindingTypes

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = 31 * header.hashCode() + subheader.hashCode() * 37
}

//-------------------------------------------------------------------------------------------------- Grade Item Average
class GradeAverageItem(private val gradeAverage: Float, private val credits: Float): Gradable {

    private val bindingTypes by lazy {
        GradesBindables().apply {
            add(BR.gradeAverageModel to model)
        }
    }
    private val model = GradeAverageModel()
    private val sh by lazy { StringHolder.instance }

    init {
        model.apply {
            gradeAverage.set(sh.getString(R.string.exams_grade_average, this@GradeAverageItem.gradeAverage))
            credits.set(sh.getString(R.string.exams_stats_count_credits, this@GradeAverageItem.credits))
        }
    }

    override fun itemViewType() = R.layout.list_item_grade_average_bindable

    override fun bindingTypes() = bindingTypes

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = gradeAverage.hashCode() * 37 + credits.hashCode() * 31
}

//-------------------------------------------------------------------------------------------------- Model
class GradeModel: GradableModel {
    val tries               = ObservableField<String>()
    val triesColor          = ObservableField<Int>()
    val remark              = ObservableField<String>()
    val examNumber          = ObservableField<Long>()
    val examDate            = ObservableField<String>()
    val typeOfExamination   = ObservableField<String>()
    val credits             = ObservableField<String>()
    val grade               = ObservableField<String>()
    val examination         = ObservableField<String>()
    val state               = ObservableField<String>()
    val stateColor          = ObservableField<Int>()
    val showAdditionalInfos = ObservableField(false)
    val gradeColor          = ObservableField<Int>()
    val chevron             = ObservableField<Int>()

    private var onToggleInfoClosure: (isShowing: Boolean) -> Unit = {}

    fun toggleAdditionalInfos() {
        showAdditionalInfos.set(!(showAdditionalInfos.get() ?: false))
        onToggleInfoClosure(showAdditionalInfos.get() ?: false)
    }

    fun onToggleInfo(callback: (isShowing: Boolean) -> Unit ) {
        onToggleInfoClosure = callback
    }
}

class GradeHeaderModel: GradableModel {
    val header      = ObservableField<String>()
    val subheader   = ObservableField<String>()
}

class GradeAverageModel: GradableModel {
    val gradeAverage = ObservableField<String>()
    val credits      = ObservableField<String>()
}