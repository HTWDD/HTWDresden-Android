package de.htwdd.htwdresden.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import de.htwdd.htwdresden.classes.Const;

/**
 * @author Kay Förster
 */
class BroadcastNotifier {
    private final LocalBroadcastManager broadcastNotifier;
    private final Intent intentCategory;

    BroadcastNotifier(@NonNull final Context context, @NonNull final String intentCategory) {
        broadcastNotifier = LocalBroadcastManager.getInstance(context);
        this.intentCategory = new Intent();
        this.intentCategory.addCategory(intentCategory);
    }

    void notifyStatus(final int statusCode, @NonNull final String message) {
        final Intent intent = new Intent(intentCategory);
        intent.setAction(Const.IntentParams.BROADCAST_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        // Puts Data data into the Intent
        intent.putExtra(Const.IntentParams.BROADCAST_MESSAGE, message);
        intent.putExtra(Const.IntentParams.BROADCAST_CODE, statusCode);
        broadcastNotifier.sendBroadcast(intent);
    }

    void notifyStatus(final int statusCode) {
        final Intent intent = new Intent(intentCategory);
        intent.setAction(Const.IntentParams.BROADCAST_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        // Puts Data data into the Intent
        intent.putExtra(Const.IntentParams.BROADCAST_CODE, statusCode);
        broadcastNotifier.sendBroadcast(intent);
    }
}
