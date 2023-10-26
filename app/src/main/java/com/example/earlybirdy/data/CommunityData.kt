package com.example.earlybirdy.data

import java.sql.Timestamp

data class CommunityData(
    val uid: String,
    val writer: String,
//    val createdTime: Timestamp,
    val contentsTitle: String,
    val contents: String,
//    val contentsPoto: String
)
