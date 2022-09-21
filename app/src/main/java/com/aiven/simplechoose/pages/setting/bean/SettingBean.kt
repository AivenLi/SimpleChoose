package com.aiven.simplechoose.pages.setting.bean

data class SettingBean(
    val title  : String,
    val type   : SettingType,
    val desc   : String? = null
)
