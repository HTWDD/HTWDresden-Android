package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Exams
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.network.services.ExamService
import de.htwdd.htwdresden.types.studyGroups.StudyData
import de.htwdd.htwdresden.ui.models.Exam
import de.htwdd.htwdresden.ui.models.ExamItem
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.verbose
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class ExamsViewModel: ViewModel() {

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Exams> {
        val realm = Realm.getDefaultInstance()
        val studyData = realm.where(StudyData::class.java).findFirst() ?: return Observable.error(Exception("No Credentials"))

        return RestApi.examService.exams("B", studyData.studyCourse, studyData.studyYear.toString(), "6")
            .runInThread(Schedulers.io())
            .map { jExams -> jExams.map { jExam -> Exam.from(jExam) } }
            .map { exams -> exams.map { ExamItem(it) }.sortedBy { it }.toCollection(ArrayList()) as Exams }
    }

}