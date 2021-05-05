package de.htwdd.htwdresden.ui.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.timePicker
import com.afollestad.materialdialogs.list.listItems
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.databinding.CalenderAddEventFragmentBinding
import de.htwdd.htwdresden.ui.viewmodels.fragments.factories.CalendarAddEventViewModelFactory
import de.htwdd.htwdresden.utils.extensions.calendar
import de.htwdd.htwdresden.utils.extensions.inflateDataBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CalendarAddEventFragment : Fragment(), DialogInterface  {

    companion object {
        const val ARG_ID = "id"
        const val ARG_TITLE = "title"
    }

    private val viewModel: CalenderAddEventViewModel by viewModels { CalendarAddEventViewModelFactory(timetableId) }
    private val timetableId by lazy { arguments?.getString(ARG_ID)!! }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflateDataBinding<CalenderAddEventFragmentBinding>(R.layout.calender_add_event_fragment, container).apply {
            calenderAddEventViewModel = viewModel
        }
        binding.dialogInterface = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun showDialog(position: Int) {
        activity?.let {
            val dialog = MaterialDialog(it as Context, BottomSheet(LayoutMode.WRAP_CONTENT))
            when(position) {
                0 -> {
                    dialog.title(R.string.timetable_edit_lessonType_description).show {
                        listItems(R.array.lesson_type) { _, _, text ->
                            viewModel.lessonType.set(text.toString())
                        }
                    }
                }
                1 -> {
                    dialog.title(R.string.timetable_edit_lesson_rotation_description).show {
                        listItems(R.array.lesson_week) { _, _, text ->
                            val isOneTimeOption = text == resources.getString(R.string.one_time)
                            viewModel.handleWeekRotation(text, isOneTimeOption)
                        }
                    }
                }
                2 -> {
                    dialog.title(R.string.timetable_edit_lesson_week_day_description).show {
                        listItems(R.array.days) { _, _, text ->
                            viewModel.handleWeekDay(text.toString())
                        }
                    }
                }
                else -> {}
            }
        }

    }

    override fun showTimePicker(isStartTime: Boolean) {
        if(viewModel.isEditable.get()==false) return
        activity?.resources?.let {
            MaterialDialog(activity as Context).show {
                val show24HoursView = it.getBoolean(R.bool.has24Format)
                val currentTime = if(isStartTime) viewModel.lessonDateStart?.calendar else viewModel.lessonDateEnd?.calendar
                timePicker(show24HoursView = show24HoursView, currentTime = currentTime) { _, time ->
                    viewModel.handleTimeChange(isStartTime, time)
                }
            }
        }
    }

    override fun showDatePicker() {
        if(viewModel.isEditable.get()==false) return
        activity?.resources?.let {
            MaterialDialog(activity as Context).show {
                val currentDate = viewModel.lessonExactDay?.calendar
                datePicker(currentDate) { _, date ->
                    viewModel.handleDateChange(date)
                }
            }
        }
    }

    override fun goBack(saveEvent: Boolean) {
        if(saveEvent) {
            try {
                viewModel.createLesson(findNavController())
            } catch (e: Exception) {
                Toast.makeText(activity as Context, "Ups", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.eventRemoveButton -> {
            viewModel.removeEvent()
            (activity as Context?)?.let {
                Toast.makeText(it, R.string.event_removed, Toast.LENGTH_SHORT).show()
            }
            MainScope().launch {
                delay(500)
                findNavController().popBackStack()
            }
            true
        }
        R.id.hideEvent -> {
            (activity as Context?)?.let {
                Toast.makeText(it, R.string.event_hidden, Toast.LENGTH_SHORT).show()
            }
            viewModel.hideEvent(findNavController())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val removeButton = menu.findItem(R.id.eventRemoveButton)
        val hideButton = menu.findItem(R.id.hideEvent)
        if(viewModel.isElective) {
            removeButton.isVisible = false
            hideButton.isVisible = true
        } else if(viewModel.isEditable.get()==true) {
            removeButton.isVisible = true
            hideButton.isVisible = false
        }
        activity?.invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if(viewModel.isEditable.get()==true || viewModel.isElective) {
            inflater.inflate(R.menu.event_menu, menu)
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }
}

interface DialogInterface {
    fun showDialog(position: Int)
    fun showTimePicker(isStartTime: Boolean)
    fun showDatePicker()
    fun goBack(saveEvent: Boolean)
}