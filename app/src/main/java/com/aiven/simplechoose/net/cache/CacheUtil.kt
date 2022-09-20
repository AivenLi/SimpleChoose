package com.aiven.simplechoose.net.cache

import android.content.Context
import android.os.Environment
import com.aiven.simplechoose.app.task.Task
import com.aiven.simplechoose.app.task.TaskApp
import com.aiven.simplechoose.net.rx.SwitchScheduleIO
import com.aiven.simplechoose.utils.Md5Utils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.jakewharton.disklrucache.DiskLruCache
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

object CacheUtil: Task {

    private val gson by lazy { Gson() }
    private var diskLruCache: DiskLruCache? = null

    override fun run(app: TaskApp) {
        initCache(app)
    }

    private fun initCache(context: Context) {

        val cacheDir = File("${getCacheDirPath(context)}${File.separator}niuxiu")
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                diskLruCache = null
                return
            }
        }
        diskLruCache = try {
            DiskLruCache.open(cacheDir, 1, 1, 1024 * 1024 * 10)
        } catch (e: IOException) {
            null
        }
    }

    private fun getCacheDirPath(context: Context): String {

        var path = ""
        if (Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED) or !Environment.isExternalStorageRemovable()) {
            path = try {
                context.externalCacheDir?.absolutePath ?:Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            } catch (e: Exception) {
                ""
            }
        }
        if (path.isBlank()) {
            path = context.cacheDir.absolutePath
        }
        return path
    }

    fun <T> save(key: String, any: T) {
        Observable.create<String> {
            saveSync(key, any)
        }.compose(SwitchScheduleIO<String>())
            .subscribe()
    }

    fun get(key: String, listener: CacheListener<String>) {

        Observable.create<String> {
            val json = getSync(key)
            if (json.isNotBlank()) {
                it.onNext(json)
            } else {
                it.onError(Throwable("No data"))
            }
        }.compose(SwitchScheduleIO())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    listener.getDataStart()
                }
                override fun onNext(t: String) {
                    try {
                        listener.getDataSuccess(t)
                        listener.getDataFinish()
                    } catch (e: JsonSyntaxException) {
                        listener.getDataFailure(e.toString())
                        listener.getDataFinish()
                    }
                }

                override fun onError(e: Throwable) {
                    listener.getDataFailure("No data")
                    listener.getDataFinish()
                }

                override fun onComplete() {
                }
            })
    }

    fun <T> get(key: String, type: Type, listener: CacheListener<T>) {

        Observable.create<String> {
            val json = getSync(key)
            if (json.isNotBlank()) {
                it.onNext(json)
            } else {
                it.onError(Throwable("No data"))
            }
        }.compose(SwitchScheduleIO())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    listener.getDataStart()
                }
                override fun onNext(t: String) {
                    try {
                        gson.fromJson<T>(t, type).let {
                            listener.getDataSuccess(it)
                        }
                        listener.getDataFinish()
                    } catch (e: JsonSyntaxException) {
                        listener.getDataFailure(e.toString())
                        listener.getDataFinish()
                    }
                }

                override fun onError(e: Throwable) {
                    listener.getDataFailure("No data")
                    listener.getDataFinish()
                }

                override fun onComplete() {
                }
            })
    }

    fun remove(key: String) {
        Observable.create<Unit> {
            removeSync(key)
            it.onComplete()
        }.compose(SwitchScheduleIO())
            .subscribe(object : Observer<Unit> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Unit) {

                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }
            })
    }

    private fun getSync(key: String): String {
        return diskLruCache?.run {
            val snapshot = this.get(Md5Utils.encode(key))
            val json = snapshot.getString(0);
            snapshot.close()
            json
        } ?: ""
    }

    private fun <T> saveSync(key: String, data: T) {

        diskLruCache?.let {
            it.edit(Md5Utils.encode(key))?.run {
                set(0, gson.toJson(data))
                commit()
                it.flush()
            }
        }
    }

    private fun removeSync(key: String) {
        diskLruCache?.remove(key)
    }

    fun closeCache() {
        if (diskLruCache?.isClosed == false) {
            diskLruCache?.close()
        }
    }
}