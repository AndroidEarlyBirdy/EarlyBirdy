package com.example.earlybirdy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.databinding.ActivityMainBinding
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

//    private fun login() {
//        val tvTest = findViewById<TextView>(R.id.tv_test)
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            showToast(this, "로그인 중 = ${currentUser.email}")
//            tvTest.setText("${currentUser.uid}")
//        } else {
//            navigateToSigninActivity(this)
//        }
//    }
//
//    private fun logOut() {
//        val tvTest = findViewById<TextView>(R.id.tv_test)
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            auth.signOut()
//            showToast(this, "로그아웃 성공!")
//            navigateToSigninActivity(this)
//        } else {
//            showToast(this, "로그인을 먼저 해주세요")
//            tvTest.setText("${currentUser?.uid}")
//            navigateToSigninActivity(this)
//        }
//    }
}