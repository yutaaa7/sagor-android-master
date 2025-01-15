package com.sagarclientapp.ui.home


import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sagarclientapp.R

class TripsViewPagerAdapter(
    private val context: Context,
    supportFragmentManager: FragmentManager
):FragmentPagerAdapter(supportFragmentManager) {




    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position === 0) {
            fragment = ScheduledFragment()
        } else if (position === 1) {
            fragment = HistoryFragment()
        }
        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position === 0) {
            title = context.getString(R.string.scheduled)
        } else if (position === 1) {
            title = context.getString(R.string.history)
        }
        return title
    }

}