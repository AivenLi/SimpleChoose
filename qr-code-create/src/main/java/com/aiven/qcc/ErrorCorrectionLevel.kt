package com.aiven.qcc

enum class ErrorCorrectionLevel {

    L("L", "7%"),
    M("M", "15%"),
    Q("Q", "25%"),
    H("H", "35%");

    val value: String
    val desc: String
    constructor(value: String, desc: String) {
        this.value = value
        this.desc = desc
    }
}