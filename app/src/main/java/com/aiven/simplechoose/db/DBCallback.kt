package com.aiven.simplechoose.db

import io.reactivex.rxjava3.disposables.Disposable

interface DBCallback<T> {

    fun onDBStart(disposable: Disposable) {}

    fun onDBResult(data: T?)

    fun onDBError(error: String)

    fun onDBFinish() {}
}