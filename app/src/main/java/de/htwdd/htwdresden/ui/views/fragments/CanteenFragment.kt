package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.CanteenItemAdapter
import de.htwdd.htwdresden.adapter.Canteens
import de.htwdd.htwdresden.ui.viewmodels.fragments.CanteenViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_canteen.*
import kotlinx.android.synthetic.main.layout_empty_view.*
import kotlin.properties.Delegates

class CanteenFragment: Fragment() {

    private val viewModel by lazy { getViewModel<CanteenViewModel>() }
    private lateinit var canteenItemAdapter: CanteenItemAdapter
    private val canteenItems: Canteens = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_canteen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { request() }
        canteenItemAdapter = CanteenItemAdapter(canteenItems)
        canteenRecycler.adapter = canteenItemAdapter
        canteenItemAdapter.onItemClick {
            findNavController()
                .navigate(R.id.action_canteen_page_fragment_to_meals_pager_page_fragment,
                bundleOf(MealsPagerFragment.ARG_TITLE to it.name, MealsPagerFragment.ARG_ID to it.id))
        }
        request()
    }

    private fun request() {
        viewModel.request()
            .runInUiThread()
            .doOnSubscribe { isRefreshing = true }
            .doOnTerminate { isRefreshing = false }
            .subscribe({ grades ->
                weak { self ->
                    self.canteenItemAdapter.update(grades)
                }
            }, {
                error(it)
                weak { self ->
                    self.includeEmptyLayout.toggle(true)
                    self.tvTitle.text = self.getString(R.string.info_internet_error)
                    self.tvMessage.text = self.getString(R.string.info_internet_no_connection)
                }
            })
            .addTo(disposeBag)
    }
}