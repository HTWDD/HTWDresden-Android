package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Exams
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.Exam
import de.htwdd.htwdresden.ui.models.ExamItem
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ExamsViewModel: ViewModel() {

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Exams> {
        val auth = cph.getStudyAuth() ?: return Observable.error(Exception("No Credentials"))

        return RestApi.examService.exams(auth.graduation, auth.major, auth.studyYear, auth.group)
            .runInThread(Schedulers.io())
            .map { jExams -> jExams.map { jExam -> Exam.from(jExam) } }
            .map { exams -> exams.map { ExamItem(it) }.sortedBy { it }.toCollection(ArrayList()) as Exams }
    }
}