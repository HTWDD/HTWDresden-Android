package de.htwdd.htwdresden.utils.extensions

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


fun <T> Observable<T>.debug(): Observable<T> {
    return this
        .doOnDispose { debug("disposed") }
        .doOnComplete { debug("completed") }
        .doOnNext { debug("onNext: $it") }
        .doOnError { debug("onError: $it") }
        .doOnSubscribe { debug("subscribed: $it") }
        .doOnTerminate { debug("terminated") }
}

fun <T> Observable<T>.runInThread(type: Scheduler = Schedulers.newThread()): Observable<T> {
    return this
        .subscribeOn(type)
        .observeOn(type)
}

fun <T> Observable<T>.runInUiThread(): Observable<T> {
    return this
        .observeOn(AndroidSchedulers.mainThread())
}
