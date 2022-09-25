package com.aiven.simplechoose.bean.dto

data class SettingBean(
    val title  : String,
    val type   : SettingType,
    var desc   : String? = null,
    var switch : Boolean = false
)
