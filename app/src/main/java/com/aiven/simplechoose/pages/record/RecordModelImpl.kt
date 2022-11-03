package com.aiven.simplechoose.pages.record

import com.aiven.simplechoose.db.SimpleDataBase
import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.RequestCallback
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RecordModelImpl: RecordContract.Model {

    override fun getRecordByPage(
        page: Int,
        size: Int,
        requestCallback: RequestCallback<List<TestPaperRecord>>
    ) {
        val dao = SimpleDataBase.getInstance().testPaperRecordDao()
        val observable = Observable.create<List<TestPaperRecord>> { emitter ->
            runCatching {
                dao.selectByPage(size, page)
            }.onSuccess {
                emitter.onNext(it)
            }.onFailure {
                emitter.onError(it)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<TestPaperRecord>> {
                override fun onSubscribe(d: Disposable) {
                    requestCallback.onRequestStart(d)
                }

                override fun onNext(t: List<TestPaperRecord>) {
                    requestCallback.onSuccess(t)
                    requestCallback.onRequestFinish()
                }

                override fun onError(e: Throwable) {
                    requestCallback.onFailure(BaseError(-1, e.toString()))
                    requestCallback.onRequestFinish()
                }

                override fun onComplete() {
                }
            })
    }
}