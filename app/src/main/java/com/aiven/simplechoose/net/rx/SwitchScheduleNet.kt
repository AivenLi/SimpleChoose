package com.aiven.simplechoose.net.rx

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

open class SwitchScheduleNet<T>: RxSchedule<T>(Schedulers.io(), AndroidSchedulers.mainThread())

