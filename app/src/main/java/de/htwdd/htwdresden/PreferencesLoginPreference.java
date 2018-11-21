package de.htwdd.htwdresden;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import de.htwdd.htwdresden.classes.Const;

/**
 * Eine individuelle Einstellung
 *
 * @author Kay Förster
 */
public class PreferencesLoginPreference extends DialogPreference {

    private TextView username;
    private TextView password;

    public PreferencesLoginPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setDialogLayoutResource(R.layout.preferences_login);
    }

    @Override
    protected void onBindDialogView(@NonNull final View view) {
        super.onBindDialogView(view);
        username = view.findViewById(R.id.login_username);
        password = view.findViewById(R.id.login_password);

        final SharedPreferences sharedPreferences = getSharedPreferences();
        username.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_LOGIN_USERNAME, ""));
        password.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_LOGIN_PASSWORD, ""));
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            // Speichere Einstellungen/
            final SharedPreferences.Editor editor = getEditor();
            editor.putString(Const.preferencesKey.PREFERENCES_LOGIN_USERNAME, username.getText().toString());
            editor.putString(Const.preferencesKey.PREFERENCES_LOGIN_PASSWORD, password.getText().toString());
            editor.commit();
        }

    }
}
