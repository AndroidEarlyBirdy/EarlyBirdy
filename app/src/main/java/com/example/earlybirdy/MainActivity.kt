package com.example.earlybirdy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.databinding.ActivityMainBinding
import com.example.earlybirdy.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewPagerAdapter: MainViewPagerAdapter by lazy {
        MainViewPagerAdapter(this@MainActivity)
    }


    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    //val db = Firebase.firestore

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
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        val btnSignout: Button = findViewById<Button>(R.id.btn_signout_test)
        btnSignout.setOnClickListener {
            logOut()
        }
    }

    private fun initView() = with(binding) {
        vpMain.adapter = viewPagerAdapter
        vpMain.isUserInputEnabled=false

        // 기본 프래그먼트로 HomeFragment 설정
        binding.vpMain.setCurrentItem(1, false)
    }

    private fun login() {
        val tvTest = findViewById<TextView>(R.id.tv_test)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            showToast(this, "로그인 중 = ${currentUser.email}")
            tvTest.setText("${currentUser.uid}")
        } else {
            navigateToSigninActivity(this)
        }
    }

    private fun logOut() {
        val tvTest = findViewById<TextView>(R.id.tv_test)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            auth.signOut()
            showToast(this, "로그아웃 성공!")
            navigateToSigninActivity(this)
        } else {
            showToast(this, "로그인을 먼저 해주세요")
            tvTest.setText("${currentUser?.uid}")
            navigateToSigninActivity(this)
        }
    }
}