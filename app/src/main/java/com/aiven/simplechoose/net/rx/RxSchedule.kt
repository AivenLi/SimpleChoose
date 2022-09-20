package com.aiven.simplechoose.net.rx

import io.reactivex.rxjava3.core.*

abstract class RxSchedule<T> protected constructor(
    private val subscribeOnScheduler: Scheduler,
    private val observerOnScheduler: Scheduler): ObservableTransformer<T, T>, CompletableTransformer {

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.subscribeOn(subscribeOnScheduler)
            .observeOn(observerOnScheduler)
    }

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(subscribeOnScheduler)
            .observeOn(observerOnScheduler)
    }
}