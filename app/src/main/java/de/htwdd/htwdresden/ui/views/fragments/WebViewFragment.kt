package de.htwdd.htwdresden.ui.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings.LOAD_DEFAULT
import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.viewmodels.fragments.WebViewModel
import de.htwdd.htwdresden.utils.extensions.getViewModel
import kotlinx.android.synthetic.main.fragment_web_view.*

class WebViewFragment: Fragment(R.layout.fragment_web_view) {

    companion object {
        const val BUNDLE_ARG_URL = "URL"
    }

    private val viewModel by lazy { getViewModel<WebViewModel>() }
    private val url: String? by lazy { arguments?.getString(BUNDLE_ARG_URL) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.apply {
            settings.apply {
                setAppCacheEnabled(true)
                cacheMode = LOAD_DEFAULT
                javaScriptEnabled = true
            }
            loadUrl(this@WebViewFragment.url)
        }
    }

}