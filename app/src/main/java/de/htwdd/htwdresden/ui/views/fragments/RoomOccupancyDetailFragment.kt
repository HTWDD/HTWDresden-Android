package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.DetailOccupancies
import de.htwdd.htwdresden.adapter.RoomOccupancyDetailItemAdapter
import de.htwdd.htwdresden.ui.viewmodels.fragments.RoomOccupancyDetailViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_room_occupancy_detail.*

class RoomOccupancyDetailFragment: Fragment() {

    companion object {
        const val BUNDLE_ARG_ID = "RoomDbId"
    }

    private val viewModel by lazy { getViewModel<RoomOccupancyDetailViewModel>() }
    private val roomId: String? by lazy { arguments?.getString(BUNDLE_ARG_ID) }
    private lateinit var roomOccupancyDetailItemAdapter: RoomOccupancyDetailItemAdapter
    private val roomOccupancyDetailItems: DetailOccupancies = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_room_occupancy_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomOccupancyDetailItemAdapter = RoomOccupancyDetailItemAdapter(roomOccupancyDetailItems)
        roomOccupancyDetailRecycler.adapter = roomOccupancyDetailItemAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.query(roomId)
            .runInUiThread()
            .subscribe({ items ->
                weak { self ->
                    self.roomOccupancyDetailItemAdapter.update(items)
                }
            }, { error(it) })
            .addTo(disposeBag)
    }
}