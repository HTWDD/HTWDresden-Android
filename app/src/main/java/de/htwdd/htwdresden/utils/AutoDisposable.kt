package de.htwdd.htwdresden.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoDisposable: LifecycleObserver {
    private lateinit var compositeDisposable: CompositeDisposable
    fun bindTo(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        compositeDisposable = CompositeDisposable()
    }

    fun add(disposable: Disposable) {
        if (::compositeDisposable.isInitialized) {
            compositeDisposable.add(disposable)
        } else {
            throw NotImplementedError("AutoDisposable needs a lifecycle, bind it!")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() = compositeDisposable.dispose()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() = compositeDisposable.clear()
}