package de.htwdd.htwdresden.ui.views.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.htwdd.htwdresden.R

class CalenderAddEventFragment : Fragment() {

    companion object {
        fun newInstance() = CalenderAddEventFragment()
    }

    private lateinit var viewModel: CalenderAddEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.calender_add_event_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CalenderAddEventViewModel::class.java)
        // TODO: Use the ViewModel
    }

}