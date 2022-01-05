package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import de.htwdd.htwdresden.adapter.Managements
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.debug
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.ResourceHolder
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class ManagementViewModel: ViewModel() {

    private val rh: ResourceHolder by lazy { ResourceHolder.instance }

    fun request(): Observable<Managements> {
        return Observables.combineLatest(
            requestSemesterPlan().runInThread(),
            loadStudentAdministration().runInThread(),
            loadStura().runInThread(),
            loadPrinicipalOffice().runInThread()) { s, m1, m2, m3 ->
            Managements().apply {
                addAll(s)
                addAll(m1)
                addAll(m2)
                addAll(m3)
            }
        }.debug()
    }

    @Suppress("UNCHECKED_CAST")
    private fun requestSemesterPlan(): Observable<Managements> {
        return RestApi.docsEndpoint.semesterPlan(Locale.getDefault().language)
            .runInThread(Schedulers.io())
            .map { jSemesterPlans -> jSemesterPlans.map { jSemesterPlan -> SemesterPlan.from(jSemesterPlan) } }
            .map { semesterPlans -> semesterPlans.filter { Date() in it.period.beginDay..it.period.endDay } }
            .map { semesterPlans -> semesterPlans.map { SemesterPlanItem(it) }.toCollection(ArrayList()) as Managements }
            .onErrorReturn { Managements() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadPrinicipalOffice(): Observable<Managements> {
        return RestApi.docsEndpoint.principalExamOffice(Locale.getDefault().language)
        .runInThread()
        .map { jManagement -> Management.from(jManagement) }
        .map { managements -> arrayListOf(ManagementItem(managements,2)) as Managements }
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadStura(): Observable<Managements> {
        return RestApi.docsEndpoint.sturaHTW(Locale.getDefault().language)
            .runInThread()
            .map { jManagement -> Management.from(jManagement) }
            .map { managements -> arrayListOf(ManagementItem(managements,3)) as Managements }
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadStudentAdministration(): Observable<Managements> {
        return RestApi.docsEndpoint.administration(Locale.getDefault().language)
            .runInThread()
            .map { jManagement -> Management.from(jManagement) }
            .map { managements -> arrayListOf(ManagementItem(managements,1)) as Managements }
    }
}