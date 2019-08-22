package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import de.htwdd.htwdresden.adapter.Managements
import de.htwdd.htwdresden.classes.API.ManagementService
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.utils.extensions.debug
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.verbose
import de.htwdd.htwdresden.utils.holders.AssetHolder
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class ManagementViewModel: ViewModel() {

    private val ah: AssetHolder by lazy { AssetHolder.instance }

    fun request(): Observable<Managements> {
        return Observables.combineLatest(
            requestSemesterPlan().runInThread(),
            loadManagement().runInThread()) { s, m ->
            Managements().apply {
                addAll(s)
                addAll(m)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun requestSemesterPlan(): Observable<Managements> {
        verbose("request()")
        return ManagementService.instance.semesterPlan()
            .runInThread(Schedulers.io())
            .map { jSemesterPlans -> jSemesterPlans.map { jSemesterPlan -> SemesterPlan.from(jSemesterPlan) } }
            .map { semesterPlans -> semesterPlans.filter { Date() in it.period.beginDay..it.period.endDay } }
            .map { semesterPlans -> semesterPlans.map { SemesterPlanItem(it) }.toCollection(ArrayList()) as Managements }
            .onErrorReturn { Managements() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadManagement(): Observable<Managements> {
        return Observable.defer {
            val result = Gson().fromJson(ah.readJsonData("Management.json"), Array<JManagement>::class.java)
            Observable.just(result)
        }
        .runInThread()
        .map { jManagements -> jManagements.map { jManagement -> Management.from(jManagement) } }
        .map { managements -> managements.map { ManagementItem(it) }.toCollection(ArrayList()) as Managements }
    }
}