package de.htwdd.htwdresden;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

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

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context context = getActivity();
        return new AlertDialog.Builder(context)
                .setTitle(R.string.timetable_overview_options_menu_timetable_reset)
                .setMessage(R.string.timetable_overview_options_menu_timetable_reset_message)
                .setPositiveButton(R.string.timetable_overview_options_menu_timetable_reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        final Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull final Realm realm) {
                                realm.where(LessonUser.class).findAll().deleteAllFromRealm();
                            }
                        });
                        realm.close();
                        TimetableHelper.startSyncService(context);
                    }
                })
                .setNegativeButton(R.string.general_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                    }
                })
                .create();
    }
}
