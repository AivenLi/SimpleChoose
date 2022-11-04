package com.aiven.simplechoose.bean.dto

import androidx.annotation.DrawableRes
import com.aiven.simplechoose.bean.enums.MineAction

data class SettingBean(
    val title  : String,
    val type   : SettingType,
    var desc   : String? = null,
    var switch : Boolean = false,
    @DrawableRes
    val icon   : Int     = 0,
    val action : MineAction
)
