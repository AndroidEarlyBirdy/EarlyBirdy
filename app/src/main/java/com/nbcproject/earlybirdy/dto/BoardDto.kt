package com.nbcproject.earlybirdy.dto

import android.net.Uri
import com.google.firebase.Timestamp

data class BoardDto(
    val bid: String = "",
    val uid: String = "",
    val writer: String= "",
    val profile: Int? = 0,
    val createdTime: Timestamp? = null,
    val contentsTitle: String= "",
    val contents: String= "",
    val contentsPhoto: String? = null
)
