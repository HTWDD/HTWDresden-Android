package de.htwdd.htwdresden;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Eine individuelle Einstellung
 *
 * @author Kay Förster
 */
public class SettingsStudyGroupPreference extends DialogPreference {

    public SettingsStudyGroupPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Returns the layout resource that is used as the content View for the dialog
     */
    @Override
    public int getDialogLayoutResource() {
        return R.layout.preferences_studiengruppe;
    }

}
