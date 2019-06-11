package de.htwdd.htwdresden;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.heinrichreimer.canteenbalance.cardreader.CardBalance;

public class MensaCreditActivity extends AppCompatActivity {
    private static final String MENSA_CREDIT = "MENSA_CREDIT";
    private static final String MENSA_LAST_TRANSACTION = "MENSA_LAST_TRANSACTION";
    private static final String MENSA_HAS_MENSA_LAST_TRANSACTION = "MENSA_HAS_MENSA_LAST_TRANSACTION";

    public static Bundle setArguments(final @NonNull CardBalance cardBalance) {
        final Bundle bundle = new Bundle();
        bundle.putString(MENSA_CREDIT, cardBalance.getBalance());
        bundle.putString(MENSA_LAST_TRANSACTION, cardBalance.getLastTransaction());
        bundle.putBoolean(MENSA_HAS_MENSA_LAST_TRANSACTION, cardBalance.isLastTransactionSupported());
        return bundle;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mensa_dialog_credit);

        // Schließen Button
        findViewById(R.id.mensa_button_close).setOnClickListener(view -> finish());
        // Guthaben anzeigen
        showCredit(getIntent().getExtras());
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        showCredit(intent.getExtras());
        super.onNewIntent(intent);
    }


   private void showCredit(@Nullable final Bundle bundle) {
        if (bundle == null) {
            return;
        }

        ((TextView) findViewById(R.id.mensa_credit_current)).setText(getString(R.string.mensa_euro_string, bundle.getString(MENSA_CREDIT, "0,00")));
        if (bundle.getBoolean(MENSA_HAS_MENSA_LAST_TRANSACTION, false)) {
            ((TextView) findViewById(R.id.mensa_last_transaction)).setText(getString(R.string.mensa_euro_string, bundle.getString(MENSA_LAST_TRANSACTION, "0,00")));
        } else {
            ((TextView) findViewById(R.id.mensa_last_transaction)).setText("-");
        }
    }
}
