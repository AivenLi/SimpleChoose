package com.aiven.simplechoose.bean.dto

enum class SettingType {
    SWITCH(0),
    CLICK(1);

    val value: Int
    constructor(value: Int) {
        this.value = value
    }
}