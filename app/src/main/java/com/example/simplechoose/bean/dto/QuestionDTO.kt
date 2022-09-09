package com.example.simplechoose.bean.dto

data class QuestionDTO(
    // 题目
    val title: String,
    // 选项列表
    val chooseList: ArrayList<AnswerDTO>,
    // 选择模式，0：单选，1：多选
    val mode: Int = 0,
    // 答案，如果mode=0则该字段有效，否则无效
    val answer: Int = 0,
    // 答案列表，mode为1时有效
    val answerList: ArrayList<Int>? = null,
    // 图片链接，可以为null。
    val imageUrl: String? = null
)
