package de.htwdd.htwdresden;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.Realm;

/**
 * Dialog zu zurücksetzen des Stundenplans. Zurücksetzen bedeutet dabei Stundenplan komplett löschen und neu laden
 *
 * @author Kay Förster
 */
public class TimetableResetDialogFragment extends DialogFragment {
    public static TimetableResetDialogFragment newInstance() {
        return new TimetableResetDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context context = requireContext();
        return new AlertDialog.Builder(context)
                .setTitle(R.string.timetable_overview_options_menu_timetable_reset)
                .setMessage(R.string.timetable_overview_options_menu_timetable_reset_message)
                .setPositiveButton(R.string.timetable_overview_options_menu_timetable_reset, (dialogInterface, i) -> {
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> realm1.where(LessonUser.class).findAll().deleteAllFromRealm());
                    realm.close();
                    TimetableHelper.startSyncService(context);
                })
                .setNegativeButton(R.string.general_cancel, (dialogInterface, i) -> {
                })
                .create();
    }
}
