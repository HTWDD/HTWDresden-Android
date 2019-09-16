package de.htwdd.htwdresden.db

import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.utils.extensions.*
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.createObject

open class RoomRealm(
    @PrimaryKey var id: String  = "",
    var name: String            = "",
    var occupancies: RealmList<OccupancyRealm> = RealmList()
): RealmObject()

fun RoomRealm.update(id: String, name: String, timetables: List<Timetable>): RoomRealm? {
    verbose("update($id, $name)")
    val realm = Realm.getDefaultInstance()
    var roomRealm: RoomRealm? = null
    realm.use {
        it.executeTransaction { transaction ->
            var result = queryFirst { equalTo("id", id) }
            if (result == null) {
                result = transaction.createObject(id)
            }
            result.name = name

            timetables.forEach {
                OccupancyRealm().update(it)?.let { occupancy ->
                    if (!result.occupancies.contains(occupancy)) {
                        result.occupancies.add(occupancy)
                    }
                }
            }

            transaction.insertOrUpdate(result)
            roomRealm = result
        }
    }
    return roomRealm
}

fun RoomRealm.delete() {
    val realm = Realm.getDefaultInstance()
    realm.use {
        it.executeTransaction {
            this.deleteFromRealm()
        }
    }
}


open class OccupancyRealm(
    @PrimaryKey var id: String  = "",
    var name: String            = "",
    var type: String            = "",
    var day: Int                = 0,
    var beginTime: String       = "",
    var endTime: String         = "",
    var week: Int               = 0,
    var professor: String       = "",
    var weeksOnly: String       = ""
): RealmObject() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as OccupancyRealm
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

fun OccupancyRealm.update(timetable: Timetable): OccupancyRealm? {
    verbose("update($timetable)")
    val realm = Realm.getDefaultInstance()
    var occupancyRealm: OccupancyRealm? = null
    realm.use { r ->
        var result = queryFirst { equalTo("id", timetable.id.uid) }
        if (result == null) {
            result = r.createObject(timetable.id.uid)
        }
        result.apply {
            name = timetable.name
            type = timetable.type
            day = timetable.day.toInt()
            beginTime = timetable.beginTime.format("HH:mm:ss")
            endTime = timetable.endTime.format("HH:mm:ss")
            week = timetable.week.toInt()
            professor = timetable.professor.defaultWhenNull("")
            weeksOnly = timetable.weeksOnly.joinToString(",")
        }
        r.insertOrUpdate(result)
        occupancyRealm = result
    }
    return occupancyRealm
}
