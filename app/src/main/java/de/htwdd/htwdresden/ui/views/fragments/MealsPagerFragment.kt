package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import de.htwdd.htwdresden.R
import kotlinx.android.synthetic.main.fragment_meals_pager.*

class MealsPagerFragment: Fragment(R.layout.fragment_meals_pager) {

    companion object {
        const val ARG_TITLE = "title"
        const val ARG_ID    = "id"
    }

    private val pagerAdapter by lazy { PagerAdapter(childFragmentManager) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        val id = arguments?.getInt(ARG_ID) ?: 80
        viewPager.adapter = pagerAdapter.apply {
            addFragment(MealsFragment.newInstance(MealsFragment.Companion.Type.Today, id))
            addFragment(MealsFragment.newInstance(MealsFragment.Companion.Type.Week, id))
            addFragment(MealsFragment.newInstance(MealsFragment.Companion.Type.NextWeek, id))
        }
    }

    private inner class PagerAdapter(fm: FragmentManager):
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val items = ArrayList<Fragment>()

        override fun getItem(position: Int) = items[position]

        override fun getCount() = items.size

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> getString(R.string.mensa_tab_today)
                1 -> getString(R.string.mensa_tab_this_week)
                else -> getString(R.string.mensa_tab_next_week)
            }
        }

        fun addFragment(fragment: Fragment) {
            items.add(fragment)
            notifyDataSetChanged()
        }
    }
}