package de.htwdd.htwdresden.interfaces

interface Identifiable<T: Modelable> {
    val viewType: Int
    val bindings: ArrayList<Pair<Int, T>>
    val movementFlags: Int
        get() = 0

    val leftAction: () -> Unit
        get() = {}

    val rightAction: () -> Unit
        get() = {}

    fun onLeftSwiped(action: () -> Unit) {}
    fun onRightSwiped(action: () -> Unit) {}

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}

interface Modelable