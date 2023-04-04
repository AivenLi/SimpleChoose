package com.aiven.simplechoose.pages.listtest

data class MultiBean(
    val isFirst: Boolean,
    val isSecond: Boolean,
    var isOpen: Boolean,
    val title: String,
    var childList: ArrayList<MultiBean>? = null
)
