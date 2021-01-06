package de.htwdd.htwdresden.ui.views.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.GridView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.TimetableGridAdapter
import de.htwdd.htwdresden.adapter.TimestampAdapter
import de.htwdd.htwdresden.databinding.TimetableCalendarFragmentBinding
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.ui.viewmodels.fragments.factories.TimetableCalendarViewModelFactory
import de.htwdd.htwdresden.utils.extensions.inflateDataBinding
import de.htwdd.htwdresden.utils.extensions.withArgumentsOf
import kotlinx.android.synthetic.main.timetable_calendar_fragment.*

class TimetableCalendarFragment : Fragment() {

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = timetableCalendar
        calendar?.adapter = TimetableGridAdapter(activity as Context,true) { onEventClick(it) }
        timetableTimestampsGrid.adapter = TimestampAdapter(activity as Context)
        calendar?.viewTreeObserver?.addOnGlobalLayoutListener {
            val cWidth = calendar?.columnWidth
        }
        viewModel.request()
    }

    private fun onEventClick(item: Timetable?) {
        findNavController().navigate(R.id.calender_add_event_fragment)
    }
}