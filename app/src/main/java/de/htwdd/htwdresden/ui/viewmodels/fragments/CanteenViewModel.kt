package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Canteens
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.Canteen
import de.htwdd.htwdresden.ui.models.CanteenItem
import de.htwdd.htwdresden.ui.models.Meal
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.runInThread
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class CanteenViewModel: ViewModel() {

    @Suppress("UNCHECKED_CAST")
    fun request(): Observable<Canteens> {
        return requestCanteens()
            .runInThread()
            .flatMap { canteens ->
                val requests = canteens.map { canteen ->
                    requestMeals(canteen.id.toString(), Date().format("yyyy-MM-dd"))
                        .onErrorReturn { ArrayList() }
                        .map {
                            canteen.apply {
                                meals.apply {
                                    clear()
                                    addAll(it)
                                }
                            }
                        }
                }
                Observable.combineLatest(requests) {
                    it.toCollection(ArrayList()) as ArrayList<Canteen>
                }
            }
            .map { it.map { canteen -> CanteenItem(canteen) }.toCollection(ArrayList()) as Canteens }
    }

    private fun requestCanteens(): Observable<List<Canteen>> {
        return RestApi
            .canteenEndpoint
            .getCanteens()
            .runInThread(Schedulers.io())
            .map { it.map { jCanteen -> Canteen.from(jCanteen) }.sortedWith(compareBy { c -> c }) }
            .map { it.sortedBy { canteen -> !canteen.name.contains("reichenbach", ignoreCase = true) } }
            .map { p -> p.filterNot { it.name.contains( "Kreuzgymnasium", ignoreCase = true) || it.name.contains("Palucca Schule", ignoreCase = true)}}
    }

    private fun requestMeals(id: String, date: String): Observable<List<Meal>> {
        return RestApi
            .canteenEndpoint
            .getMeals(id, date)
            .runInThread(Schedulers.io())
            .map { it.map { jMeal -> Meal.from(jMeal) } }
    }
}