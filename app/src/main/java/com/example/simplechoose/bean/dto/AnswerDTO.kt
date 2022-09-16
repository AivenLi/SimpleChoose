package com.example.simplechoose.bean.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AnswerDTO(
    val title: String,
    val index: Int,
    var selected: Boolean = false
) : Parcelable
