package de.htwdd.htwdresden.ui.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.heinrichreimer.canteenbalance.cardreader.CardBalance
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.utils.extensions.click
import de.htwdd.htwdresden.utils.extensions.toggle
import kotlinx.android.synthetic.main.activity_mensa_credit.*

class MensaCreditActivity: AppCompatActivity() {

    companion object {
        const val ARG_CREDIT                = "CREDIT"
        const val ARG_LAST_TRANSACTION      = "LAST_TRANSACTION"
        const val ARG_HAS_LAST_TRANSACTION  = "HAS_LAST_TRANSACTION"

        fun setBundle(cardBalance: CardBalance) = bundleOf(
            ARG_CREDIT to cardBalance.balance,
            ARG_LAST_TRANSACTION to cardBalance.lastTransaction,
            ARG_HAS_LAST_TRANSACTION to cardBalance.isLastTransactionSupported
        )
    }

    private val credit by lazy {
        resources.getString(R.string.mensa_euro, intent.extras?.getString(ARG_CREDIT, "0,00") ?: "0,00")
    }
    private val hasLastTransaction by lazy {
        intent.extras?.getBoolean(ARG_HAS_LAST_TRANSACTION, false) == true
                && (intent.extras?.getString(ARG_LAST_TRANSACTION, "0,00") ?: "0,00") != "0,00"
    }
    private val lastTransaction by lazy {
        if (hasLastTransaction) {
            resources.getString(R.string.mensa_euro, intent.extras?.getString(ARG_LAST_TRANSACTION, "0,00") ?: "0,00")
        } else {
            ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensa_credit)
        setup()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setup()
    }

    private fun setup() {
        btnClose.click { finish() }
        tvCredit.text = credit
        tvLastTransactionLabel.toggle(hasLastTransaction)
        tvLastTransaction.apply {
            toggle(hasLastTransaction)
            text = lastTransaction
        }
    }
}