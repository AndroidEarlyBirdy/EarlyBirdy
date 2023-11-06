package com.example.earlybirdy.dto

import com.google.firebase.Timestamp

data class AttendanceDto(
    val AttendanceId : String = "",
    var date: Timestamp? = null
)
