package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Meals
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.Meal
import de.htwdd.htwdresden.ui.models.MealHeaderItem
import de.htwdd.htwdresden.ui.models.MealItem
import de.htwdd.htwdresden.utils.extensions.datesOfCurrentWeek
import de.htwdd.htwdresden.utils.extensions.datesOfNextWeek
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.runInThread
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MealsViewModel: ViewModel() {

    var type: String = "today"

    @Suppress("UNCHECKED_CAST")
    fun request(id: Int): Observable<Meals> {                                                       // different reuqest for week type
        return when (type) {
            "week" -> requestForWeek("$id").onErrorReturn { Meals() }                           // request meals for current week
            "nextWeek" -> requestForWeek("$id", false).onErrorReturn { Meals() }   // request meals for next week
            else -> requestForDay("$id").onErrorReturn { Meals() }                              // request meals for today
        }
    }

    private fun requestForDay(id: String): Observable<Meals> {
        return RestApi
            .canteenService
            .getMeals(id, Date().format("yyyy-MM-dd"))                                      // api call
            .runInThread(Schedulers.io())
            .map { it.map { jMeal -> Meal.from(jMeal) } }                                           // json to model
            .map { meals ->
                val sortedKeys      = mutableSetOf<String>()
                val sortedValues    = mutableSetOf<Meal>()
                meals.groupBy { it.category }.apply {
                    keys.sorted().forEach { sortedKeys.add(it) }
                    values.forEach { v -> v.forEach { sortedValues.add(it) } }
                }
                sortedKeys to sortedValues                                                          // grouped elements
            }
            .map { pair ->
                val result = Meals()
                pair.first.forEach { key ->
                    result.add(MealHeaderItem(key, Date().format("dd. MMMM")))
                    result.addAll(pair.second.filter { it.category.contains(key) }.map { MealItem(it) })
                }
                result
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun requestForWeek(id: String, isCurrentWeek: Boolean = true): Observable<Meals> {
        val weeks = if (isCurrentWeek) {
            GregorianCalendar.getInstance(Locale.GERMAN).datesOfCurrentWeek                         // all dates for current week
        } else {
            GregorianCalendar.getInstance(Locale.GERMAN).datesOfNextWeek                            // all dates for next week
        }

        return Observable.combineLatest(
            weeks                                                                                   // combine all requested dates
            .map { it.format("yyyy-MM-dd") }
            .map { RestApi.canteenService.getMeals(id, it).runInThread(Schedulers.io()) }
            .map { it.map {  jMeals -> jMeals.map { jMeal -> Meal.from(jMeal) } } }
        ) { it.toCollection(ArrayList()) as ArrayList<List<Meal>> }
            .runInThread()
            .map { meals ->
                val hMap = HashMap<Date, List<Meal>>()                                              // map into hasMap with k = date and v = list of meals
                for (i in 0 until weeks.size) {
                    hMap[weeks[i]] = meals[i]
                }
                hMap.toSortedMap()
            }
            .map { hMap ->
                val result = Meals()
                for ((k, v) in hMap) {
                    result.add(MealHeaderItem(k.format("EEEE"), k.format("dd. MMMM")))
                    result.addAll(v.map { MealItem(it) })
                }
                result
            }
    }
}