package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.utils.extensions.defaultWhenNull
import de.htwdd.htwdresden.utils.holders.StringHolder
import java.io.Serializable

//-------------------------------------------------------------------------------------------------- Protocols
interface Examable: Identifiable<ExamableModels>
interface ExamableModels: Modelable

//-------------------------------------------------------------------------------------------------- JSON
data class JExam(
    val title: String,
    val examType: String,
    val studyBranch: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val examiner: String,
    val nextChance: String,
    val rooms: List<String>
): Serializable

//-------------------------------------------------------------------------------------------------- Concrete Model
class Exam(
    val title: String,
    val examType: String,
    val studyBranch: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val examiner: String,
    val nextChance: String,
    val rooms: List<String>): Comparable<Exam> {

    companion object {
        fun from(json: JExam): Exam {
            return Exam(
                json.title,
                json.examType,
                json.studyBranch,
                json.day,
                json.startTime,
                json.endTime,
                json.examiner,
                json.nextChance,
                json.rooms
            )
        }
    }

    override fun compareTo(other: Exam) = day.compareTo(other.day)

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + examType.hashCode()
        result = 31 * result + studyBranch.hashCode()
        result = 31 * result + day.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + examiner.hashCode()
        result = 31 * result + nextChance.hashCode()
        result = 31 * result + rooms.hashCode()
        return result
    }
}

//-------------------------------------------------------------------------------------------------- Grade Warning
class ExamWarningItem(private val text: String): Examable {

    private val model = ExamWarningModel()

    override val viewType: Int
        get() = R.layout.list_item_exam_warning

    override val bindings by lazy {
        ArrayList<Pair<Int, ExamableModels>>().apply {
            add(BR.examWarningModel to model)
        }
    }

    init {
        model.apply {
            text.set(this@ExamWarningItem.text)
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()
    override fun hashCode() = super.hashCode()
}

//-------------------------------------------------------------------------------------------------- Item
class ExamItem(private val item: Exam): Examable {

    override val viewType: Int
        get() =  R.layout.list_item_examable_exam_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, ExamableModels>>().apply {
            add(BR.examModel to model)
        }
    }

    private val model = ExamModel()

    private val sh: StringHolder by lazy { StringHolder.instance }

    init {
        model.apply {
            title.set(item.title)
            examType.set(item.examType
                .replace("SP", sh.getString(R.string.exams_type_written))
                .replace("MP", sh.getString(R.string.exams_type_oral)))
            studyBranch.set(sh.getString(R.string.exams_branch, item.studyBranch.defaultWhenNull("-")))
            day.set(item.day)
            examTime.set("${item.startTime} - ${item.endTime}")
            examiner.set(sh.getString(R.string.exams_examinier, item.examiner))
            nextChance.set(item.nextChance)
            rooms.set(item.rooms.joinToString(", "))
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.hashCode()
}

//-------------------------------------------------------------------------------------------------- Modelable
class ExamModel: ExamableModels {
    val title       = ObservableField<String>()
    val examType    = ObservableField<String>()
    val studyBranch = ObservableField<String>()
    val day         = ObservableField<String>()
    val examTime    = ObservableField<String>()
    val examiner    = ObservableField<String>()
    val nextChance  = ObservableField<String>()
    val rooms       = ObservableField<String>()
}


class ExamWarningModel: ExamableModels {
    val text      = ObservableField<String>()
}