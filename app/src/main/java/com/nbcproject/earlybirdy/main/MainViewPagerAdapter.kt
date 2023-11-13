package com.nbcproject.earlybirdy.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.ranking.RankingFragment
import com.nbcproject.earlybirdy.board.board_main.BoardFragment
import com.nbcproject.earlybirdy.home.HomeFragment
import com.nbcproject.earlybirdy.my_page.MyPageFragment



class MainViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = ArrayList<MainTabs>()

    init {
        fragmentList.add(
            MainTabs(HomeFragment.newInstance(), R.string.tab_home),
        )
        fragmentList.add(
            MainTabs(RankingFragment.newInstance(), R.string.tab_rank)
        )
        fragmentList.add(
            MainTabs(BoardFragment.newInstance(), R.string.tab_board),
        )
        fragmentList.add(
            MainTabs(MyPageFragment.newInstance(), R.string.tab_mypage),
        )
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    fun getTitle(position: Int): Int {
        return fragmentList[position].titleRes
    }

    override fun createFragment(position: Int): Fragment {
        //기본 프래그먼트를 설정
        return fragmentList[position].fragment
    }
}