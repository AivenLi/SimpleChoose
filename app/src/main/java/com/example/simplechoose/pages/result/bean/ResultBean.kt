package com.example.simplechoose.pages.result.bean

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultBean(
    val title: String,
    val score: Float,
    val rightNum: Int,
    val leftNum: Int,
    val unCheckNum: Int,
    val useTime: Long
) : Parcelable
