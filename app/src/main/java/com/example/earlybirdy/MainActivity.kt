package com.example.earlybirdy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.databinding.ActivityMainBinding
import com.example.earlybirdy.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewPagerAdapter: MainViewPagerAdapter by lazy {
        MainViewPagerAdapter(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        val tabLayout = binding.tlMain

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(tabLayout, binding.vpMain) { tab, position ->
            tab.setText(viewPagerAdapter.getTitle(position))


            when (position) {
                0 -> tab.setIcon(R.drawable.ic_board)
                1 -> tab.setIcon(R.drawable.ic_home)
                2 -> tab.setIcon(R.drawable.ic_mypage)

            }
        }.attach()
    }

    private fun initView() = with(binding) {
        vpMain.adapter = viewPagerAdapter
    }
}
