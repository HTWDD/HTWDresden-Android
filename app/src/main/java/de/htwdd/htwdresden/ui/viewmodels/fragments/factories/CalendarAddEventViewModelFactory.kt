package de.htwdd.htwdresden.ui.viewmodels.fragments.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.htwdd.htwdresden.ui.viewmodels.fragments.CalenderAddEventViewModel

class CalendarAddEventViewModelFactory(private val timetableId: String) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalenderAddEventViewModel(timetableId) as T
    }
}
