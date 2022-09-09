package com.example.simplechoose.bean.dto

data class AnswerDTO(
    val title: String,
    val index: Int,
    var selected: Boolean = false
)
