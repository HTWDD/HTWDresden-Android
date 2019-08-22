package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.ExamItemAdapter
import de.htwdd.htwdresden.adapter.Exams
import de.htwdd.htwdresden.ui.viewmodels.fragments.ExamsViewModel
import de.htwdd.htwdresden.utils.extensions.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_exams.*
import kotlin.properties.Delegates

class ExamsFragment: Fragment() {

    // region - Properties
    private val disposable = CompositeDisposable()
    private lateinit var viewModel: ExamsViewModel
    private lateinit var examItemAdapter: ExamItemAdapter
    private val examItems: Exams = ArrayList()
    private var isRefreshing: Boolean by Delegates.observable(true) { _, _, new ->
        weak { self -> self.swipeRefreshLayout.isRefreshing = new }
    }
    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener { request() }
        examItemAdapter = ExamItemAdapter(examItems)
        examableRecycler.adapter = examItemAdapter
        viewModel = ViewModelProviders.of(this).get(ExamsViewModel::class.java)
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
                    self.emptyView.toggle(exams.isEmpty())
                    self.examItemAdapter.update(exams)
                }
            }, {
                error(it)
                weak { self ->
                    self.errorView.toggle(true)
                    self.addStudyGroup.click {
                        TODO("Link to StudyGroup")
                    }
                }
            }).addTo(disposable)
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}