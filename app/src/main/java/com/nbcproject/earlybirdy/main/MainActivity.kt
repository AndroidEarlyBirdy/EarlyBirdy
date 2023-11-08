package com.nbcproject.earlybirdy.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.databinding.ActivityMainBinding


open class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private var alertDialog: AlertDialog? = null
    private val viewPagerAdapter: MainViewPagerAdapter by lazy {
        MainViewPagerAdapter(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 인터넷에 연결되어 있지 않다면, 앱의 모든 액티비티 종료 후 프로세스도 강제종료.
        if (!isNetworkConnected(this)) {
            AlertDialog.Builder(this)
                .setTitle("경고")
                .setMessage("인터넷에 연결되어 있지 않습니다. Wi-Fi 설정을 확인해주세요.")
                .setPositiveButton("Wi-Fi 설정") { _, _ ->
                    // 사용자가 "Wi-Fi 설정" 버튼을 누르면, Wi-Fi 설정 화면으로 이동합니다.
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                .setNegativeButton("취소") { _, _ ->
                    finishAffinity()  // 앱의 모든 액티비티 종료 후 프로세스도 강제종료.
                    System.exit(0)
                }
                .setCancelable(false)  // 뒤로가기 버튼 등으로 다이얼로그를 취소할 수 없게 함
                .show()
        } else {
            initView()
            setTabLayout()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        // 인터넷에 연결되어 있지 않다면, 앱의 모든 액티비티 종료 후 프로세스도 강제종료.
        if (!isNetworkConnected(this)) {
            AlertDialog.Builder(this)
                .setTitle("경고")
                .setMessage("인터넷에 연결되어 있지 않습니다. Wi-Fi 설정을 확인해주세요.")
                .setPositiveButton("Wi-Fi 설정") { _, _ ->
                    // 사용자가 "Wi-Fi 설정" 버튼을 누르면, Wi-Fi 설정 화면으로 이동합니다.
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                .setNegativeButton("취소") { _, _ ->
                    finishAffinity()  // 앱의 모든 액티비티 종료 후 프로세스도 강제종료.
                    System.exit(0)
                }
                .setCancelable(false)  // 뒤로가기 버튼 등으로 다이얼로그를 취소할 수 없게 함
                .show()


        }
    }


    // Activity가 Pause 상태일 때 (다른 Activity로 이동했을 때), 다이얼로그를 닫습니다.
    override fun onPause() {
        super.onPause()
        alertDialog?.dismiss()
    }

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false

            @Suppress("DEPRECATION")
            return networkInfo.isConnectedOrConnecting
        }
    }

    private fun initView() = with(binding) {
        vpMain.adapter = viewPagerAdapter
        vpMain.isUserInputEnabled = false

        // 기본 프래그먼트로 HomeFragment 설정
        binding.vpMain.setCurrentItem(0, false)
    }

    private fun setTabLayout() = with(binding) {
        val tabLayout = tlMain

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(tabLayout, vpMain) { tab, position ->
            tab.setText(viewPagerAdapter.getTitle(position))

            // 아이콘 변경
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_home)
                1 -> tab.setIcon(R.drawable.ic_board)
                2 -> tab.setIcon(R.drawable.ic_board)
                3 -> tab.setIcon(R.drawable.ic_mypage)
            }
        }.attach()
    }
}