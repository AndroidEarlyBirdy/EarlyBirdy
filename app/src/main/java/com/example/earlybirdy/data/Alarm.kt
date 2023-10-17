package com.example.earlybirdy.data

import com.google.type.DateTime

data class Alarm(
    val hour: Int,
    val minute: Int,
    val alarm: Boolean,
    val vibe: Boolean
)
