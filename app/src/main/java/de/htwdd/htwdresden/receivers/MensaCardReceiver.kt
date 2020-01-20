package de.htwdd.htwdresden.receivers

import android.content.Context
import android.content.Intent
import com.heinrichreimer.canteenbalance.app.AbstractCardBalanceReceiver
import com.heinrichreimer.canteenbalance.cardreader.CardBalance
import de.htwdd.htwdresden.ui.views.activities.MensaCreditActivity

class MensaCardReceiver: AbstractCardBalanceReceiver() {
    override fun onReceiveCardBalance(context: Context?, cardBalance: CardBalance?) {
        cardBalance?.let { sCardBalance ->
            val intent = Intent(context, MensaCreditActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT + Intent.FLAG_ACTIVITY_NEW_TASK
                putExtras(MensaCreditActivity.setBundle(sCardBalance))
            }
            context?.startActivity(intent)
        }
    }
}