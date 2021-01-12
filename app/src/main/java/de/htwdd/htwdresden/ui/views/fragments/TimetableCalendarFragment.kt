package de.htwdd.htwdresden.ui.views.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.GridView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.TimetableGridAdapter
import de.htwdd.htwdresden.adapter.TimestampAdapter
import de.htwdd.htwdresden.databinding.TimetableCalendarFragmentBinding
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.ui.viewmodels.fragments.factories.TimetableCalendarViewModelFactory
import de.htwdd.htwdresden.utils.extensions.currentWeek
import de.htwdd.htwdresden.utils.extensions.inflateDataBinding
import de.htwdd.htwdresden.utils.extensions.withArgumentsOf
import kotlinx.android.synthetic.main.timetable_calendar_fragment.*

class TimetableCalendarFragment : Fragment(), ClickListener{

    companion object {
        fun newInstance(calenderType: Int) = TimetableCalendarFragment().withArgumentsOf(CALENDER_TYPE_KEY to calenderType)
        private const val CALENDER_TYPE_KEY = "calendar_type"
        const val CALENDAR_CURRENT_WEEK = 0
        const val CALENDAR_NEXT_WEEK = 1
    }

    private val calenderType by lazy { arguments?.getInt(CALENDER_TYPE_KEY)!! }
    private val viewModel: TimetableCalendarViewModel by viewModels {  TimetableCalendarViewModelFactory(calenderType) }
    private var calendar: GridView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflateDataBinding<TimetableCalendarFragmentBinding>(R.layout.timetable_calendar_fragment, container).apply {
            timetableCalendarViewModel = viewModel
        }
        binding.clickListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isCurrentWeek = calenderType == CALENDAR_CURRENT_WEEK
        calendar = timetableCalendar
        calendar?.adapter = TimetableGridAdapter(activity as Context, isCurrentWeek) { onEventClick() }
        timetableTimestampsGrid.adapter = TimestampAdapter(activity as Context)
    }

    private fun onEventClick() {
//        findNavController()
//            .navigate(R.id.action_calender_add_event_fragment)
    }

    override fun onResume() {
        viewModel.request()
        super.onResume()
    }

    override fun onLessonClick(timetable: Timetable) {
        findNavController()
            .navigate(R.id.action_calender_add_event_fragment, bundleOf(CalendarAddEventFragment.ARG_ID to timetable.id))
    }

}

interface ClickListener {
    fun onLessonClick(timetable: Timetable)
}