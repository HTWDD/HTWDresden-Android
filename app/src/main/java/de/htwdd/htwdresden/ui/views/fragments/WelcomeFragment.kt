package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Swipeable

class WelcomeFragment: Fragment(), Swipeable {
    companion object {
        private var delegate: SwipeDelegate? = null
        fun newInstance(delegate: SwipeDelegate? = null): WelcomeFragment = WelcomeFragment().apply {
            this@Companion.delegate = delegate
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_welcome, container, false)

}