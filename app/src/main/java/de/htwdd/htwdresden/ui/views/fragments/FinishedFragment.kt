package de.htwdd.htwdresden.ui.views.fragments

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Swipeable
import de.htwdd.htwdresden.utils.extensions.click
import de.htwdd.htwdresden.utils.extensions.emit
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import kotlinx.android.synthetic.main.fragment_finished.*

class FinishedFragment: Fragment(R.layout.fragment_finished), Swipeable {

    companion object {
        private var delegate: SwipeDelegate? = null
        fun newInstance(delegate: SwipeDelegate? = null) = FinishedFragment().apply {
            this@Companion.delegate = delegate
        }
    }

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnFinish.click {
            cph.setOnboarding(false)
            delegate?.moveNext()
        }
        setup()
    }

    override fun onResume() {
        super.onResume()
        if (!lottieAnimationView.isAnimating) {
            lottieAnimationView.playAnimation()
        }
        konfetti.emit()
    }

    private fun setup() {
        lottieAnimationView.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                btnFinish.isEnabled = true
            }
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {
                btnFinish.isEnabled = false
            }
        })
    }
}