package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.utils.holders.ColorHolder

//-------------------------------------------------------------------------------------------------- Protocols
interface Canteenable: Identifiable<CanteenableModels> {
    val name: String
    val id: Int
}
interface CanteenableModels: Modelable

//-------------------------------------------------------------------------------------------------- JSON
data class JCanteen (
    val id: Long,
    val name: String,
    val city: String,
    val address: String,
    val coordinates: List<Double>
)

//-------------------------------------------------------------------------------------------------- Concrete Model
class Canteen(
    val id: Long,
    val name: String,
    val city: String,
    val address: String,
    val location: Pair<Double, Double>
): Comparable<Canteen> {

    val meals: ArrayList<Meal> = ArrayList()

    companion object {
        fun from(json: JCanteen): Canteen {
            return Canteen(
                json.id,
                splitByCommaAndTake(json.name, first = false).trim().replace("Johannesstadt", "Johannstadt"),
                json.city,
                splitByCommaAndTake(json.address).trim(),
                json.coordinates.first() to json.coordinates.last()
            )
        }

        private fun splitByCommaAndTake(jProperty: String, first: Boolean = true): String {
            return if (jProperty.contains(",")) {
                if (first) {
                    jProperty.split(",").firstOrNull() ?: jProperty
                } else {
                    jProperty.split(",").lastOrNull() ?: jProperty
                }
            } else {
                jProperty
            }
        }
    }

    override fun compareTo(other: Canteen) = name.compareTo(other.name)

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + location.hashCode()
        return result
    }
}

//-------------------------------------------------------------------------------------------------- Item
class CanteenItem(private val item: Canteen): Canteenable {

    override val viewType: Int
        get() = R.layout.list_item_canteen_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, CanteenableModels>>().apply {
            add(BR.canteenModel to model)
        }
    }
    private val model = CanteenModel()

    private val ch by lazy { ColorHolder.instance }

    override val name: String
        get() = item.name

    override val id: Int
        get() = item.id.toInt()


    init {
        model.apply {
            name.set(item.name)
            address.set(item.address)
            city.set(item.city)
            meals.set("${item.meals.size}")
            mealsColor.set(ch.getColor(if (item.meals.isEmpty()) R.color.red_500 else R.color.grey_600))
            textColor.set(ch.getColor(if(item.meals.isEmpty()) R.color.grey_600 else R.color.dark_gray ))
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.hashCode()
}

//-------------------------------------------------------------------------------------------------- Model
class CanteenModel: CanteenableModels {
    val name        = ObservableField<String>()
    val address     = ObservableField<String>()
    val city        = ObservableField<String>()
    val meals       = ObservableField<String>()
    val mealsColor  = ObservableField<Int>()
    val textColor   = ObservableField<Int>()
}