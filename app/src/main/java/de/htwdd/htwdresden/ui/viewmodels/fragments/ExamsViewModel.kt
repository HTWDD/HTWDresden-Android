package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Exams
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.Exam
import de.htwdd.htwdresden.ui.models.ExamItem
import de.htwdd.htwdresden.ui.models.ExamWarningItem
import de.htwdd.htwdresden.ui.models.TimetableWarningItem
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class ExamsViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Exams> {
        val auth = cph.getStudyAuth() ?: return Observable.error(Exception("No Credentials"))

        return RestApi.examEndpoint.exams(auth.graduation, auth.major, auth.studyYear, auth.group)
            .runInThread(Schedulers.io())
            .map { jExams -> jExams.map { jExam -> Exam.from(jExam) } }
            .map { it.sortedWith(compareBy { it }) }
            .map { exams ->
                val result = Exams()
                if (requestNotes().blockingSingle().isNotEmpty()){
                    result.add(ExamWarningItem(requestNotes().blockingSingle()))
                }
                result.addAll(exams.map { ExamItem(it) }.toCollection(ArrayList()) as Exams)
                result
            }

    }

    private fun requestNotes(): Observable<String> {
        return RestApi
            .docsEndpoint
            .notes(Locale.getDefault().language)
            .runInThread(Schedulers.io())
            .map { jNotes -> jNotes.timetable }
    }
}