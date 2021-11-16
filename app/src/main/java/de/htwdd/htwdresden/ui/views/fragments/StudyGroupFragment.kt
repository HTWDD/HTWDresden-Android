package de.htwdd.htwdresden.ui.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Swipeable
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.ui.viewmodels.fragments.StudyGroupViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_study_group.*
import kotlin.properties.Delegates.observable

@SuppressLint("SetTextI18n")
class StudyGroupFragment: Fragment(R.layout.fragment_study_group), Swipeable {

    companion object {
        const val ARG_IS_BOARDING = "isBoarding"
        private var delegate: SwipeDelegate? = null
        fun newInstance(delegate: SwipeDelegate? = null) = StudyGroupFragment().apply {
            this@Companion.delegate = delegate
            arguments = bundleOf(ARG_IS_BOARDING to true)
        }
    }

    private val viewModel by lazy { getViewModel<StudyGroupViewModel>() }

    inner class State {
        val years = ArrayList<StudyYear>()
        var year: StudyYear? by observable<StudyYear?>(null) { _, _, newValue ->
            btnMajor.isEnabled = newValue != null
            major = null
            tvS.text = "${newValue?.studyYear?.plus(2000)}"
        }
        val majors: ArrayList<StudyCourse>
                get() {
                    if (years.isEmpty() || year == null) { return ArrayList() }
                    return years.first { it.studyYear == year?.studyYear }.studyCourses.filter { it.name.isNotEmpty() }.toCollection(ArrayList())
                }
        var major: StudyCourse? by observable<StudyCourse?>(null) { _, _, newValue ->
            btnGroup.isEnabled = newValue != null
            group = null
            tvMajor.text = if (newValue == null) { "" } else { "${newValue.studyCourse} | ${newValue.name}" }
        }
        val groups: ArrayList<StudyGroup>
            get() {
                if (majors.isEmpty() || major == null) { return ArrayList() }
                return majors.first { it.studyCourse == major?.studyCourse }.studyGroups.toCollection(ArrayList())
            }
        var group: StudyGroup? by observable<StudyGroup?>(null) { _, _, newValue ->
            tvGroup.text = if (newValue == null) { "" } else { "${newValue.studyGroup} | ${newValue.name}" }

            if (year != null && major != null && newValue != null) {
                Handler().postDelayed({
                    viewModel.saveToken("${year?.studyYear}:${major?.studyCourse}:${newValue.studyGroup}:${newValue.name}")
                    if (arguments?.getBoolean(ARG_IS_BOARDING) == false) {
                        Timetable.deleteAllTimetable()
                        findNavController().popBackStack()
                    } else {
                        delegate?.moveNext()
                    }
                }, 750)
            }
        }
    }
    private val state by lazy { State() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        btnLater.toggle(arguments?.getBoolean(ARG_IS_BOARDING) == false)
        try {
            viewModel
                .request()
                .runInUiThread()
                .subscribe {
                    weak { self ->
                        self.state.years.apply {
                            clear()
                            addAll(it)
                        }
                        self.btnYear?.isEnabled = it.isNotEmpty()
                    }
                }
                .addTo(disposeBag)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        btnYear?.click {
            context?.let {
                MaterialDialog(it, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.year)
                    listItems(items = state.years.map { "${it.studyYear + 2000}"}, selection = { _, index, _ ->
                        state.year = state.years[index]
                    })
                }
            }
        }

        btnMajor?.click {
            context?.let {
                MaterialDialog(it, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.major)
                    listItems(items = state.majors.map { "${it.studyCourse} | ${it.name.defaultWhenNull("---")}" }) { _, index, _ ->
                        state.major = state.majors[index]
                    }
                }
            }
        }

        btnGroup?.click {
            context?.let {
                MaterialDialog(it, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.group)
                    listItems(items = state.groups.map { "${it.studyGroup} | ${it.name.defaultWhenNull("---")}" }) { _, index, _ ->
                        state.group = state.groups[index]
                    }
                }
            }
        }

        btnLater?.click {
            findNavController().popBackStack()
        }
    }
}