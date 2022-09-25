package com.aiven.simplechoose.bean.dto

data class UpdateAppDTO(
    val versionName: String,
    val minVersion: String,
    val versionCode: Int,
    val desc: String? = null,
    val url: String? = null,
    val md5: String? = null,
    val apkSize: Long
)
