package de.htwdd.htwdresden.custom

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.utils.extensions.setColorForLessonType

@SuppressLint("ViewConstructor")
class LessonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, timetable: Timetable
) : LinearLayout(context, attrs, defStyleAttr) {

    private val type: TextView
    private val tag: TextView
    private val rooms: TextView
    private val lessonItemContainer: CardView

    init {
        val view = inflate(context, R.layout.timetable_grid_lesson_item, this)
        type = view.findViewById(R.id.timetableType)
        tag = view.findViewById(R.id.timetableTag)
        rooms = view.findViewById(R.id.timetableRoom)
        lessonItemContainer = view.findViewById(R.id.lessonItemContainer)
        tag.text = timetable.lessonTag
        type.text = timetable.type
        rooms.text = TextUtils.join(", ", timetable.rooms)
        lessonItemContainer.setColorForLessonType(timetable.type)
//        lessonItemRoot.setOnClickListener { onClick(lesson) }
    }
}