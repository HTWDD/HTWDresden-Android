package de.htwdd.htwdresden.utils.extensions

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmQuery

typealias Query<T> = RealmQuery<T>.() -> Unit

fun <T: RealmModel> T.queryFirst(query: Query<T>): T? {
    Realm.getDefaultInstance().use { realm ->
        val item: T? = realm.where(this.javaClass).withQuery(query).findFirst()
        return if(item != null && RealmObject.isValid(item)) realm.copyFromRealm(item) else null
    }
}

private inline fun <T> T.withQuery(block: (T) -> Unit): T {
    block(this)
    return this
}