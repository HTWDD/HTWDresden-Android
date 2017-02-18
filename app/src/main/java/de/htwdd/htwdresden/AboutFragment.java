package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.htwdd.htwdresden.interfaces.INavigation;


/**
 * Fragment zur Anzeige der App-Infos
 *
 * @author Kay Förster
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Setze Toolbartitle
        ((INavigation)getActivity()).setTitle(getResources().getString(R.string.navi_about));

        // Hole Views
        final TextView viewVersion = (TextView) view.findViewById(R.id.app_version);
        final TextView textGitHub = (TextView) view.findViewById(R.id.about_link_github);
        final TextView textMail = (TextView) view.findViewById(R.id.app_email);

        // Zeige die aktuelle Versionsnummer an
        try {
            final PackageManager manager = this.getActivity().getPackageManager();
            viewVersion.setText(manager.getPackageInfo(this.getActivity().getPackageName(), 0).versionName);
        } catch (Exception e) {
            viewVersion.setText(getText(R.string.info_error));
        }

        // Setze OnClickListener für Button zum Öffnen der Projektwebseite
        textGitHub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/HTWDD/HTWDresden"));
                startActivity(browserIntent);
            }
        });

        // Setze OnClickListener für Email
        textMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(getActivity(), new String[]{getText(R.string.app_email).toString()}, getString(R.string.about_mail_info_message), "[Android]", "");
            }
        });

        return view;
    }

    private static void sendEmail(Context context, String[] recipientList, String title, String subject, String body) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipientList);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(emailIntent, title));
    }
}
