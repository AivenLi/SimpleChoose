package com.aiven.simplechoose.pages.setting.bean

enum class SettingType {
    SWITCH(0),
    CLICK(1);

    val value: Int
    constructor(value: Int) {
        this.value = value
    }
}