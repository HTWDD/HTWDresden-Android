package de.htwdd.htwdresden.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.heinrichreimer.canteenbalance.app.AbstractCardBalanceReceiver;
import com.heinrichreimer.canteenbalance.cardreader.CardBalance;

import de.htwdd.htwdresden.MensaCreditActivity;

/**
 * Receiver zur Benachrichtigung von Mensa-Guthaben
 *
 * @author Kay Förster
 */
public class MensaCreditReceiver extends AbstractCardBalanceReceiver {
    @Override
    protected void onReceiveCardBalance(final Context context, final CardBalance cardBalance) {
        Log.i("MensaReceiver", "Mensa Guthaben erkannt: " + cardBalance);

        final Intent intent = new Intent(context, MensaCreditActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT + Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(MensaCreditActivity.setArguments(cardBalance));
        context.startActivity(intent);
    }
}
