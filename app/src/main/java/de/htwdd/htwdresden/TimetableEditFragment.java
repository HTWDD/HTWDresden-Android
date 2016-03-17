package de.htwdd.htwdresden;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.events.UpdateTimetableEvent;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.Lesson;


public class TimetableEditFragment extends Fragment {
    private static final String[] nameOfDays = Arrays.copyOfRange(DateFormatSymbols.getInstance().getWeekdays(), 2, 8);
    private static final String[] listOfDs = new String[Const.Timetable.beginDS.length];
    private EditText lesson_name;
    private EditText lesson_tag;
    private EditText lesson_prof;
    private Spinner lesson_type;
    private EditText lesson_rooms;
    private Spinner lesson_week;
    private Spinner lesson_day;
    private Spinner lesson_ds;
    private EditText lesson_weeksOnly;
    private Lesson lesson = null;

    public TimetableEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // DS-Spinner mit Daten füllen
        int count = listOfDs.length;
        Resources resources = getResources();
        for (int i = 0; i < count; i++)
            listOfDs[i] = resources.getString(R.string.timetable_ds_list, i + 1, format.format(Const.Timetable.beginDS[i]), format.format(Const.Timetable.endDS[i]));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int week;
        int day = 1;
        int ds = 1;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable_edit, container, false);

        // Ändere Titel
        if (getActivity() instanceof INavigation)
            ((INavigation) getActivity()).setTitle(getResources().getString(R.string.timetable_edit_activity_titel));

        // Views finden;
        lesson_name = (EditText) view.findViewById(R.id.timetable_edit_lessonName);
        lesson_tag = (EditText) view.findViewById(R.id.timetable_edit_lessonTag);
        lesson_prof = (EditText) view.findViewById(R.id.timetable_edit_lessonProf);
        lesson_type = (Spinner) view.findViewById(R.id.timetable_edit_lessonType);
        lesson_rooms = (EditText) view.findViewById(R.id.timetable_edit_lessonRooms);
        lesson_week = (Spinner) view.findViewById(R.id.timetable_edit_lessonWeek);
        lesson_day = (Spinner) view.findViewById(R.id.timetable_edit_lessonDay);
        lesson_ds = (Spinner) view.findViewById(R.id.timetable_edit_lessonDS);
        lesson_weeksOnly = (EditText) view.findViewById(R.id.timetable_edit_lessonWeeksOnly);

        // Wurden Informationen übergeben?
        Bundle bundle = getArguments();
        if (bundle != null) {
            week = bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, 1);
            day = bundle.getInt(Const.BundleParams.TIMETABLE_DAY, 1);
            ds = bundle.getInt(Const.BundleParams.TIMETABLE_DS, 1);

            // Soll eine Stunden bearbeitet werden
            if (bundle.getBoolean(Const.BundleParams.TIMETABLE_EDIT, false)) {
                DatabaseManager databaseManager = new DatabaseManager(getActivity());
                TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);

                // anhand einer Stunden-ID oder der übergeben Stunde
                if (bundle.containsKey(Const.BundleParams.TIMETABLE_LESSON_ID)) {
                    lesson = timetableUserDAO.getByID(bundle.getLong(Const.BundleParams.TIMETABLE_LESSON_ID));
                    if (lesson != null) {
                        day = lesson.getDay();
                        ds = lesson.getDs();
                    }
                } else {
                    ArrayList<Lesson> lessons = timetableUserDAO.getByDS(week, day, ds);
                    if (lessons.size() > 0)
                        lesson = lessons.get(0);
                }
            }
        }

        // Adapter für Daten erstellen und setzen
        ArrayAdapter adapterDays = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, nameOfDays);
        ArrayAdapter adapterDs = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listOfDs);

        lesson_day.setAdapter(adapterDays);
        lesson_ds.setAdapter(adapterDs);

        // Standardwerte setzen
        lesson_day.setSelection(day - 1);
        lesson_ds.setSelection(ds - 1);

        // Werte in View eintragen
        if (lesson != null) {
            lesson_name.setText(lesson.getName());
            lesson_tag.setText(lesson.getTag());
            lesson_prof.setText(lesson.getProfessor());
            lesson_type.setSelection(lesson.getTypeInt());
            lesson_rooms.setText(lesson.getRooms());
            lesson_week.setSelection(lesson.getWeek());
            lesson_weeksOnly.setText(lesson.getWeeksOnly());

            Button buttonDelete = (Button) view.findViewById(R.id.timetable_edit_LessonDelete);
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

        Button buttonSave = (Button) view.findViewById(R.id.timetable_edit_lessonSave);
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

        return view;
    }

    /**
     * Holt sich Daten aus dem View und speichert diese in die Datenbank
     *
     * @return true beim erfolgreichen speichern sonst false
     */
    private boolean saveLesson() {
        Lesson lesson = new Lesson();
        lesson.setName(lesson_name.getText().toString());
        lesson.setTag(lesson_tag.getText().toString());
        lesson.setProfessor(lesson_prof.getText().toString());
        lesson.setTypeInt(lesson_type.getSelectedItemPosition());
        lesson.setRooms(lesson_rooms.getText().toString());
        lesson.setWeek(lesson_week.getSelectedItemPosition());
        lesson.setDay(lesson_day.getSelectedItemPosition() + 1);
        lesson.setDs(lesson_ds.getSelectedItemPosition() + 1);
        lesson.setWeeksOnly(lesson_weeksOnly.getText().toString());

        /**
         * Wenn keine Kurzform gesetzt ist, diese automatisch erzeugen
         */
        if (lesson.getTag().isEmpty()) {
            lesson.setTag(lesson.getName().substring(0, Math.min(lesson.getName().length(), 5)));
        }

        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);

        // Stunde speichern
        if (this.lesson == null)
            return timetableUserDAO.save(lesson) != -1;
        else {
            lesson.setId(this.lesson.getId());
            return timetableUserDAO.update(lesson) > 0;
        }
    }
}
