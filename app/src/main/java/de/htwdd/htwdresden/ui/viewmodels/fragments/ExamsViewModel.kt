package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Exams
import de.htwdd.htwdresden.classes.API.ExamService
import de.htwdd.htwdresden.ui.models.Exam
import de.htwdd.htwdresden.ui.models.ExamItem
import de.htwdd.htwdresden.utils.extensions.debug
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.verbose
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ExamsViewModel: ViewModel() {

    init {
        verbose("init()")
    }

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Exams> {
        verbose("request()")
        return ExamService.instance.exams("B", "041", "17", "6")
            .debug()
            .runInThread(Schedulers.io())
            .map { jExams -> jExams.map { jExam -> Exam.from(jExam) } }
            .map { exams -> exams.map { ExamItem(it) }.sortedBy { it }.toCollection(ArrayList()) as Exams }
    }

}