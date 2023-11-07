package com.nbcproject.earlybirdy.main

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings

class NetworkChangeReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!isNetworkConnected(context!!)) {
            AlertDialog.Builder(context)
                .setTitle("경고")
                .setMessage("인터넷에 연결되어 있지 않습니다. Wi-Fi 설정을 확인해주세요.")
                .setPositiveButton("Wi-Fi 설정") { _, _ ->
                    // 사용자가 "Wi-Fi 설정" 버튼을 누르면, Wi-Fi 설정 화면으로 이동합니다.
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
                .setNegativeButton("취소") { _, _ ->
                    (context as Activity).finishAffinity()
                    System.exit(0)
                }
                .setCancelable(false)
                .show()
        }
    }

    //인터넷 연결 확인 함수
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

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

}