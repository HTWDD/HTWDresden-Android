package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.ManagementItemAdapter
import de.htwdd.htwdresden.adapter.Managements
import de.htwdd.htwdresden.ui.viewmodels.fragments.ManagementViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_management.*
import kotlin.properties.Delegates

class ManagementFragment: Fragment(R.layout.fragment_management) {

    private val viewModel by lazy { getViewModel<ManagementViewModel>() }
    private lateinit var adapter: ManagementItemAdapter
    private val items: Managements = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { request() }
        adapter = ManagementItemAdapter(items)
        semesterPlanRecycler.adapter = adapter
        request()
    }

    private fun request() {
        viewModel.request()
            .runInUiThread()
            .doOnSubscribe { isRefreshing = true }
            .doOnTerminate { isRefreshing = false }
            .subscribe({ managements ->
                weak { self ->
                    self.adapter.update(managements)
                }
            }, {
                error(it)
            })
            .addTo(disposeBag)
    }
}