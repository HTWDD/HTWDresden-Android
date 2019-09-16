package de.htwdd.htwdresden.ui.models

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import de.htwdd.htwdresden.BR
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.CampusPlanBindables
import de.htwdd.htwdresden.databinding.TemplateBuildingsBindableBinding
import de.htwdd.htwdresden.interfaces.Identifiable

interface CampusPlanable: Identifiable<CampusPlanBindables>
interface CampusPlanModels

data class JCampusPlan(
    val building: String,
    val image: Long,
    val buildings: List<String>
)

class CampusPlan(
    val building: String,
    val image: Long,
    val buildings: List<String>
) {
    companion object {
        fun from(json: JCampusPlan): CampusPlan {
            return CampusPlan(
                json.building,
                json.image,
                json.buildings
            )
        }
    }

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = building.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + buildings.hashCode()
        return result
    }
}

class CampusPlanItem(private val item: CampusPlan): CampusPlanable, Comparable<CampusPlanItem> {

    private val bindingTypes: CampusPlanBindables by lazy {
        CampusPlanBindables().apply {
            add(Pair(BR.campusPlanItem, model))
        }
    }
    private val model = CampusPlanModel()

    init {
        model.apply {
            title.set(item.building)
            image.set(when (item.image) {
                1L -> R.drawable.campusplan_dresden
                else -> R.drawable.campusplan_pillnitz
            })
        }
    }

    fun addBuildings(layout: LinearLayout) {
        layout.removeAllViews()
        item.buildings.forEach { building ->
            val split = building.split(":")

            val buildingView = LayoutInflater.from(layout.context).inflate(R.layout.template_buildings_bindable, null, false)
            val binding = DataBindingUtil.bind<TemplateBuildingsBindableBinding>(buildingView)?.apply {
                campusPlanBuildingModel = CampusPlanBuildingModel().apply {
                    legend.set(split.first())
                    description.set(split.last())
                }
            }
            layout.addView(binding?.root)
        }
    }

    override fun itemViewType() = R.layout.list_item_campus_plan_bindable

    override fun bindingTypes() = bindingTypes

    override fun compareTo(other: CampusPlanItem) = item.building.compareTo(other.item.building)

    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = item.hashCode()
}


class CampusPlanModel: CampusPlanModels {
    val title = ObservableField<String>()
    val image = ObservableField<Int>()
}

class CampusPlanBuildingModel: CampusPlanModels {
    val legend      = ObservableField<String>()
    val description = ObservableField<String>()
}