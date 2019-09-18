package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.CampusPlanItemAdapter
import de.htwdd.htwdresden.adapter.CampusPlans
import de.htwdd.htwdresden.ui.viewmodels.fragments.CampusPlanViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_campus_plan.*
import kotlin.properties.Delegates

class CampusPlanFragment: Fragment() {

    private val viewModel by lazy { getViewModel<CampusPlanViewModel>() }
    private lateinit var adapter: CampusPlanItemAdapter
    private val items: CampusPlans = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(
        R.layout.fragment_campus_plan, container, false)


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