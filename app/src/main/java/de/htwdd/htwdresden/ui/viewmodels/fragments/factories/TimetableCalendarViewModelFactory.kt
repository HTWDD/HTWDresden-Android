package de.htwdd.htwdresden.ui.viewmodels.fragments.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.htwdd.htwdresden.ui.views.fragments.TimetableCalendarViewModel

class TimetableCalendarViewModelFactory(private val calenderType: Int) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TimetableCalendarViewModel(calenderType) as T
    }
}
