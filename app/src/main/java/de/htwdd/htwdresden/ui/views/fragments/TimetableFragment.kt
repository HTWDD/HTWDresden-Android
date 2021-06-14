package de.htwdd.htwdresden.ui.views.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract.Calendars
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.updateListItems
import com.google.android.material.tabs.TabLayout
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.SectionsPagerAdapter
import de.htwdd.htwdresden.adapter.TimetableItemAdapter
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.ui.viewmodels.fragments.TimetableViewModel
import de.htwdd.htwdresden.ui.views.fragments.TimetableCalendarFragment.Companion.CALENDAR_CURRENT_WEEK
import de.htwdd.htwdresden.ui.views.fragments.TimetableCalendarFragment.Companion.CALENDAR_NEXT_WEEK
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.layout_empty_view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates

class TimetableFragment: Fragment(R.layout.fragment_timetable) {

    private val PERMISSION_REQUEST_CODE: Int = 10
    private val defaultPattern = "dd.MM.yyyy"
    private val viewModel by lazy { getViewModel<TimetableViewModel>() }
    private lateinit var adapter: TimetableItemAdapter
    private val items: Timetables = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }
    private var isCalendarView: Boolean = false
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }
    private val smoothScroller: SmoothScroller by lazy {
        object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        activity ?: return
        setHasOptionsMenu(true)
        configureViewPager()
        setup()
        if(isCalendarView) {
            handleViewChange()
        } else {
            request()
        }
        adapter.onItemClick {
            if(it is TimetableItem) {
                val destinationTitle = if(it.item.createdByUser) activity?.resources?.getString(R.string.timetable_edit_activity_title) ?: "" else activity?.resources?.getString(R.string.timetable_event) ?: ""
                findNavController()
                    .navigate(R.id.action_calender_add_event_fragment, bundleOf(CalendarAddEventFragment.ARG_ID to it.item.id, CalendarAddEventFragment.ARG_TITLE to destinationTitle))
            }
        }
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

    private fun configureViewPager() {
        val tabs: TabLayout? = view?.findViewById(R.id.tabs)
        val viewPager = view?.findViewById<ViewPager>(R.id.viewPager)
        viewPager?.adapter = SectionsPagerAdapter(activity as Context, childFragmentManager).apply {
            clear()
            addFragment(
                TimetableCalendarFragment.newInstance(CALENDAR_CURRENT_WEEK),
                R.string.mensa_tab_this_week
            )
            addFragment(
                TimetableCalendarFragment.newInstance(CALENDAR_NEXT_WEEK),
                R.string.mensa_tab_next_week
            )
        }
        tabs?.setupWithViewPager(viewPager)
    }

    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { request() }
        adapter = TimetableItemAdapter(items)
        adapter.onItemsLoaded { goToToday(smooth = false) }
        timetableRecycler.adapter = adapter
        adapter.onEmpty {
            weak { self ->
                self.includeEmptyLayout.toggle(it)
                self.tvIcon.text    = getString(R.string.exams_no_results_icon)
                self.tvTitle.text   = getString(R.string.timetable_no_results_title)
                self.tvMessage.text = getString(R.string.timetable_no_results_message)
            }
        }
    }

    private fun request() {
        try {
            viewModel.request()
                .runInUiThread()
                .doOnSubscribe { isRefreshing = true }
                .doOnTerminate { isRefreshing = false }
                .doOnComplete { isRefreshing = false }
                .doOnDispose { isRefreshing = false }
                .subscribe({ timetables ->
                    weak { self ->
                        if (timetables.isNotEmpty()) {
                            self.adapter.update(timetables)
                        }
                    }
                }, {
                    error(it)
                    weak { self ->
                        self.includeEmptyLayout?.show()
                        self.tvIcon?.text    = getString(R.string.exams_no_results_icon)
                        self.tvTitle?.text   = getString(R.string.exams_no_credentials_title)
                        self.tvMessage?.text = getString(R.string.timetable_no_credentials_message)
                        self.btnEmptyAction?.apply {
                            show()
                            text = getString(R.string.general_add)
                            click {
                                self.findNavController().navigate(R.id.action_to_study_group_page_fragment)
                            }
                        }
                    }
                })
                .addTo(disposeBag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToToday(smooth: Boolean = false) {
        if (items.isNotEmpty()) {
            val todayPosition = findTodayPosition()
            val targetPosition = if (todayPosition == 0) todayPosition else (todayPosition - 1) % items.size
            if (!smooth) {
                Handler().post {
                    (timetableRecycler.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                        targetPosition,
                        0
                    )
                }
            } else {
                Handler().postDelayed({
                    smoothScroller.targetPosition = targetPosition
                    timetableRecycler.layoutManager?.startSmoothScroll(smoothScroller)
                }, 25)
            }
        }
    }

    private fun findTodayPosition(): Int {
        val first = items.firstOrNull().guard { return 0 }

        (first as? TimetableHeaderItem)?.let {
            if (Date() < it.subheader()) {
                return 0
            }
        }
        var position = 0
        val currentDate = Date()
        items.forEach {
            position += 1
            if (it is TimetableHeaderItem) {
                if (it.subheader().format(defaultPattern) == currentDate.format(defaultPattern) || it.subheader().after(currentDate)) {
                    return position
                }
            }
        }
        return items.size
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val todayItem = menu.findItem(R.id.menu_today)
        val exportItem = menu.findItem(R.id.menu_export)
        val calendarItem = menu.findItem(R.id.menu_calendar)
        todayItem.isVisible = !isCalendarView
        exportItem.isVisible = isCalendarView
        if(isCalendarView) {
            calendarItem.setIcon(R.drawable.ic_list)
        } else {
            calendarItem.setIcon(R.drawable.ic_calendar)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_today -> {
            goToToday(smooth = true)
            true
        }
        R.id.menu_calendar -> {
            isCalendarView = !isCalendarView
            handleViewChange()
            activity?.invalidateOptionsMenu()
            true
        }
        R.id.menu_add_event -> {
            onEventClick()
            true
        }
        R.id.menu_export -> {
            handleExportClick()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showExportMenu() {
        (activity as Context?)?.let {
            MaterialDialog(it, BottomSheet(LayoutMode.WRAP_CONTENT)).title(R.string.export_title)
                .message(R.string.export_message).show {
                    listItems(R.array.export_options) { _, index, text ->
                        showCalendarList(index)
                    }
                }
        }
    }

    private fun handleExportClick() {
        (activity as Context?)?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.READ_CALENDAR
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                showExportMenu()
            }
        }

    }

    private fun showCalendarList(optionIndex: Int) {
        (activity as Context?)?.let { context ->
            val calendars = getCalendars(context)
            MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).title(R.string.export_calendar_list).show {
                    listItems(items = calendars.map { it.value }) { _, index, _ ->
                        (activity as Context?)?.let {
                            val calendarId = calendars.keys.toCollection(ArrayList())[index]
                            try {
                                viewModel.exportCalendar(context.contentResolver, optionIndex, calendarId)
                                Toast.makeText(it, R.string.export_success_message, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(it, R.string.export_failure_message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }
    }

    private fun onEventClick() {
        (activity as Context?)?.let { context ->
            MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).title(R.string.timetable_add_event).show {
                listItems(res = R.array.addEventOptions) { _, index, _ ->
                    when(index) {
                        0 -> addElectiveTimetable()
                        1 -> addOwnEvent()
                    }
                }
            }
        }
    }

    private fun addElectiveTimetable() {
        lifecycleScope.launch {
            (activity as Context?)?.let { context ->
                val initialDialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).title(R.string.timetable_add_elective_lecture).show {
                    customView(viewRes = R.layout.dialog_progress_bar, scrollable = true)
                }
                val timetables = kotlin.runCatching { viewModel.getElectiveTimetables().map { Timetable.from(it) }.filter { it.type.isElective()} }.getOrNull()
                initialDialog.dismiss()
                if(timetables==null) {
                    delay(500)
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                } else {
                    MaterialDialog(context, BottomSheet(LayoutMode.MATCH_PARENT)).title(R.string.timetable_add_elective_lecture).show {
                        listItems(items = timetables.map { it.name }.sortedBy { it }.distinct()) { _, _, text ->
                            val timetablesToAdd = timetables.toCollection(ArrayList()).filter { it.name == text }
                            timetablesToAdd.forEach {
                                it.createdByUser = true
                                TimetableRealm().updateAsync(it) {}
                                Toast.makeText(context, R.string.timetable_event_added, Toast.LENGTH_SHORT).show()
                                lifecycleScope.launch {
                                    delay(1000)
                                    onResume()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addOwnEvent() {
        val destinationTitle = activity?.resources?.getString(R.string.timetable_add_event) ?: ""
        findNavController()
            .navigate(R.id.action_calender_add_event_fragment, bundleOf(CalendarAddEventFragment.ARG_TITLE to destinationTitle))
    }

    private fun handleViewChange() {
        includeEmptyLayout.hide(isCalendarView)
        swipeRefreshLayout.isEnabled = !isCalendarView
        swipeRefreshLayout.isRefreshing = false
        timetableRecycler.toggle(!isCalendarView)
        viewPager.toggle(isCalendarView)
        if(!isCalendarView) {
            request()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(!grantResults.contains(-1) && requestCode == PERMISSION_REQUEST_CODE ) {
            showExportMenu()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.timetable, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getCalendars(context: Context): HashMap<Long,String> {
        val fields = arrayOf(Calendars.CALENDAR_DISPLAY_NAME, Calendars._ID)
        val uri = Uri.parse("content://com.android.calendar/calendars");
        val calendars = HashMap<Long,String>()
        val cursor = context.contentResolver.query(uri, fields, null, null, null) ?: return calendars

        try {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val name: String = cursor.getString(0)
                    val id = cursor.getString(1).toLongOrNull()
                    if(id!=null) calendars[id] = name
                }
            }
            cursor.close()
        } catch (ex: Error) {
            ex.printStackTrace()
        }
        finally {
            cursor.close()
        }
        return calendars
    }
}