package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.ExamItemAdapter
import de.htwdd.htwdresden.adapter.Exams
import de.htwdd.htwdresden.ui.viewmodels.fragments.ExamsViewModel
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import kotlinx.android.synthetic.main.fragment_exams.*
import kotlinx.android.synthetic.main.layout_empty_view.*
import kotlin.properties.Delegates

class ExamsFragment: Fragment(R.layout.fragment_exams) {

    private val viewModel by lazy { getViewModel<ExamsViewModel>() }
    private lateinit var adapter: ExamItemAdapter
    private val items: Exams = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
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

    private fun setup() {
        swipeRefreshLayout.setOnRefreshListener { request() }
        adapter = ExamItemAdapter(items)
        examableRecycler.adapter = adapter
        adapter.onEmpty {
            weak { self ->
                self.includeEmptyLayout.toggle(it)
                self.tvIcon.text    = getString(R.string.exams_no_results_icon)
                self.tvTitle.text   = getString(R.string.exams_no_results_title)
                self.tvMessage.text = getString(R.string.exams_no_results_message)
            }
        }
        request()
    }

    private fun request() {
        viewModel
            .request()
            .runInUiThread()
            .doOnSubscribe { isRefreshing = true }
            .doOnTerminate { isRefreshing = false }
            .subscribe({ exams ->
                weak { self ->
                    self.adapter.update(exams)
                }
            }, {
                error(it)
                weak { self ->
                    self.includeEmptyLayout.show()
                    self.tvIcon.text    = getString(R.string.exams_no_results_icon)
                    self.tvTitle.text   = getString(R.string.exams_no_credentials_title)
                    self.tvMessage.text = getString(R.string.exams_no_credentials_message)
                    self.btnEmptyAction.apply {
                        show()
                        text = getString(R.string.general_add)
                        click {
                            self.findNavController().navigate(R.id.action_to_study_group_page_fragment)
                        }
                    }
                }
            }).addTo(disposeBag)
    }
}