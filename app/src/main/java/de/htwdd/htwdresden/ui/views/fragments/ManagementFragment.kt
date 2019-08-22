package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.ManagementItemAdapter
import de.htwdd.htwdresden.adapter.Managements
import de.htwdd.htwdresden.ui.viewmodels.fragments.ManagementViewModel
import de.htwdd.htwdresden.utils.extensions.error
import de.htwdd.htwdresden.utils.extensions.runInUiThread
import de.htwdd.htwdresden.utils.extensions.weak
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_management_layout.*
import kotlin.properties.Delegates

class ManagementFragment: Fragment() {

    // region - Properties
    private val disposable = CompositeDisposable()
    private lateinit var viewModel: ManagementViewModel
    private lateinit var managementItemAdapter: ManagementItemAdapter
    private val managementItems: Managements = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }
    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_management_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener { request() }
        managementItemAdapter = ManagementItemAdapter(managementItems)
        semesterPlanRecycler.adapter = managementItemAdapter
        viewModel = ViewModelProviders.of(this).get(ManagementViewModel::class.java)
        request()
    }

    private fun request() {
        viewModel.request()
            .runInUiThread()
            .doOnSubscribe { isRefreshing = true }
            .doOnTerminate { isRefreshing = false }
            .subscribe({ managements ->
                weak { self ->
                    self.managementItemAdapter.update(managements)
                }
            }, {
                error(it)
            })
            .addTo(disposable)
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}