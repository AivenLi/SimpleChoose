package com.aiven.simplechoose.utils

import android.view.View
import android.widget.Checkable
import com.aiven.simplechoose.R
import com.aiven.simplechoose.db.DBCallback
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.schedulers.Schedulers
import org.reactivestreams.Subscription

var <T: View> T.lastTime: Long
    set(value) = setTag(R.id.single_click, value)
    get() = getTag(R.id.single_click) as? Long ?: 0

inline fun <T: View> T.setSingleClickListener(timeout: Long = 1000, crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > timeout || this is Checkable) {
            lastTime = currentTime
            block(this)
        }
    }
}

fun Completable.doSql(dbCallback: DBCallback<Unit>) {
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                dbCallback.onDBStart(d)
            }

            override fun onComplete() {
                dbCallback.onDBFinish()
            }

            override fun onError(e: Throwable) {
                dbCallback.onDBError(e.toString())
                dbCallback.onDBFinish()
            }
        })
}

/**
 * 该方法不回调onDBStart
 * */
fun <T> Flowable<T>.doSql(dbCallback: DBCallback<T>) {
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            dbCallback.onDBResult(it)
            dbCallback.onDBFinish()
        },{
            dbCallback.onDBError(it.toString())

        })
}

fun <T> Observable<T>.doSql(dbCallback: DBCallback<T>) {
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : Observer<T> {
            override fun onSubscribe(d: Disposable) {
                dbCallback.onDBStart(d)
            }

            override fun onNext(t: T) {
                dbCallback.onDBResult(t)
                dbCallback.onDBFinish()
            }

            override fun onError(e: Throwable) {
                dbCallback.onDBError(e.toString())
                dbCallback.onDBFinish()
            }

            override fun onComplete() {
            }
        })
}

fun <T> Single<T>.doSql(dbCallback: DBCallback<T>) {
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : SingleObserver<T> {
            override fun onSubscribe(d: Disposable) {
                dbCallback.onDBStart(d)
            }

            override fun onSuccess(t: T) {
                dbCallback.onDBResult(t)
                dbCallback.onDBFinish()
            }

            override fun onError(e: Throwable) {
                dbCallback.onDBError(e.toString())
                dbCallback.onDBFinish()
            }
        })
}