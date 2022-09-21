package com.aiven.simplechoose.pages.result.bean

import android.os.Parcel
import android.os.Parcelable
import com.aiven.simplechoose.pages.result.bean.enums.AnswerResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultBean(
    val title: String,
    val score: Float,
    val rightNum: Int,
    val leftNum: Int,
    val unCheckNum: Int,
    val useTime: Long,
    val answerList: ArrayList<AnswerResult>
) : Parcelable
