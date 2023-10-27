package com.example.earlybirdy.community

data class LocalCommunityData( // 파이어베이스에 안씀
    val cid: String, // document.id
    val uid: String, // communityData.uid
    val writer: String,
//    val createdTime: Timestamp,
    val contentsTitle: String,
    val contents: String,
//    val contentsPoto: String
)
