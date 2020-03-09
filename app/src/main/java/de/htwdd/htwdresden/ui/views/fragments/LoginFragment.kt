package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Swipeable
import de.htwdd.htwdresden.ui.viewmodels.fragments.LoginViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.nio.charset.Charset
import kotlin.properties.Delegates.observable

class LoginFragment: Fragment(R.layout.fragment_login), Swipeable {

    companion object {
        const val ARG_IS_BOARDING = "isBoarding"
        private var delegate: SwipeDelegate? = null
        fun newInstance(delegate: SwipeDelegate? = null) = LoginFragment().apply {
            this@Companion.delegate = delegate
            arguments = bundleOf(ARG_IS_BOARDING to true)
        }
    }

    private val viewModel by lazy { getViewModel<LoginViewModel>() }

    private inner class State {
        var userName: String? by observable<String?>(null) { _, _, _ ->
            canLogin = hasUserName && hasPassword
        }
        val hasUserName: Boolean
            get() = userName?.let { it.length >= 5 } ?: false

        var password: String? by observable<String?>(null) { _, _, _ ->
            canLogin = hasUserName && hasPassword
        }
        val hasPassword: Boolean
            get() = password?.isNotEmpty() ?: false

        var canLogin: Boolean by observable(false) { _, _, newValue ->
            btnLogin.isEnabled = newValue
        }
    }
    private val state by lazy { State() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        btnLater.toggle(arguments?.getBoolean(ARG_IS_BOARDING) == false)

        userName.addTextChangedListener { text ->
            state.userName = text.toString()
        }

        password.addTextChangedListener { text ->
            state.password = text.toString()
        }

        btnLater.click {
            findNavController().popBackStack()
        }

        btnLogin.click {
            val authToken = Base64.encodeToString("s${state.userName}:${state.password}".toByteArray(Charset.forName("UTF-8")), Base64.DEFAULT).trimEnd()
            viewModel
                .login(authToken)
                .runInUiThread()
                .subscribe({
                    weak { self ->
                        Handler().postDelayed({
                            self.viewModel.saveToken(authToken)
                            if (arguments?.getBoolean(ARG_IS_BOARDING) == false) {
                                self.findNavController().popBackStack()
                            } else {
                                delegate?.moveNext()
                            }
                        }, 250)
                    }
                }, {
                    weak { self ->
                        self.showErrorDialog()
                    }
                }).addTo(disposeBag)
        }
    }

    private fun showErrorDialog() {
        MaterialDialog(context!!).show {
            title(R.string.login_error)
            message(R.string.login_error_message)
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}