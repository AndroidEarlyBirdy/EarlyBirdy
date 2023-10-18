package com.example.earlybirdy.data

import com.google.firebase.Timestamp

data class MyGoal(
    val goalId: String? = null,
    val title: String = "",
    val check: Boolean = false,
    val date: Timestamp? = null
)
