package com.example.earlybirdy.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class NetworkConnectionCheck(private val context: Context) : ConnectivityManager.NetworkCallback() {

    private val networkRequest: NetworkRequest
    private val connectivityManager: ConnectivityManager

    init {
        networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun register() {
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        // 네트워크가 연결되었을 때 할 동작
        Toast.makeText(context, "네트워크 연결됨", Toast.LENGTH_SHORT).show()
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        // 네트워크 연결이 끊겼을 때 할 동작
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
        Toast.makeText(context, "네트워크 연결 끊김", Toast.LENGTH_SHORT).show()
    }
}
