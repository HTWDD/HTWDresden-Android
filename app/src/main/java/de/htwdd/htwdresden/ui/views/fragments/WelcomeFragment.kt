package de.htwdd.htwdresden.ui.views.fragments

import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Swipeable

class WelcomeFragment: Fragment(R.layout.fragment_welcome), Swipeable {
    companion object {
        private var delegate: SwipeDelegate? = null
        fun newInstance(delegate: SwipeDelegate? = null): WelcomeFragment = WelcomeFragment().apply {
            this@Companion.delegate = delegate
        }
    }
}