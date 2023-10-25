package com.example.earlybirdy.repository

import com.example.earlybirdy.data.MyPageData

interface UserRepository {
    fun getMyPageUserData(userId : String)
    fun getAttendanceData(userId: String)
}