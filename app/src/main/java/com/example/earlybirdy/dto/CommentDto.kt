package com.example.earlybirdy.dto

import com.google.firebase.Timestamp

data class CommentDto(
    val cid: String = "",
    val uid: String = "",
    val writer: String= "",
    val commentTime: Timestamp? = null,
    val comments: String= ""
)
