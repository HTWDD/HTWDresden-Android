package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.TimetableItemAdapter
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.ui.models.TimetableFreeDayItem
import de.htwdd.htwdresden.ui.models.TimetableHeaderItem
import de.htwdd.htwdresden.ui.viewmodels.fragments.TimetableViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.fragment_timetable.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_timetable_overview.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class TimetableFragment: Fragment() {

    private val viewModel by lazy { getViewModel<TimetableViewModel>() }
    private lateinit var timetableItemAdapter: TimetableItemAdapter
    private val timetableItems: Timetables = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener { request() }
        timetableItemAdapter = TimetableItemAdapter(timetableItems)
        timetableItemAdapter.onItemsLoaded { goToToday(smooth = false) }
        timetableRecycler.adapter = timetableItemAdapter
        request()
    }

    private fun request() {
        viewModel.request()
            .runInUiThread()
            .doOnSubscribe { isRefreshing = true }
            .doOnTerminate { isRefreshing = false }
            .doOnComplete { isRefreshing = false }
            .doOnDispose { isRefreshing = false }
            .subscribe({ timetables ->
                weak { self ->
                    if (timetables.isNotEmpty()) {
                        if (timetables.filterIsInstance<TimetableHeaderItem>().none { it.subheader() == Date().format("dd. MMMM")}) {
                            timetables.add(TimetableHeaderItem(Date().format("EEEE"), Date().format("dd. MMMM")))
                            timetables.add(TimetableFreeDayItem(getString(R.string.timetable_holiday)))
                        }
                        self.timetableItemAdapter.update(timetables)
                    }
                }
            }, {
                error(it)
            })
            .addTo(disposeBag)
    }

    private fun goToToday(smooth: Boolean = false) {
        if (timetableItems.isNotEmpty()) {
            val todayPosition = findTodayPosition()
            if (!smooth) {
                Handler().postDelayed({
                    timetableRecycler.scrollToPosition(todayPosition)
                }, 200)
            } else {
                Handler().postDelayed({
                    timetableRecycler.smoothScrollToPosition(todayPosition)
                }, 200)
            }
        }
    }

    private fun findTodayPosition(): Int {
        var position = 0
        val currentDate = Date().format("dd. MMMM")
        timetableItems.forEach {
            position += 1
            if (it is TimetableHeaderItem) {
                if (it.subheader() == currentDate) {
                    return position
                }
            }
        }
        return timetableItems.size
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_today -> {
            goToToday(smooth = true)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.timetable, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }
}