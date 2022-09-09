package com.example.im.controller.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, fragments: List<Fragment>): FragmentStateAdapter(fragmentManager, lifecycle) {
    private var fragmentList:List<Fragment>

    init {
        fragmentList = fragments
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}