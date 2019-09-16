package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.TimetableItemAdapter
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.ui.models.TimetableHeaderItem
import de.htwdd.htwdresden.ui.viewmodels.fragments.TimetableViewModel
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.layout_empty_view.*
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
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        request()
    }

    override fun onResume() {
        super.onResume()
        cph.onChanged()
            .runInUiThread()
            .subscribe {
                when (it) {
                    is CryptoSharedPreferencesHolder.SubscribeType.StudyToken -> {
                        weak { self ->
                            self.request()
                        }
                    }
                }
            }
            .addTo(disposeBag)
    }

    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { request() }
        timetableItemAdapter = TimetableItemAdapter(timetableItems)
        timetableItemAdapter.onItemsLoaded { goToToday(smooth = false) }
        timetableRecycler.adapter = timetableItemAdapter
        timetableItemAdapter.onEmpty {
            weak { self ->
                self.includeEmptyLayout.toggle(it)
                self.tvIcon.text    = getString(R.string.exams_no_results_icon)
                self.tvTitle.text   = getString(R.string.timetable_no_results_title)
                self.tvMessage.text = getString(R.string.timetable_no_results_message)
            }
        }
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
                        self.timetableItemAdapter.update(timetables)
                    }
                }
            }, {
                error(it)
                weak { self ->
                    self.includeEmptyLayout.show()
                    self.tvIcon.text    = getString(R.string.exams_no_results_icon)
                    self.tvTitle.text   = getString(R.string.exams_no_credentials_title)
                    self.tvMessage.text = getString(R.string.timetable_no_credentials_message)
                    self.btnEmptyAction.apply {
                        show()
                        text = getString(R.string.general_add)
                        click {
                            self.findNavController().navigate(R.id.action_to_study_group_page_fragment)
                        }
                    }
                }
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
        val first = timetableItems.firstOrNull().guard { return 0 }

        (first as? TimetableHeaderItem)?.let {
            if (Date() < it.subheader()) {
                return 0
            }
        }
        var position = 0
        val currentDate = Date()
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