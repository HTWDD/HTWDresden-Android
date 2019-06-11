package de.htwdd.htwdresden;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Fragment zur Anzeige der App-Infos
 *
 * @author Kay Förster
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    private static void sendEmail(final @NonNull Context context, final String[] recipientList, final String title) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipientList);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[Android]");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        context.startActivity(Intent.createChooser(emailIntent, title));
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        final Context context = view.getContext();

        // Zeige die aktuelle Versionsnummer an
        final TextView viewVersion = view.findViewById(R.id.app_version);
        try {
            final PackageManager manager = context.getPackageManager();
            viewVersion.setText(manager.getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (final Exception e) {
            viewVersion.setText(getText(R.string.info_error));
        }

        // Setze OnClickListener für Button zum Öffnen der Projektwebseite
        view.findViewById(R.id.about_link_github).setOnClickListener(arg0 -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/HTWDD/HTWDresden"))));

        // Setze OnClickListener für Email
        view.findViewById(R.id.app_email).setOnClickListener(v -> sendEmail(context, new String[]{getText(R.string.app_email).toString()}, getString(R.string.about_mail_info_message)));

        return view;
    }
}
