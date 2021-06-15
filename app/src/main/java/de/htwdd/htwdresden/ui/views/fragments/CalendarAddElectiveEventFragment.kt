package de.htwdd.htwdresden.ui.views.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.OverviewItemAdapter
import de.htwdd.htwdresden.adapter.Overviews
import de.htwdd.htwdresden.adapter.TimetableItemAdapter
import de.htwdd.htwdresden.adapter.Timetables
import de.htwdd.htwdresden.databinding.FragmentSearchElectiveBinding
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.ui.viewmodels.fragments.OverviewViewModel
import de.htwdd.htwdresden.ui.viewmodels.fragments.TimetableViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.fragment_overview.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_search_elective.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates
import android.view.MenuInflater
import androidx.recyclerview.widget.RecyclerView


class CalendarAddElectiveEventFragment: Fragment() {

    companion object {
        const val ARG_TITLE = "title"
    }

    private var searchMenu: MenuItem? = null
    private val viewModel by lazy { getViewModel<TimetableViewModel>() }
    private lateinit var adapter: OverviewItemAdapter
    private val items: Overviews = Overviews()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflateDataBinding<FragmentSearchElectiveBinding>(R.layout.fragment_search_elective, container).apply {
            timetableViewModel = viewModel
        }

        //hide or show search view when scrolling
       /* binding.electivesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                searchMenu?.let {
                    if (dy<-20 && it.isActionViewExpanded){
                        it.collapseActionView()
                    } else if (dy > 20 && !it.isActionViewExpanded){
                        it.expandActionView()
                    } else {
                        //do nothing
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })*/

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()

        viewModel.showError.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                }
                viewModel.resetShowError()
            }
        })

        viewModel.filteredElectives.observe(viewLifecycleOwner, Observer {
            it?.let {
                items.apply {
                    clear()
                    addAll(it)
                    adapter.notifyDataSetChanged()
                    isRefreshing = false
                }
            }


        })
    }

    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { loadElectiveTimetables() }
        adapter = OverviewItemAdapter(items)
        electivesRecycler.adapter = adapter
        adapter.onItemClick { item ->
            when (item) {
                is OverviewScheduleItem     -> {
                    val elective = (item as OverviewScheduleItem).item
                    elective.createdByUser = true
                    TimetableRealm().updateAsync(elective) {}
                    Toast.makeText(context, R.string.timetable_event_added, Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        //delay(1000)
                        findNavController().popBackStack()
                    }
                }
            }
        }

        loadElectiveTimetables()
    }


    private fun loadElectiveTimetables() {
        isRefreshing = true
        viewModel.loadElectiveTimetables()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(
            R.menu.menu_search,
            menu
        )
        searchMenu = menu.findItem(R.id.search)
        // Associate searchable configuration with the SearchView
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchMenu?.actionView as? androidx.appcompat.widget.SearchView
        searchView?.setSearchableInfo(
            searchManager
                .getSearchableInfo(requireActivity().componentName)
        )
        searchView?.maxWidth = Int.MAX_VALUE

        // listening to search query text change
        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.setQuery("", false);
                searchView.isIconified = true;
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                viewModel.setSearchTerm(query)
                return true
            }
        })

        //the back button in searchview closes the fragment instantly
        searchMenu?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                findNavController().popBackStack()
               return true
            }
        });
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.search) {
            true
        } else super.onOptionsItemSelected(item)
    }


}