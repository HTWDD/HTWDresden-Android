package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.Occupancies
import de.htwdd.htwdresden.adapter.RoomOccupancyItemAdapter
import de.htwdd.htwdresden.adapter.RoomOccupancySwipeController
import de.htwdd.htwdresden.db.RoomRealm
import de.htwdd.htwdresden.db.update
import de.htwdd.htwdresden.ui.viewmodels.fragments.RoomOccupancyViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_room_occupancy.*
import kotlinx.android.synthetic.main.layout_empty_view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class RoomOccupancyFragment: Fragment() {

    private val viewModel by lazy { getViewModel<RoomOccupancyViewModel>() }
    private lateinit var adapter: RoomOccupancyItemAdapter
    private val items: Occupancies = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout?.isRefreshing = new }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_room_occupancy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout?.isEnabled = false
        adapter = RoomOccupancyItemAdapter(items)
        roomOccupancyRecycler.adapter = adapter
        adapter.onItemClick {
            findNavController()
                .navigate(R.id.action_overview_page_fragment_to_room_occupancy_detail_page_fragment,
                    bundleOf(RoomOccupancyDetailFragment.BUNDLE_ARG_ID to it.id(), "title" to it.name().toUpperCase(
                        Locale.getDefault())))
        }
        ItemTouchHelper(RoomOccupancySwipeController(adapter)).apply {
            attachToRecyclerView(roomOccupancyRecycler)
        }
        adapter.onEmpty {
            weak { self ->
                self.includeEmptyLayout.toggle(it)
                self.tvIcon.text = "💡"
                self.tvTitle.text = getString(R.string.room_occupancy_no_room)
                self.tvMessage.text = getString(R.string.room_occupancy_add_new_rooms)
                self.btnEmptyAction.apply {
                    show()
                    text = getString(R.string.general_add)
                    click {
                        addNewRoomAction()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.query()
        viewModel.onRoomChanged { items, deletions, insertions, changes ->
            weak { self ->
                self.items.apply {
                    clear()
                    addAll(items)
                }
                if (deletions.isEmpty() && insertions.isEmpty() && changes.isEmpty()) {
                    self.adapter.update(items)
                } else {
                    for (i in deletions.indices.reversed()) {
                        val range = deletions[i]
                        self.adapter.remove(range)
                    }
                    insertions.forEach { self.adapter.insert(it) }
                    changes.forEach { self.adapter.modify(it) }
                }
            }
        }
    }

    private fun request(room: String) {
        viewModel.request(room)
            .runInThread()
            .runInUiThread()
            .doOnSubscribe { isRefreshing = true }
            .doOnComplete { isRefreshing = false }
            .doOnTerminate { isRefreshing = false }
            .doOnTerminate { isRefreshing = false }
            .subscribe({
                weak { _ ->
                    RoomRealm().update(room.uid, room, it)
                }
            }, { error ->
                weak { self ->
                    error.message?.let { sError ->
                        when {
                            sError.contains("404") -> {
                                MaterialDialog(self.context!!).show {
                                    title(R.string.room_timetable_add_no_Lessons)
                                    message(text = String.format(getString(R.string.room_timetable_add_save_error), room))
                                    positiveButton(R.string.general_close)
                                }
                            }

                            else -> error(error)
                        }
                    }
                }
            })
            .addTo(disposeBag)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_add -> {
            addNewRoomAction()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun addNewRoomAction() {
        MaterialDialog(context!!).show {
            title(R.string.room_timetable_add)
            message(R.string.room_timetable_addDialog_message)

            input(waitForPositiveButton = false, maxLength = 7, hintRes = R.string.room_timetable_addDialog_hint) { dialog, text ->
                val inputField = dialog.getInputField()
                val isValid = text.matches(Regex("^[a-zA-Z] [a-zA-Z0-9]{3,5}\$"))
                inputField.error = if (isValid) null else getString(R.string.room_timetable_invalid)
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
            }
            positiveButton(R.string.general_add) {
                request(it.getInputField().text.toString().toLowerCase())
            }
            negativeButton(R.string.general_cancel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.room_occupancy, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }
}