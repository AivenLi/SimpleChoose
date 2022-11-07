package com.aiven.simplechoose.bean.enums

enum class MineAction {

    TEST_RECORD(0),
    SETTING(1),
    QR_CREATE(2),
    SCAN_CODE(3);

    val action: Int
    constructor(action: Int) {
        this.action = action
    }
}