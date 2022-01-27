package de.htwdd.htwdresden.ui.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.GradeItemAdapter
import de.htwdd.htwdresden.adapter.Grades
import de.htwdd.htwdresden.ui.viewmodels.fragments.GradesViewModel
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import kotlinx.android.synthetic.main.fragment_grades.*
import kotlinx.android.synthetic.main.layout_empty_view.*
import kotlin.properties.Delegates

class GradesFragment: Fragment(R.layout.fragment_grades) {

    private val viewModel by lazy { getViewModel<GradesViewModel>() }
    private lateinit var adapter: GradeItemAdapter
    private val items: Grades = ArrayList()
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    @SuppressLint("SetTextI18n")
    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { request() }
        adapter = GradeItemAdapter(items)
        gradesRecycler.adapter = adapter
        adapter.onEmpty {
            weak { self ->
                self.includeEmptyLayout.toggle(it)
                self.tvIcon.text    = "\uD83D\uDE35"
                self.tvTitle.text   = getString(R.string.exams_no_grades_title)
                self.tvMessage.text = getString(R.string.exams_no_grades_message)
            }
        }

        request()

        cph.onChanged()
            .debug()
            .runInUiThread()
            .subscribe {
                when (it) {
                    is CryptoSharedPreferencesHolder.SubscribeType.AuthToken -> {
                        weak { self ->
                            self.request()
                        }
                    }
                }
            }
            .addTo(disposeBag)
    }

    private fun request() {
        try {
            viewModel.requestGrades()
                .runInUiThread()
                .doOnSubscribe { isRefreshing = true }
                .doOnTerminate { isRefreshing = false }
                .subscribe(
                    { grades ->
                    weak { self ->
                        self.adapter.update(grades)
                    }
                },
                    {
                    error(it)
                    weak { self ->
                        self.includeEmptyLayout?.show()
                        self.tvIcon?.text    = getString(R.string.exams_no_results_icon)
                        self.tvTitle?.text   = getString(R.string.grades_no_credentials_title)
                        self.tvMessage?.text = getString(R.string.grades_no_credentials_message)
                        self.btnEmptyAction?.apply {
                            show()
                            text = getString(R.string.login_with_img)
                            click {
                                self.findNavController().navigate(R.id.action_to_login_page_fragment)
                            }
                        }
                    }
                })
                .addTo(disposeBag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}