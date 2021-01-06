package de.htwdd.htwdresden.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

open class SectionsPagerAdapter(private val context: Context, fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentListWithTitleId = ArrayList<PagerAdapterItem>()

    override fun getItem(position: Int) = fragmentListWithTitleId[position].fragment

    override fun getPageTitle(position: Int) = context.resources.getString(fragmentListWithTitleId[position].tabTitleResourceId)

    override fun getCount(): Int = fragmentListWithTitleId.size

    fun addFragment(fragment: Fragment, tabTitleResourceId: Int) {
        fragmentListWithTitleId.add(PagerAdapterItem(fragment, tabTitleResourceId))
        notifyDataSetChanged()
    }

    fun clear() {
        fragmentListWithTitleId.clear()
        notifyDataSetChanged()
    }

    private class PagerAdapterItem(val fragment: Fragment, val tabTitleResourceId: Int)
}