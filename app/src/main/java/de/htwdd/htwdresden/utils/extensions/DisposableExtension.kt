package de.htwdd.htwdresden.utils.extensions

import de.htwdd.htwdresden.utils.AutoDisposableUtil
import io.reactivex.disposables.Disposable

fun Disposable.addTo(autoDisposable: AutoDisposableUtil) = autoDisposable.add(this)