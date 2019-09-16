package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieDrawable
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Swipeable
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import kotlinx.android.synthetic.main.fragment_crashlytics.*

class CrashlyticsFragment: Fragment(), Swipeable {

    companion object {
        private var delegate: SwipeDelegate? = null
        fun newInstance(delegate: SwipeDelegate? = null) = CrashlyticsFragment().apply {
            this@Companion.delegate = delegate
        }
    }

    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_crashlytics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    override fun onResume() {
        super.onResume()
        cph.onChanged().runInUiThread().subscribe {
            when (it) {
                is CryptoSharedPreferencesHolder.SubscribeType.Crashlytics -> checkState()
            }
        }.addTo(disposeBag)
        checkState()
    }

    private fun setup() {
        checkState()
        btnYes.click {
            cph.setCrashlytics(true)
            delegate?.moveNext()
        }
    }

    private fun checkState() {
        btnYes.isEnabled = cph.hasAnalytics()

        if (cph.hasAnalytics()) {
            tvNeedsAnalytics.hide()
            lottieAnimationView.apply {
                setAnimation("PulseBlue.json")
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }

            if (cph.hasCrashlytics()) {
                btnYes.apply {
                    isEnabled = false
                    text = "✓"
                }

                lottieAnimationView.apply {
                    setAnimation("PulseGray.json")
                    repeatCount = LottieDrawable.INFINITE
                    playAnimation()
                }
            }
        }

    }
}