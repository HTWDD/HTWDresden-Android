package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import de.htwdd.htwdresden.adapter.SpinnerRealmAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IWizardSaveSettings;
import de.htwdd.htwdresden.types.dataBinding.WizardDataBindingObject;
import de.htwdd.htwdresden.types.studyGroups.StudyCourse;
import de.htwdd.htwdresden.types.studyGroups.StudyGroup;
import de.htwdd.htwdresden.types.studyGroups.StudyYear;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Wizard Fragment für den Stunden- / Prüfungsplan
 *
 * @author Kay Förster
 */
public class WizardStgSettingsFragment extends Fragment implements IWizardSaveSettings {
    private static WizardDataBindingObject dataBindingObject = null;
    private Spinner spinnerStudyYear;
    private Spinner spinnerStudyCourse;
    private Spinner spinnerStudyGroup;
    private SpinnerRealmAdapter<StudyCourse> spinnerCourseAdapter;
    private SpinnerRealmAdapter<StudyGroup> spinnerGroupAdapter;

    public WizardStgSettingsFragment() {
        // Required empty public constructor
    }

    public static WizardStgSettingsFragment newInstance(@NonNull final WizardDataBindingObject dataBindingObject) {
        final WizardStgSettingsFragment fragment = new WizardStgSettingsFragment();
        fragment.setDataBindingObject(dataBindingObject);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.wizard_second_stg_settings, container, false);
        final Context context = getActivity();

        // Spinner Views holen
        spinnerStudyYear = (Spinner) view.findViewById(R.id.spinner_studyYear);
        spinnerStudyCourse = (Spinner) view.findViewById(R.id.spinner_studyCourse);
        spinnerStudyGroup = (Spinner) view.findViewById(R.id.spinner_studyGroup);
        final Spinner spinnerStudyDegree = (Spinner) view.findViewById(R.id.wizard_abschluss);

        // Studienjahrgänge laden
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<StudyYear> studyYears = realm.where(StudyYear.class).findAllSorted(Const.database.StudyGroups.STUDY_YEAR);
        studyYears.addChangeListener(new RealmChangeListener<RealmResults<StudyYear>>() {
            @Override
            public void onChange(RealmResults<StudyYear> element) {
                /**
                 * Bei Änderungen an der Datenbank müssen die Daten aus den Adapter entfernt werden da diese nicht mehr existent sind.
                 * Ansonsten würde dies zu einem Absturz der App führen
                 */
                spinnerCourseAdapter.updateData(null);
                spinnerGroupAdapter.updateData(null);
                selectSpinner(element);
            }
        });

        // Adapter erstellen
        final SpinnerRealmAdapter<StudyYear> spinnerYearAdapter = new SpinnerRealmAdapter<>(context, studyYears, getString(R.string.general_please_select));
        spinnerCourseAdapter = new SpinnerRealmAdapter<>(context, null);
        spinnerGroupAdapter = new SpinnerRealmAdapter<>(context, null);

        // Adapter den Spinner zuordnen und Auswahl-Listener anhängen
        spinnerStudyYear.setAdapter(spinnerYearAdapter);
        spinnerStudyYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                final StudyYear studyYear = spinnerYearAdapter.getItem(i);
                if (studyYear == null)
                    return;
                dataBindingObject.studyGroupYear = studyYear.studyYear;
                spinnerCourseAdapter.updateData(studyYear.studyCourses);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });
        spinnerStudyCourse.setAdapter(spinnerCourseAdapter);
        spinnerStudyCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                final StudyCourse studyCourse = spinnerCourseAdapter.getItem(i);
                if (studyCourse == null)
                    return;
                dataBindingObject.studyGroupCurse = studyCourse.studyCourse;
                spinnerGroupAdapter.updateData(studyCourse.studyGroups);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });
        spinnerStudyGroup.setAdapter(spinnerGroupAdapter);
        spinnerStudyGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                final StudyGroup studyGroup = spinnerGroupAdapter.getItem(i);
                if (studyGroup != null)
                    dataBindingObject.studyGroup = studyGroup.studyGroup;
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });

        spinnerStudyDegree.setSelection(dataBindingObject.studyDegree);
        spinnerStudyDegree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                dataBindingObject.studyDegree = i;
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });

        // Hinweis falls keine Daten verfügbar sind
        if (studyYears.size() == 0) {
            Toast.makeText(context, R.string.info_error_no_current_data, Toast.LENGTH_LONG).show();
        }

        selectSpinner(studyYears);

        return view;
    }

    /**
     * Selektiert und aktualisiert die Studiengruppen Spinner entsprechend der Daten im {@see dataBindingObject}
     *
     * @param studyYears Realm-Liste aller verfügbarer Studienjahrgänge
     */
    private void selectSpinner(@NonNull final RealmResults<StudyYear> studyYears) {
        final StudyYear studyYear = studyYears.where().equalTo(Const.database.StudyGroups.STUDY_YEAR, dataBindingObject.studyGroupYear).findFirst();
        if (studyYear != null) {
            spinnerStudyYear.setSelection(studyYears.indexOf(studyYear) + 1);

            // Studiengang setzen, falls vorhanden
            final StudyCourse studyCourse = studyYear.studyCourses.where().equalTo(
                    Const.database.StudyGroups.STUDY_COURSE,
                    dataBindingObject.studyGroupCurse,
                    Case.INSENSITIVE
            ).findFirst();
            if (studyCourse != null) {
                spinnerCourseAdapter.updateData(studyYear.studyCourses);
                spinnerStudyCourse.setSelection(studyYear.studyCourses.indexOf(studyCourse));

                // Studiengruppe setzen, falls vorhanden
                final StudyGroup studyGroup = studyCourse.studyGroups.where().equalTo(
                        Const.database.StudyGroups.STUDY_GROUP,
                        dataBindingObject.studyGroup,
                        Case.INSENSITIVE
                ).findFirst();
                if (studyGroup != null) {
                    spinnerGroupAdapter.updateData(studyCourse.studyGroups);
                    spinnerStudyGroup.setSelection(studyCourse.studyGroups.indexOf(studyGroup));
                }
            }
        }
    }

    @Override
    public void setDataBindingObject(@NonNull final WizardDataBindingObject dataBindingObject) {
        WizardStgSettingsFragment.dataBindingObject = dataBindingObject;
    }
}
