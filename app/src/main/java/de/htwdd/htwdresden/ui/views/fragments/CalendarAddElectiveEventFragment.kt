package de.htwdd.htwdresden.ui.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.OverviewItemAdapter
import de.htwdd.htwdresden.adapter.Overviews
import de.htwdd.htwdresden.adapter.TimetableItemAdapter
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.ui.viewmodels.fragments.OverviewViewModel
import de.htwdd.htwdresden.ui.viewmodels.fragments.TimetableViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class CalendarAddElectiveEventFragment: Fragment(R.layout.fragment_overview) {

    companion object {
        const val ARG_TITLE = "title"
    }

    private val viewModel by lazy { getViewModel<TimetableViewModel>() }
    private lateinit var adapter: OverviewItemAdapter
    private val items: Overviews = Overviews()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { addElectiveTimetable() }
        adapter = OverviewItemAdapter(items)
        overviewRecycler.adapter = adapter
        adapter.onItemClick { item ->
            when (item) {
                is OverviewScheduleItem     -> {
                    val elective = (item as OverviewScheduleItem).item
                    elective.createdByUser = true
                    TimetableRealm().updateAsync(elective) {}
                    Toast.makeText(context, R.string.timetable_event_added, Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        delay(1000)
                        onResume()
                    }
                }
            }
        }
        addElectiveTimetable()
    }


    private fun addElectiveTimetable() {
        isRefreshing = true
        lifecycleScope.launch {
            (activity as Context?)?.let { context ->
                val timetables = kotlin.runCatching { viewModel.getElectiveTimetables().map { Timetable.from(it) }.filter { it.type.isElective()} }.getOrNull()
                if(timetables==null) {
                    delay(500)
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                } else {
                    items.apply { addAll(timetables.sortedBy { it.name }.distinct().map {OverviewScheduleItem(it, true)} ) }
                    adapter.notifyDataSetChanged()

                }
                isRefreshing = false
            }
        }
    }

}