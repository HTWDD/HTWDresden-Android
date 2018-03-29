package de.htwdd.htwdresden;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.semsterPlan.Semester;
import de.htwdd.htwdresden.types.semsterPlan.TimePeriod;
import io.realm.Realm;
import io.realm.RealmList;


/**
 * Fragment zur Übersicht aller wichtigen Uni-Einrichtungen
 */
public class ManagementFragment extends Fragment {
    private Realm realm;
    private View mLayout;

    public ManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_management, container, false);
        realm = Realm.getDefaultInstance();

        mLayout.findViewById(R.id.management_office).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/de/hochschule/hochschulstruktur/zentrale-verwaltung-dezernate/dezernat-studienangelegenheiten/studentensekretariat.html"))));
        mLayout.findViewById(R.id.management_examination_office).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/de/hochschule/hochschulstruktur/zentrale-verwaltung-dezernate/dezernat-studienangelegenheiten/pruefungsamt.html"))));
        mLayout.findViewById(R.id.management_stura).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.stura.htw-dresden.de/"))));

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

        // Allgemeine Studienjahresablaufplan
        final String semesterBezeichnung = mLayout.getResources().getString("S".equals(semester.getType()) ? R.string.academic_year_summer : R.string.academic_year_winter);
        ((TextView) mLayout.findViewById(R.id.semesterplan_Bezeichnung)).setText(getString(R.string.general_concat, semesterBezeichnung, String.valueOf(semester.getYear())));
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
        final TextView semesterPlanFreieTage = mLayout.findViewById(R.id.semesterplan_freieTage);
        final TextView semesterPlanFreieTageNamen = mLayout.findViewById(R.id.semesterplan_freieTageNamen);
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
