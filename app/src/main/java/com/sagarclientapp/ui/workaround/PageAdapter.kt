package com.sagarclientapp.ui.workaround

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return WorkAroundOneFragment()
            }
            1 -> {
                return WorkAroundtwoFragment()
            }
            2 -> {
                return WorkAroundThreeFragment()
            }
            else -> {
                return WorkAroundOneFragment()
            }
        }
    }



}