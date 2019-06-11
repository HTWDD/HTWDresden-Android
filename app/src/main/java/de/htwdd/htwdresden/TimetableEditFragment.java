package de.htwdd.htwdresden;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.LessonUser;
import de.htwdd.htwdresden.types.LessonWeek;
import io.realm.Realm;
import io.realm.RealmList;


public class TimetableEditFragment extends Fragment {
    private static final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private static final String[] nameOfDays = Arrays.copyOfRange(DateFormatSymbols.getInstance().getWeekdays(), 2, 8);
    private static final String[] listOfDs = new String[Const.Timetable.beginDS.length + 1];
    private Realm realm;
    private View mLayout;
    private TextInputEditText lesson_name;
    private TextInputEditText lesson_tag;
    private TextInputEditText lesson_prof;
    private Spinner lesson_type;
    private TextInputEditText lesson_rooms;
    private Spinner lesson_week;
    private Spinner lesson_day;
    private Spinner lesson_ds;
    private EditText lesson_beginTime;
    private EditText lesson_endTime;
    private TextInputEditText lesson_weeksOnly;
    private LessonUser lesson = null;
    private int startTime;
    private int endTime;
    private boolean[] selectedKws = new boolean[53];

    public TimetableEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final Activity activity = requireActivity();
        final Bundle bundle = new Bundle(getArguments());

        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_timetable_edit, container, false);
        realm = Realm.getDefaultInstance();

        // Titel ändern
        if (activity instanceof INavigation) {
            ((INavigation) activity).setTitle(getResources().getString(R.string.timetable_edit_activity_titel));
        }

        // Alten Zustand wiederherstellen
        if (savedInstanceState != null) {
            startTime = savedInstanceState.getInt("startTime", 0);
            endTime = savedInstanceState.getInt("endTime", 0);
            selectedKws = savedInstanceState.getBooleanArray("selectedKws");
        }

        // Views suchen und befüllen
        createLocalResources(bundle);

        createListener();

        // Wenn keine Lehrveranstaltung übergeben wurde, hier aufhören
        if (!bundle.containsKey(Const.BundleParams.TIMETABLE_LESSON_ID))
            return mLayout;

        lesson = realm.where(LessonUser.class).endsWith(Const.database.Lesson.ID, bundle.getString(Const.BundleParams.TIMETABLE_LESSON_ID, "")).findFirst();

        if (lesson != null) {
            // Formular mit Daten füllen
            fillForms(lesson);

            // Button zum Löschen / Ausblenden einer Lehrveranstaltung
            final Button buttonDelete = mLayout.findViewById(R.id.timetable_edit_LessonDelete);
            buttonDelete.setEnabled(true);

            // Lehrveranstaltung wurde vom Nutzer erstellt und kann gelöscht werden
            if (lesson.isCreatedByUser()) {
                buttonDelete.setOnClickListener(view -> {
                    realm.beginTransaction();
                    lesson.deleteFromRealm();
                    realm.commitTransaction();
                    Toast.makeText(activity, R.string.timetable_edit_lessonDeleteSuccess, Toast.LENGTH_SHORT).show();
                    activity.finish();
                });
            }
            // Lehrveranstaltung nur ausblenden
            else {
                buttonDelete.setText(lesson.isHideLesson() ? R.string.timetable_edit_show : R.string.timetable_edit_hide);
                buttonDelete.setOnClickListener(view -> {
                    realm.beginTransaction();
                    lesson.setHideLesson(!lesson.isHideLesson());
                    realm.commitTransaction();
                    Toast.makeText(activity, R.string.timetable_edit_lessonSaveSuccess, Toast.LENGTH_SHORT).show();
                    activity.finish();
                });
            }
        }

        return mLayout;
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("startTime", startTime);
        outState.putInt("endTime", endTime);
        outState.putBooleanArray("selectedKws", selectedKws);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    /**
     * Holt alle Views aus dem Layout
     */
    private void createLocalResources(@NonNull final Bundle arguments) {
        final int count = listOfDs.length;
        final Resources resources = getResources();
        final Context context = requireContext();

        // Views finden
        lesson_name = mLayout.findViewById(R.id.timetable_edit_lessonName);
        lesson_tag = mLayout.findViewById(R.id.timetable_edit_lessonTag);
        lesson_prof = mLayout.findViewById(R.id.timetable_edit_lessonProf);
        lesson_type = mLayout.findViewById(R.id.timetable_edit_lessonType);
        lesson_rooms = mLayout.findViewById(R.id.timetable_edit_lessonRooms);
        lesson_week = mLayout.findViewById(R.id.timetable_edit_lessonWeek);
        lesson_day = mLayout.findViewById(R.id.timetable_edit_lessonDay);
        lesson_ds = mLayout.findViewById(R.id.timetable_edit_lessonDS);
        lesson_weeksOnly = mLayout.findViewById(R.id.timetable_edit_lessonWeeksOnly);
        lesson_beginTime = mLayout.findViewById(R.id.timetable_edit_startTime);
        lesson_endTime = mLayout.findViewById(R.id.timetable_edit_endTime);
        setTimeAndUpdateView(getMinutes(true), getMinutes(false));

        // DS-Spinner mit Daten füllen
        listOfDs[0] = getString(R.string.timetable_edit_lessonDS_value);
        for (int i = 1; i < count; i++) {
            listOfDs[i] = resources.getString(
                    R.string.timetable_ds_list,
                    i,
                    dateFormat.format(Const.Timetable.getDate(Const.Timetable.beginDS[i - 1])),
                    dateFormat.format(Const.Timetable.getDate(Const.Timetable.endDS[i - 1]))
            );
        }

        lesson_day.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, nameOfDays));
        lesson_day.setSelection(arguments.getInt(Const.BundleParams.TIMETABLE_DAY, 1) - 1);
        lesson_ds.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, listOfDs));
        lesson_ds.setSelection(arguments.getInt(Const.BundleParams.TIMETABLE_DS, 0));
    }

    /**
     * Erstellt InputListener für Elemente
     */
    private void createListener() {
        // Adapter für Auswahl erstellen
        lesson_ds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                mLayout.findViewById(R.id.timetable_edit_row_individualTime).setVisibility(i == 0 ? View.VISIBLE : View.GONE);
                if (i != 0) {
                    setTimeAndUpdateView(Const.Timetable.beginDS[i - 1], Const.Timetable.endDS[i - 1]);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });

        // OnClick-Listener für individuelle Zeit
        lesson_beginTime.setOnClickListener(view -> {
            final long minutes = getMinutes(true);
            final int hour = (int) minutes / 60;
            final int minute = (int) minutes % 60;

            new TimePickerDialog(
                    getActivity(),
                    (timePicker, hourOfDay, minute1) -> setTimeAndUpdateView((int) TimeUnit.MINUTES.convert(hourOfDay, TimeUnit.HOURS) + minute1, endTime), hour, minute, true
            ).show();
        });
        lesson_endTime.setOnClickListener(view -> {
            final long minutes = getMinutes(false);
            final int hour = (int) minutes / 60;
            final int minute = (int) minutes % 60;

            new TimePickerDialog(
                    getActivity(),
                    (timePicker, hourOfDay, minute12) -> setTimeAndUpdateView(startTime, (int) TimeUnit.MINUTES.convert(hourOfDay, TimeUnit.HOURS) + minute12), hour, minute, true
            ).show();
        });

        // OnClick-Listener für Auswahl der Wochen
        lesson_weeksOnly.setOnClickListener(view -> {
            // Liste von möglichen Kalenderwochen erstellen
            final int increment = lesson_week.getSelectedItemPosition() == 0 ? 1 : 2;
            final int startWeek = lesson_week.getSelectedItemPosition() == 2 ? 2 : 1;
            final String[] kws = new String[53 / increment];
            final boolean[] selectedKwsMinimal = new boolean[53 / increment];
            for (int kw = 0; kw < 53 / increment; kw++) {
                kws[kw] = view.getResources().getString(R.string.timetable_calendar_week, kw * increment + startWeek);
                selectedKwsMinimal[kw] = selectedKws[(startWeek - 1) + kw * increment];
            }

            new AlertDialog.Builder(requireContext())
                    .setMultiChoiceItems(kws, selectedKwsMinimal, (dialog, which, isChecked) -> {
                        selectedKws[(startWeek - 1) + which * increment] = isChecked;
                        // Auswahl in Textbox neu anzeigen
                        StringBuilder listOfWeeks = new StringBuilder();
                        for (int i = 0; i < selectedKws.length; i++) {
                            if (selectedKws[i]) {
                                listOfWeeks.append(i + 1).append("; ");
                            }
                        }
                        lesson_weeksOnly.setText(listOfWeeks.toString());
                    })
                    .setPositiveButton("OK", null)
                    .setTitle(R.string.timetable_edit_lessonWeeks_title)
                    .create()
                    .show();
        });

        // Speichern
        mLayout.findViewById(R.id.timetable_edit_lessonSave).setOnClickListener(view -> {
            final Activity activity = requireActivity();
            if (saveLesson()) {
                Toast.makeText(activity, R.string.timetable_edit_lessonSaveSuccess, Toast.LENGTH_SHORT).show();
                activity.finish();
            } else
                Snackbar.make(view, R.string.info_error_save, Snackbar.LENGTH_LONG).show();
        });
    }

    /**
     * Befüllt alle Formularfelder mit den Informationen aus der übergebene Lehrveranstaltung
     *
     * @param lesson ausgewählte Lehrveranstaltung
     */
    private void fillForms(@NonNull final LessonUser lesson) {
        lesson_name.setText(lesson.getName());
        lesson_tag.setText(lesson.getLessonTag());
        lesson_prof.setText(lesson.getProfessor());
        lesson_type.setSelection(TimetableHelper.getIntegerTypOfLesson(lesson));
        lesson_week.setSelection(lesson.getWeek());
        lesson_rooms.setText(TimetableHelper.getStringOfRooms(lesson));
        lesson_day.setSelection(lesson.getDay() - 1);

        // Lehrveranstaltung in ein Zeitraster einordnen
        final int count = listOfDs.length;
        for (int i = 0; i < count - 1; i++) {
            if (Const.Timetable.beginDS[i] == lesson.getBeginTime() && Const.Timetable.endDS[i] == lesson.getEndTime()) {
                lesson_ds.setSelection(i + 1);
                break;
            }
        }

        if (lesson_ds.getSelectedItemPosition() == 0) {
            setTimeAndUpdateView(lesson.getBeginTime(), lesson.getEndTime());
            mLayout.findViewById(R.id.timetable_edit_row_individualTime).setVisibility(View.VISIBLE);
        }

        // Auswahl der einzelnen Wochen
        final RealmList<LessonWeek> lessonWeeks = lesson.getWeeksOnly();
        for (final LessonWeek lessonWeek : lessonWeeks) {
            selectedKws[lessonWeek.getWeekOfYear() - 1] = true;
        }
        lesson_weeksOnly.setText(TimetableHelper.getStringOfKws(lesson));
    }

    /**
     * Liefert die Startzeit/Endzeit der aktuell stattfinden Lehrveranstaltung oder falls außerhalb der Zeiten die Zeit der ersten Lehrveranstaltung
     *
     * @param startTime True wenn Startzeit sonst Endzeit
     * @return Zeit in Minuten seit Mitternacht
     */
    private int getMinutes(final boolean startTime) {
        final int currentDS = TimetableHelper.getCurrentDS(TimetableHelper.getMinutesSinceMidnight(GregorianCalendar.getInstance(Locale.GERMANY))) - 1;

        if (currentDS >= 0) {
            return startTime ? Const.Timetable.beginDS[currentDS] : Const.Timetable.endDS[currentDS];
        } else {
            return startTime ? Const.Timetable.beginDS[0] : Const.Timetable.endDS[0];
        }
    }

    // Setzt im View die ausgewählte Zeit
    private void setTimeAndUpdateView(final int startTime, final int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        lesson_beginTime.setText(dateFormat.format(Const.Timetable.getDate(startTime)));
        lesson_endTime.setText(dateFormat.format(Const.Timetable.getDate(endTime)));
    }

    /**
     * Holt sich Daten aus dem View und speichert diese in die Datenbank
     *
     * @return true beim erfolgreichen speichern sonst false
     */
    private boolean saveLesson() {
        try {
            realm.beginTransaction();
            if (lesson == null) {
                lesson = realm.createObject(LessonUser.class, "user_" + UUID.randomUUID().toString());
                lesson.setCreatedByUser(true);
            }

            lesson.setName(lesson_name.getText().toString());
            lesson.setLessonTag(lesson_tag.getText().toString());
            lesson.setProfessor(lesson_prof.getText().toString());
            lesson.setType(lesson_type.getSelectedItem().toString());
            lesson.setWeek(lesson_week.getSelectedItemPosition());
            lesson.setDay(lesson_day.getSelectedItemPosition() + 1);
            lesson.setBeginTime(startTime);
            lesson.setEndTime(endTime);
            lesson.setEditedByUser(true);

            // Wenn keine Kurzform gesetzt ist, diese automatisch erzeugen
            if (lesson.getLessonTag() == null || lesson.getLessonTag().isEmpty()) {
                lesson.setLessonTag(lesson.getName().substring(0, Math.min(lesson.getName().length(), 5)));
            }

            // Liste ausgewählten Kalenderwochen erstellen
            final RealmList<LessonWeek> lessonWeeks = new RealmList<>();
            LessonWeek lessonWeek;
            for (int i = 0; i < 53; i++) {
                if (selectedKws[i]) {
                    lessonWeek = realm.where(LessonWeek.class).equalTo("weekOfYear", i + 1).findFirst();
                    if (lessonWeek == null) {
                        lessonWeek = realm.createObject(LessonWeek.class, i + 1);
                    }
                    lessonWeeks.add(lessonWeek);
                }
            }
            lesson.setWeeksOnly(lessonWeeks);

            // Liste von Räumen erstellen
            final String[] rooms = lesson_rooms.getText().toString().split(";");
            final RealmList<String> lessonRooms = lesson.getRooms();
            lessonRooms.deleteAllFromRealm();
            lessonRooms.addAll(Arrays.asList(rooms));

            realm.commitTransaction();
        } catch (final Exception e) {
            return false;
        }
        return true;
    }
}
