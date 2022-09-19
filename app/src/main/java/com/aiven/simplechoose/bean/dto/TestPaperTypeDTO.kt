package com.aiven.simplechoose.bean.dto

/**
 * 试卷种类
 * */
data class TestPaperTypeDTO(
    val title   : String,
    val status  : Int,    // 0开放，其他不开放
    val url     : String,
    val iconUrl : String
)