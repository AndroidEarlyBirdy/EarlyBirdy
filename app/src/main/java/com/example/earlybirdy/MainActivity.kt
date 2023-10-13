package com.example.earlybirdy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference


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

            // 아이콘 변경
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_home)
                1 -> tab.setIcon(R.drawable.ic_board)
                2 -> tab.setIcon(R.drawable.ic_mypage)
            }
        }.attach()
    }

    private fun initView() = with(binding) {
        vpMain.adapter = viewPagerAdapter
        vpMain.isUserInputEnabled=false

        // 기본 프래그먼트로 HomeFragment 설정
        binding.vpMain.setCurrentItem(1, false)
    }
}