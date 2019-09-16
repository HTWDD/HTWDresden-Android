package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.OverviewItemAdapter
import de.htwdd.htwdresden.adapter.Overviews
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.ui.viewmodels.fragments.OverviewViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlin.properties.Delegates

class OverviewFragment: Fragment() {

    private val viewModel by lazy { getViewModel<OverviewViewModel>() }
    private lateinit var overviewItemAdapter: OverviewItemAdapter
    private val overviewItems: Overviews = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { request() }
        overviewItemAdapter = OverviewItemAdapter(overviewItems)
        overviewRecycler.adapter = overviewItemAdapter
        overviewItemAdapter.onClick { item ->
            when (item) {
                is OverviewMensaItem        -> findNavController().navigate(R.id.action_canteen_page_fragment_to_meals_pager_page_fragment)
                is OverviewStudyGroupItem   -> findNavController().navigate(R.id.action_to_study_group_page_fragment)
                is OverviewFreeDayItem,
                is OverviewScheduleItem     -> findNavController().navigate(R.id.timetable_page_fragment)
                is OverviewLoginItem        -> findNavController().navigate(R.id.action_to_login_page_fragment)
                is OverviewGradeItem        -> findNavController().navigate(R.id.grades_page_fragment)
            }
        }
        request()
    }

    private fun request() {
        viewModel
            .request()
            .runInUiThread()
            .doOnSubscribe { isRefreshing = true }
            .doOnDispose { isRefreshing = false }
            .doOnTerminate { isRefreshing = false }
            .doOnComplete { isRefreshing = false }
            .subscribe({ overviews ->
                weak { self ->
                    self.overviewItemAdapter.update(overviews)
                }
            }, {
                error(it)
            })
            .addTo(disposeBag)
    }
}