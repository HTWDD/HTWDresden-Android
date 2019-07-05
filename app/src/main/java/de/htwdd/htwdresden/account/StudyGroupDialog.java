package de.htwdd.htwdresden.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.DialogFragment;

import de.htwdd.htwdresden.PreferencesActivity;
import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.studyGroups.StudyGroup;
import io.realm.Realm;

public class StudyGroupDialog extends DialogFragment {

    private Realm realm;
    private int studyYear;
    private String studyCourse;
    private String studyGroup;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
// Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.preferences_studiengruppe, null))
                // Add action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StudyGroupDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}