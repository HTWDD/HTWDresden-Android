package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import de.htwdd.htwdresden.adapter.CampusPlans
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.CampusPlan
import de.htwdd.htwdresden.ui.models.CampusPlanItem
import de.htwdd.htwdresden.ui.models.JCampusPlan
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.holders.ResourceHolder
import io.reactivex.Observable
import java.util.*
import kotlin.collections.ArrayList

class CampusPlanViewModel: ViewModel() {

    private val rh: ResourceHolder by lazy { ResourceHolder.instance }

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<CampusPlans> {

        return RestApi.docsEndpoint.campusPlan("de")
        .runInThread()
        .map { it.map { jCampusPlan ->  CampusPlan.from(jCampusPlan) } }
        .map { it.sortedWith(compareBy { c -> c }) }
        .map { it.map { campusPlan -> CampusPlanItem(campusPlan) }.toCollection(ArrayList()) as CampusPlans }
    }
}