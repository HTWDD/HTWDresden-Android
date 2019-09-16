package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import de.htwdd.htwdresden.adapter.CampusPlans
import de.htwdd.htwdresden.ui.models.CampusPlan
import de.htwdd.htwdresden.ui.models.CampusPlanItem
import de.htwdd.htwdresden.ui.models.JCampusPlan
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.ResourceHolder
import io.reactivex.Observable

class CampusPlanViewModel: ViewModel() {

    private val rh: ResourceHolder by lazy { ResourceHolder.instance }

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<CampusPlans> {

        return Observable.defer {
            val result = Gson().fromJson(rh.readJsonData("CampusPlan.json"), Array<JCampusPlan>::class.java)
            Observable.just(result)
        }
        .runInThread()
        .map { it.map { jCampusPlan ->  CampusPlan.from(jCampusPlan) } }
        .map { it.map { campusPlan -> CampusPlanItem(campusPlan) }.toCollection(ArrayList()) as CampusPlans }
    }
}