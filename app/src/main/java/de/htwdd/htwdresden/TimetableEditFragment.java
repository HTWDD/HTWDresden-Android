package de.htwdd.htwdresden;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.events.UpdateTimetableEvent;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.Lesson2;
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
    private Lesson2 lesson = null;
    private long startTime;
    private long endTime;
    private boolean[] selectedKws = new boolean[53];

    public TimetableEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final Bundle bundle = getArguments();

        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_timetable_edit, container, false);
        realm = Realm.getDefaultInstance();

        // Titel ändern
        if (activity instanceof INavigation)
            ((INavigation) activity).setTitle(getResources().getString(R.string.timetable_edit_activity_titel));

        // Alten Zustand wiederherstellen
        if (savedInstanceState != null) {
            startTime = savedInstanceState.getLong("startTime", 0);
            endTime = savedInstanceState.getLong("endTime", 0);
            selectedKws = savedInstanceState.getBooleanArray("selectedKws");
        }

        // Views suchen und befüllen
        createLocalResources(bundle);

        createListener();

        // Soll eine Stunden bearbeitet werden
        if (bundle.getBoolean(Const.BundleParams.TIMETABLE_EDIT, false)) {
            // ...anhand einer Stunden-ID
            if (bundle.containsKey(Const.BundleParams.TIMETABLE_LESSON_ID)) {
                lesson = realm.where(Lesson2.class).endsWith(Const.database.Lesson.ID, bundle.getString(Const.BundleParams.TIMETABLE_LESSON_ID)).findFirst();
            }
            // ...oder der übergeben Stunde
            else {
                final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
                calendar.set(Calendar.DAY_OF_WEEK, bundle.getInt(Const.BundleParams.TIMETABLE_DAY, 0) + 1);
                calendar.set(Calendar.WEEK_OF_YEAR, bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, 1));
                lesson = TimetableHelper.getLessonsByDateAndDs(realm, calendar, true, bundle.getInt(Const.BundleParams.TIMETABLE_DS, 1)).first(null);
            }

            if (lesson != null) {
                // Formular mit Daten füllen
                fillForms(lesson);
            }
        }

        // Werte in View eintragen
        if (lesson != null) {

            Button buttonDelete = (Button) mLayout.findViewById(R.id.timetable_edit_LessonDelete);
            buttonDelete.setEnabled(true);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseManager databaseManager = new DatabaseManager(getActivity());
                    TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);
                    long result = timetableUserDAO.delete(String.valueOf(lesson.getId()));
                    if (result > 0) {
                        EventBus.getInstance().post(new UpdateTimetableEvent());
                        Toast.makeText(getActivity(), R.string.timetable_edit_lessonDeleteSuccess, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else
                        Snackbar.make(view, R.string.info_error_error, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        Button buttonSave = (Button) mLayout.findViewById(R.id.timetable_edit_lessonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveLesson()) {
                    EventBus.getInstance().post(new UpdateTimetableEvent());
                    Toast.makeText(getActivity(), R.string.timetable_edit_lessonSaveSuccess, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else
                    Snackbar.make(view, R.string.info_error_save, Snackbar.LENGTH_LONG).show();
            }
        });

        return mLayout;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startTime);
        outState.putLong("endTime", endTime);
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
        final Context context = getActivity();

        // Views finden
        lesson_name = (TextInputEditText) mLayout.findViewById(R.id.timetable_edit_lessonName);
        lesson_tag = (TextInputEditText) mLayout.findViewById(R.id.timetable_edit_lessonTag);
        lesson_prof = (TextInputEditText) mLayout.findViewById(R.id.timetable_edit_lessonProf);
        lesson_type = (Spinner) mLayout.findViewById(R.id.timetable_edit_lessonType);
        lesson_rooms = (TextInputEditText) mLayout.findViewById(R.id.timetable_edit_lessonRooms);
        lesson_week = (Spinner) mLayout.findViewById(R.id.timetable_edit_lessonWeek);
        lesson_day = (Spinner) mLayout.findViewById(R.id.timetable_edit_lessonDay);
        lesson_ds = (Spinner) mLayout.findViewById(R.id.timetable_edit_lessonDS);
        lesson_weeksOnly = (TextInputEditText) mLayout.findViewById(R.id.timetable_edit_lessonWeeksOnly);
        lesson_beginTime = (EditText) mLayout.findViewById(R.id.timetable_edit_startTime);
        lesson_endTime = (EditText) mLayout.findViewById(R.id.timetable_edit_endTime);
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
        lesson_beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final long minutes = getMinutes(true);
                final int hour = (int) minutes / 60;
                final int minute = (int) minutes % 60;

                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(final TimePicker timePicker, final int hourOfDay, final int minute) {
                        setTimeAndUpdateView(TimeUnit.MINUTES.convert(hourOfDay, TimeUnit.HOURS) + minute, endTime);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });
        lesson_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final long minutes = getMinutes(false);
                final int hour = (int) minutes / 60;
                final int minute = (int) minutes % 60;

                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(final TimePicker timePicker, final int hourOfDay, final int minute) {
                        setTimeAndUpdateView(startTime, TimeUnit.MINUTES.convert(hourOfDay, TimeUnit.HOURS) + minute);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        // OnClick-Listener für Auswahl der Wochen
        lesson_weeksOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Liste von möglichen Kalenderwochen erstellen
                final int increment = lesson_week.getSelectedItemPosition() == 0 ? 1 : 2;
                final int startWeek = lesson_week.getSelectedItemPosition() == 2 ? 2 : 1;
                final String[] kws = new String[53 / increment];
                final boolean[] selectedKwsMinimal = new boolean[53 / increment];
                for (int kw = 0; kw < 53 / increment; kw++) {
                    kws[kw] = view.getResources().getString(R.string.timetable_calendar_week, kw * increment + startWeek);
                    selectedKwsMinimal[kw] = selectedKws[kw * increment];
                }

                new AlertDialog.Builder(getActivity())
                        .setMultiChoiceItems(kws, selectedKwsMinimal, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                selectedKws[which * increment] = isChecked;
                                // Auswahl in Textbox neu anzeigen
                                String listOfWeeks = "";
                                for (int i = 0; i < selectedKws.length; i++) {
                                    if (selectedKws[i]) {
                                        listOfWeeks += i + 1 + "; ";
                                    }
                                }
                                lesson_weeksOnly.setText(listOfWeeks);
                            }
                        })
                        .setPositiveButton("OK", null)
                        .setTitle(R.string.timetable_edit_lessonWeeks_title)
                        .create()
                        .show();
            }
        });

    }

    /**
     * Befüllt alle Formularfelder mit den Informationen aus der übergebene Lehrveranstaltung
     *
     * @param lesson ausgewählte Lehrveranstaltung
     */
    private void fillForms(@NonNull final Lesson2 lesson) {
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
    private long getMinutes(final boolean startTime) {
        final int currentDS = TimetableHelper.getCurrentDS(TimetableHelper.getMinutesSinceMidnight(GregorianCalendar.getInstance(Locale.GERMANY))) - 1;

        if (currentDS >= 0) {
            return startTime ? Const.Timetable.beginDS[currentDS] : Const.Timetable.endDS[currentDS];
        } else {
            return startTime ? Const.Timetable.beginDS[0] : Const.Timetable.endDS[0];
        }
    }

    // Setzt im View die ausgewählte Zeit
    private void setTimeAndUpdateView(final long startTime, final long endTime) {
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
//        Lesson lesson = new Lesson();
//        lesson.setName(lesson_name.getText().toString());
//        lesson.setTag(lesson_tag.getText().toString());
//        lesson.setProfessor(lesson_prof.getText().toString());
//        lesson.setTypeInt(lesson_type.getSelectedItemPosition());
//        lesson.setRooms(lesson_rooms.getText().toString());
//        lesson.setWeek(lesson_week.getSelectedItemPosition());
//        lesson.setDay(lesson_day.getSelectedItemPosition() + 1);
//        lesson.setDs(lesson_ds.getSelectedItemPosition() + 1);
//        lesson.setWeeksOnly(lesson_weeksOnly.getText().toString());
//
//        /**
//         * Wenn keine Kurzform gesetzt ist, diese automatisch erzeugen
//         */
//        if (lesson.getTag().isEmpty()) {
//            lesson.setTag(lesson.getName().substring(0, Math.min(lesson.getName().length(), 5)));
//        }
//
//        DatabaseManager databaseManager = new DatabaseManager(getActivity());
//        TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);
//
//        // Stunde speichern
//        if (this.lesson == null)
//            return timetableUserDAO.save(lesson) != -1;
//        else {
//            lesson.setId(this.lesson.getId());
//            return timetableUserDAO.update(lesson) > 0;
//        }
        return true;
    }
}
