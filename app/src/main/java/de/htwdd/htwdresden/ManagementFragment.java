package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.semsterPlan.Semester;
import de.htwdd.htwdresden.types.semsterPlan.TimePeriod;
import io.realm.Realm;
import io.realm.RealmList;


/**
 * Fragement zur Übersicht aller wichtigen Uni-Einrichtungen
 */
public class ManagementFragment extends Fragment {
    private Realm realm;
    private View mLayout;

    public ManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_management, container, false);
        realm = Realm.getDefaultInstance();

        // Setze Toolbartitle
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_uni_administration));

        mLayout.findViewById(R.id.management_office).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/hochschule/zentrale-verwaltung-dezernate/dezernat-studienangelegenheiten/studentensekretariat.html")));
            }
        });

        mLayout.findViewById(R.id.management_printing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/intern/technik-druck-it-dienste/drucken-kopieren.html")));
            }
        });

        mLayout.findViewById(R.id.management_examination_office).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/hochschule/zentrale-verwaltung-dezernate/dezernat-studienangelegenheiten/pruefungsamt.html")));
            }
        });

        mLayout.findViewById(R.id.management_stura).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.stura.htw-dresden.de/")));
            }
        });

        // Semesterplan anzeigen
        showSemesterInfo();

        return mLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    /**
     * Aktuellen Semesterplan anzeigen
     */
    private void showSemesterInfo() {
        final Date date = new Date();
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        final Semester semester = realm.where(Semester.class)
                .lessThanOrEqualTo(Const.database.SemesterPlan.SEMESTER_START, date)
                .greaterThanOrEqualTo(Const.database.SemesterPlan.SEMESTER_END, date)
                .findFirst();

        if (semester == null) {
            mLayout.findViewById(R.id.management_semesterplan).setVisibility(View.GONE);
            return;
        }

        // Allgemeine Semesterinformationen
        final String[] semesterName = (mLayout.getResources().getStringArray(R.array.semesterName));
        final String semesterBezeichnung = "S".equals(semester.getType()) ? semesterName[0] : semesterName[1];
        ((TextView) mLayout.findViewById(R.id.semesterplan_Bezeichnung)).setText(semesterBezeichnung + " " + semester.getYear());
        ((TextView) mLayout.findViewById(R.id.semesterplan_lecturePeriod)).setText(getString(
                    R.string.timetable_ds_list_simple,
                dateFormat.format(semester.getLecturePeriod().getBeginDay()),
                dateFormat.format(semester.getLecturePeriod().getEndDay())
            ));
        ((TextView) mLayout.findViewById(R.id.semesterplan_pruefPeriod)).setText(getString(
                R.string.timetable_ds_list_simple,
                dateFormat.format(semester.getExamsPeriod().getBeginDay()),
                dateFormat.format(semester.getExamsPeriod().getEndDay())
        ));
        ((TextView) mLayout.findViewById(R.id.semesterplan_reregistration)).setText(getString(
                R.string.timetable_ds_list_simple,
                dateFormat.format(semester.getReregistration().getBeginDay()),
                dateFormat.format(semester.getReregistration().getEndDay())
        ));

        // Freie Tage anzeigen
        final RealmList<TimePeriod> freeDays = semester.getFreeDays();
        final TextView semesterPlanFreieTage = (TextView) mLayout.findViewById(R.id.semesterplan_freieTage);
        final TextView semesterPlanFreieTageNamen = (TextView) mLayout.findViewById(R.id.semesterplan_freieTageNamen);
        for (final TimePeriod period : freeDays) {
            if (semesterPlanFreieTage.getText().length() != 0) {
                semesterPlanFreieTageNamen.append("\n");
                semesterPlanFreieTage.append("\n");
            }
            semesterPlanFreieTageNamen.append(period.getName());
            semesterPlanFreieTage.append(getString(
                        R.string.timetable_ds_list_simple,
                    dateFormat.format(period.getBeginDay()),
                    dateFormat.format(period.getEndDay())));
        }
    }
}
