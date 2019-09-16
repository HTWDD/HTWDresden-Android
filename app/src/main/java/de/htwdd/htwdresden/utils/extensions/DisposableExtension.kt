package de.htwdd.htwdresden.utils.extensions

import de.htwdd.htwdresden.utils.AutoDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(autoDisposable: AutoDisposable) = autoDisposable.add(this)