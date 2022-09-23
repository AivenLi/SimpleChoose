package com.aiven.simplechoose.pages.setting.bean

import androidx.annotation.DrawableRes

data class SettingBean(
    val title  : String,
    val type   : SettingType,
    var desc   : String? = null,
    var switch : Boolean = false,
    @DrawableRes
    val icon   : Int     = 0
)
