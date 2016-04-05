package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.SemesterPlanDAO;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.FreeDay;
import de.htwdd.htwdresden.types.SemesterPlan;


/**
 * Fragement zur Übersicht aller wichtigen Uni-Einrichtungen
 */
public class ManagementFragment extends Fragment {
    private View mLayout;

    public ManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_management, container, false);

        // Setze Toolbartitle
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_uni_administration));

        CardView management_office = (CardView) mLayout.findViewById(R.id.management_office);
        management_office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/hochschule/zentrale-verwaltung-dezernate/dezernat-studienangelegenheiten/studentensekretariat.html"));
                getActivity().startActivity(browserIntent);
            }
        });

        CardView management_printing = (CardView) mLayout.findViewById(R.id.management_printing);
        management_printing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/intern/technik-druck-it-dienste/drucken-kopieren.html"));
                getActivity().startActivity(browserIntent);
            }
        });

        CardView management_examination_office = (CardView) mLayout.findViewById(R.id.management_examination_office);
        management_examination_office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/hochschule/zentrale-verwaltung-dezernate/dezernat-studienangelegenheiten/pruefungsamt.html"));
                getActivity().startActivity(browserIntent);
            }
        });

        CardView management_stura = (CardView) mLayout.findViewById(R.id.management_stura);
        management_stura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.stura.htw-dresden.de/"));
                getActivity().startActivity(browserIntent);
            }
        });

        // Semesterplan anzeigen
        getSemesterplan();

        return mLayout;
    }

    private void getSemesterplan() {
        final SemesterPlanDAO semesterPlanDAO = new SemesterPlanDAO(new DatabaseManager(getActivity()));
        final SemesterPlan semesterPlan = semesterPlanDAO.getSemsterplan(Calendar.getInstance().get(Calendar.YEAR), Const.Semester.getActualSemester());
        final CardView cardView = (CardView) mLayout.findViewById(R.id.management_semesterplan);

        if (semesterPlan == null) {
            cardView.setVisibility(View.GONE);
            return;
        } else cardView.setVisibility(View.VISIBLE);

        final TextView semesterplanBezeichnung = (TextView) mLayout.findViewById(R.id.semesterplan_Bezeichnung);
        final TextView semesterplanLecturePeriod = (TextView) mLayout.findViewById(R.id.semesterplan_lecturePeriod);
        final TextView semesterplanFreieTage = (TextView) mLayout.findViewById(R.id.semesterplan_freieTage);
        final TextView semesterplanFreieTageNamen = (TextView) mLayout.findViewById(R.id.semesterplan_freieTageNamen);
        final TextView semesterplanPruefPeriod = (TextView) mLayout.findViewById(R.id.semesterplan_pruefPeriod);
        final TextView semesterplanRegistration = (TextView) mLayout.findViewById(R.id.semesterplan_reregistration);

        String bezeichnung = Const.Semester.getSemesterName(mLayout.getResources().getStringArray(R.array.semesterName), semesterPlan.getType()) + " " + semesterPlan.getYear();
        semesterplanBezeichnung.setText(bezeichnung);
        semesterplanLecturePeriod.setText(semesterPlan.getLecturePeriod().toString());
        semesterplanPruefPeriod.setText(semesterPlan.getExamsPeriod().toString());
        semesterplanRegistration.setText(semesterPlan.getReregistration().toString());

        semesterplanFreieTageNamen.setText(null);
        semesterplanFreieTage.setText(null);

        if (semesterPlan.getFreeDays() == null)
            return;

        for (FreeDay freeDay : semesterPlan.getFreeDays()) {
            if (semesterplanFreieTage.getText().length() != 0) {
                semesterplanFreieTageNamen.append("\n");
                semesterplanFreieTage.append("\n");
            }
            semesterplanFreieTageNamen.append(freeDay.getName());
            semesterplanFreieTage.append(freeDay.toString());
        }
    }
}
