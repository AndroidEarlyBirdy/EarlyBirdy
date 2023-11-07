package com.nbcproject.earlybirdy.dto

import com.google.firebase.Timestamp

data class BoardDto(
    val bid: String = "",
    val uid: String = "",
    val writer: String= "",
    val createdTime: Timestamp? = null,
    val contentsTitle: String= "",
    val contents: String= "",
)
