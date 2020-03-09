package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.CampusPlanItemAdapter
import de.htwdd.htwdresden.adapter.CampusPlans
import de.htwdd.htwdresden.ui.viewmodels.fragments.CampusPlanViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_campus_plan.*
import kotlin.properties.Delegates

class CampusPlanFragment: Fragment(R.layout.fragment_campus_plan) {

    private val viewModel by lazy { getViewModel<CampusPlanViewModel>() }
    private lateinit var adapter: CampusPlanItemAdapter
    private val items: CampusPlans = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener { request() }
        adapter = CampusPlanItemAdapter(items)
        campusPlanRecycler.adapter = adapter
        request()
    }

    private fun request() {
        viewModel.request()
            .runInUiThread()
            .doOnSubscribe { isRefreshing = true }
            .doOnDispose { isRefreshing = false }
            .doOnTerminate { isRefreshing = false }
            .doOnComplete { isRefreshing = false }
            .subscribe({ campusPlans ->
                weak { self ->
                    self.adapter.update(campusPlans)
                }
            }, {
                error(it)
            })
            .addTo(disposeBag)
    }
}