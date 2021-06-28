package de.htwdd.htwdresden.ui.views.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.findNavController
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Swipeable
import de.htwdd.htwdresden.ui.viewmodels.fragments.OnboardingViewModel
import de.htwdd.htwdresden.utils.extensions.getViewModel
import kotlinx.android.synthetic.main.fragment_onboarding.*
import android.os.Parcelable




//-------------------------------------------------------------------------------------------------- Protocol
interface SwipeDelegate {
    fun moveNext()
    fun movePrevious()
}

//-------------------------------------------------------------------------------------------------- Fragment
class OnboardingFragment : Fragment(R.layout.fragment_onboarding), SwipeDelegate {
    private val viewModel by lazy { getViewModel<OnboardingViewModel>() }
    private val pagerAdapter by lazy { PagerAdapter(childFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupView()
    }

    private fun setupView(){
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setup(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val parcelable = savedInstanceState.getParcelable("Adapter") as Parcelable?
            pagerAdapter.restoreState(parcelable, ClassLoader.getSystemClassLoader());
        } else {
            pagerAdapter.apply {
                clear()
                addFragment(WelcomeFragment.newInstance(this@OnboardingFragment))
                addFragment(CrashlyticsFragment.newInstance(this@OnboardingFragment))
                addFragment(StudyGroupFragment.newInstance(this@OnboardingFragment))
                addFragment(LoginFragment.newInstance(this@OnboardingFragment))
                addFragment(FinishedFragment.newInstance(this@OnboardingFragment))
            }
        }
    }

    override fun moveNext() {
        viewPager?.run {
            if (currentItem == pagerAdapter.count - 1) {
                findNavController().popBackStack()
            } else {
                currentItem += 1
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val parcelable: Parcelable? = pagerAdapter.saveState()
        outState.putParcelable("Adapter", parcelable)
    }

    override fun movePrevious() {
        viewPager?.run { currentItem -= 1 }
    }

    //---------------------------------------------------------------------------------------------- ViewPager Adapter
    private inner class PagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val items = ArrayList<Swipeable>()

        override fun getItem(position: Int) = items[position] as Fragment

        override fun getCount() = items.size

        fun addFragment(fragment: Swipeable) {
            items.add(fragment)
            notifyDataSetChanged()

        }

        fun clear() {
            items.clear()
            notifyDataSetChanged()
        }
    }
}