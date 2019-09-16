package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieDrawable
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.interfaces.Swipeable
import de.htwdd.htwdresden.utils.extensions.addTo
import de.htwdd.htwdresden.utils.extensions.click
import de.htwdd.htwdresden.utils.extensions.disposeBag
import de.htwdd.htwdresden.utils.extensions.runInUiThread
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import kotlinx.android.synthetic.main.fragment_analytics.*

class AnalyticsFragment: Fragment(), Swipeable {

    companion object {
        private var delegate: SwipeDelegate? = null
        fun newInstance(delgate: SwipeDelegate? = null) = AnalyticsFragment().apply {
            this@Companion.delegate = delgate
        }
    }
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_analytics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    override fun onResume() {
        super.onResume()
        cph.onChanged().runInUiThread().subscribe {
            when (it) {
                is CryptoSharedPreferencesHolder.SubscribeType.Anaytics -> checkState()
            }
        }.addTo(disposeBag)
        checkState()
    }

    private fun setup() {
        checkState()
        btnYes.click {
            cph.setAnayltics(true)
            delegate?.moveNext()
        }
    }

    private fun checkState() {
        if (cph.hasAnalytics()) {
            btnYes.apply {
                text = "✓"
                isEnabled = false
            }

            lottieAnimationView.apply {
                setAnimation("PulseGray.json")
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
        }
    }
}