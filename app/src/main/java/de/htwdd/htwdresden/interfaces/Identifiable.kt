package de.htwdd.htwdresden.interfaces

interface Identifiable<T> {
    fun itemViewType(): Int
    fun bindingTypes(): T
}