package com.aiven.simplechoose.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.MediaStore
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

/**
 * 该方法不执行失败不回调onFinish
 * */
fun Completable.doSql(dbCallback: DBCallback<Unit>?) {
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                dbCallback?.onDBStart(d)
            }

            override fun onComplete() {
                dbCallback?.onDBFinish()
            }

            override fun onError(e: Throwable) {
                dbCallback?.onDBError(e.toString())
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

fun grantedPermission(grants: IntArray): Boolean {
    var granted = true
    for (g in grants) {
        if (g != PackageManager.PERMISSION_GRANTED) {
            granted = false
            break
        }
    }
    return granted
}

fun grantedPermission(map: Map<String, Boolean>): Boolean {
    var granted = true
    val iterator = map.entries.iterator()
    for ((_, value) in map) {
        if (!value) {
            granted = false
            break
        }
    }
    return granted
}

inline fun Intent.getImageFromPhoto(activity: Activity): String? {
    val uri = data
    uri?.let { u ->
        val cursor: Cursor? = activity.contentResolver
            .query(u, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        cursor?.let { c ->
            if (c.moveToFirst()) {
                val value = c.getColumnIndex(MediaStore.Images.Media.DATA)
                if (value >= 0) {
                    return c.getString(value)
                }
            }
        }
        cursor?.close()
    }
    return null
}