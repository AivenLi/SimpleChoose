package com.aiven.simplechoose.pages.setting.bean

data class SettingBean(
    val title  : String,
    val type   : SettingType,
    var desc   : String? = null,
    var switch : Boolean = false
)
