package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.DetailOccupancies
import de.htwdd.htwdresden.db.RoomRealm
import de.htwdd.htwdresden.ui.models.DetailRoomOccupancyHeaderItem
import de.htwdd.htwdresden.ui.models.DetailRoomOccupancyItem
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.guard
import de.htwdd.htwdresden.utils.holders.StringHolder
import io.reactivex.Flowable
import io.realm.Realm
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import kotlin.collections.ArrayList

class RoomOccupancyDetailViewModel: ViewModel() {

    private val realm: Realm by lazy { Realm.getDefaultInstance() }
    private val sh: StringHolder by lazy { StringHolder.instance }

    @Suppress("UNCHECKED_CAST")
    fun query(id: String?): Flowable<DetailOccupancies> {
        id.guard { return Flowable.empty() }
        return realm.where(RoomRealm::class.java).equalTo("id", id)
            .findFirstAsync()
            .asFlowable<RoomRealm>()
            .filter { it.isLoaded }
            .map { it.occupancies.map { it }.toCollection(ArrayList()) }
            .map { it.groupBy { g -> g.day } }
            .map { map ->
                val calendar = GregorianCalendar.getInstance()
                val result = DetailOccupancies()
                map.keys.sorted().forEach { key ->
                    calendar.set(DAY_OF_WEEK, (key % 7) + 1)
                    val occupancies = "${map[key]?.size} ${sh.getString(R.string.occupancies)}"
                    result.add(DetailRoomOccupancyHeaderItem(calendar.time.format("EEEE"), occupancies))
                    map[key]?.mapNotNull { DetailRoomOccupancyItem(it) }?.let {
                        result.addAll(it.sortedWith(compareBy { c -> c }).toCollection(ArrayList()))
                    }
                }
                result
            }
    }

}