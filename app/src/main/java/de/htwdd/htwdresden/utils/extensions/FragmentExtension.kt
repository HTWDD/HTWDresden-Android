package de.htwdd.htwdresden.utils.extensions

import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.htwdd.htwdresden.utils.AutoDisposable
import java.lang.ref.WeakReference

/**
 * Creates a Weak Reference for Fragments.
 * The Block-Code will only run, when weak ref. is not collected from gc.
 *
 * usage:
 *
 * weak { self: Fragment ->
 *  <b>code comes hier</b>
 * }
 */
fun <T: Fragment> T.weak(block: (fragment: T) -> Unit) {
    val weakReference = WeakReference(this)
    weakReference.get()?.let {
        block(it)
    }
}

private fun <T: Fragment> T.disposeBag(): AutoDisposable {
    return AutoDisposable().apply {
        bindTo(this@disposeBag.lifecycle)
    }
}

val <T: Fragment> T.disposeBag: AutoDisposable
    get() = disposeBag()

inline fun <reified T: ViewModel> Fragment.getViewModel(): T = ViewModelProvider(this).get(T::class.java)

fun <T: Fragment> T.hideKeyboard() {
    context?.let {
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let { view ->
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

fun Fragment.withArgumentsOf(vararg pairs: Pair<String, Any?>): Fragment {
    arguments = bundleOf(*pairs)
    return this
}

inline fun <reified T : ViewDataBinding> Fragment.inflateDataBinding(layoutId: Int, container: ViewGroup?): T = DataBindingUtil.inflate<T>(layoutInflater, layoutId, container, false).apply { lifecycleOwner = viewLifecycleOwner }