package de.htwdd.htwdresden.utils.extensions

infix fun <T> Collection<T>.contentDeepEquals(collection: Collection<T>?) = collection?.let { other ->
    size == other.size && toCollection(ArrayList()).toArray() contentDeepEquals other.toCollection(ArrayList()).toArray()
} ?: false