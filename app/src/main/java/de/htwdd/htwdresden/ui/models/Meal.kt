package de.htwdd.htwdresden.ui.models

import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Identifiable
import de.htwdd.htwdresden.interfaces.Modelable
import de.htwdd.htwdresden.utils.holders.StringHolder

//-------------------------------------------------------------------------------------------------- Protocols
interface Mealable: Identifiable<MealableModel>
interface MealableModel: Modelable

//-------------------------------------------------------------------------------------------------- JSON
data class JMeal (
    val id: Long,
    val name: String,
    val category: String,
    val prices: JPrices,
    val notes: List<String>
)

data class JPrices (
    val students: Double?   = null,
    val employees: Double?  = null,
    val pupils: Double?     = null,
    val others: Double?     = null
)

//-------------------------------------------------------------------------------------------------- Concrete Model
class Meal(
    val id: Long,
    val name: String,
    val category: String,
    val prices: Prices,
    val notes: List<String>
) {

    companion object {
        fun from(json: JMeal): Meal {
            return Meal(
                json.id,
                json.name,
                json.category,
                Prices.from(json.prices),
                json.notes
            )
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + prices.hashCode()
        result = 31 * result + notes.hashCode()
        return result
    }
}

class Prices(
    val students: Double?   = null,
    val employees: Double?  = null,
    val pupils: Double?     = null,
    val others: Double?     = null
) {

    companion object {
        fun from(json: JPrices): Prices {
            return Prices(
                json.students,
                json.employees,
                json.pupils,
                json.others
            )
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = students?.hashCode() ?: 0
        result = 31 * result + (employees?.hashCode() ?: 0)
        result = 31 * result + (pupils?.hashCode() ?: 0)
        result = 31 * result + (others?.hashCode() ?: 0)
        return result
    }
}

//-------------------------------------------------------------------------------------------------- Item
class MealItem(private val item: Meal): Mealable {

    override val viewType: Int
        get() = R.layout.list_item_meal_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, MealableModel>>().apply {
            add(BR.mealModel to model)
        }
    }

    private val model = MealModel()

    private val sh by lazy { StringHolder.instance }

    init {
        model.apply {
            name.set(item.name)
            hasPork.set(item.notes.any { it.contains("schwein", true) })
            hasBeef.set(item.notes.any { it.contains("rind", true) })
            isVeggie.set(item.notes.any { it.contains("vegetarisch", true) })
            isVegan.set(item.notes.any { it.contains("vegan", true) })
            hasGarlic.set(item.notes.any { it.contains("knoblauch", true) })
            hasAlcohol.set(item.notes.any { it.contains("alkohol", true) })
            priceStudent.set(sh.getString(R.string.meal_price_student, item.prices.students ?: 0f ))
            priceEmployees.set(sh.getString(R.string.meal_price_employee, item.prices.employees ?: 0f ))
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.hashCode()
}

//-------------------------------------------------------------------------------------------------- Header Item
class MealHeaderItem(private val header: String, private val subheader: String): Mealable {

    override val viewType: Int
        get() = R.layout.list_item_meal_header_bindable

    override val bindings by lazy {
        ArrayList<Pair<Int, MealableModel>>().apply {
            add(BR.mealHeaderModel to model)
        }
    }

    private val model = MealHeaderModel()

    init {
        model.apply {
            header.set(this@MealHeaderItem.header)
            subheader.set(this@MealHeaderItem.subheader)
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + subheader.hashCode()
        return result
    }
}

//-------------------------------------------------------------------------------------------------- Model
class MealModel: MealableModel {
    val name            = ObservableField<String>()
    val hasPork         = ObservableField<Boolean>()
    val hasBeef         = ObservableField<Boolean>()
    val isVeggie        = ObservableField<Boolean>()
    val isVegan         = ObservableField<Boolean>()
    val hasGarlic       = ObservableField<Boolean>()
    val hasAlcohol      = ObservableField<Boolean>()
    val priceStudent    = ObservableField<String>()
    val priceEmployees  = ObservableField<String>()
}

//-------------------------------------------------------------------------------------------------- Header Model
class MealHeaderModel: MealableModel {
    val header = ObservableField<String>()
    val subheader = ObservableField<String>()
}