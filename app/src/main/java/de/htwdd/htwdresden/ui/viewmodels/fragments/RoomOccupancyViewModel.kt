package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.adapter.Occupancies
import de.htwdd.htwdresden.db.RoomRealm
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.RoomOccupancyItem
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.utils.extensions.runInThread
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.OrderedCollectionChangeSet
import io.realm.Realm
import io.realm.RealmResults

typealias OccupancyCallback = (occupancies: Occupancies,
                               deletedChangeSet: Array<out OrderedCollectionChangeSet.Range>,
                               insertedChangeSet: Array<out OrderedCollectionChangeSet.Range>,
                               modifiedChangeSet: Array<out OrderedCollectionChangeSet.Range>) -> Unit

class RoomOccupancyViewModel: ViewModel() {

    private lateinit var roomRealms: RealmResults<RoomRealm>
    private val realm: Realm by lazy { Realm.getDefaultInstance() }
    private var onRealmChangedClosure: OccupancyCallback = { _, _, _, _ -> }

    @Suppress("UNCHECKED_CAST")
    fun query() {
        roomRealms = realm.where(RoomRealm::class.java)
            .sort("name")
            .findAllAsync()
            .apply {
                addChangeListener { t, changeSet ->
                    onRealmChangedClosure(t.map { RoomOccupancyItem(it) }.toCollection(ArrayList()) as Occupancies,
                        changeSet.deletionRanges, changeSet.insertionRanges, changeSet.changeRanges)
                }
            }
    }

    fun onRoomChanged(callback: OccupancyCallback) {
        onRealmChangedClosure = callback
    }

    fun request(room: String): Observable<List<Timetable>> {
        return RestApi
            .timetableEndpoint
            .roomTimetable(room)
            .runInThread(Schedulers.io())
            .map { json -> json.map { Timetable.from(it) } }
    }


    override fun onCleared() {
        super.onCleared()
        realm.apply {
            removeAllChangeListeners()
            close()
        }
    }
}