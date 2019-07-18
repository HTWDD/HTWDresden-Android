package de.htwdd.htwdresden;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.classes.API.IGeneralService;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import de.htwdd.htwdresden.classes.ConnectionHelper;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.types.canteen.Canteen;
import de.htwdd.htwdresden.types.canteen.Meal;
import de.htwdd.htwdresden.types.semsterPlan.Semester;
import de.htwdd.htwdresden.types.semsterPlan.TimePeriod;
import de.htwdd.htwdresden.types.studyGroups.StudyCourse;
import de.htwdd.htwdresden.types.studyGroups.StudyGroup;
import de.htwdd.htwdresden.types.studyGroups.StudyYear;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 */
class CheckUpdates implements Runnable {
    private final static String LOG_TAG = "CheckUpdateTask";
    private final Context context;

    CheckUpdates(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        // Überprüfe Internetverbindung
        if (ConnectionHelper.checkNoInternetConnection(context)) {
            return;
        }

        // Einstellungen holen
        final Realm realm = Realm.getDefaultInstance();
        final Calendar calendar = GregorianCalendar.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final long mensaDayLastUpdate = sharedPreferences.getLong(Const.preferencesKey.PREFERENCES_MENSA_DAY_LASTUPDATE, 0);
        final long studyGroupsLastUpdate = sharedPreferences.getLong(Const.preferencesKey.PREFERENCES_STUDY_GROUP_LAST_UPDATE, 0);
        final long semesterplanLastUpdate = sharedPreferences.getLong(Const.preferencesKey.PREFERENCES_SEMESTERPLAN_UPDATETIME, 0);
        final IGeneralService iGeneralService = Retrofit2Rubu.getInstance(context).getRetrofit().create(IGeneralService.class);

        // Aktualisiere Meal für Overview
        if ((calendar.getTimeInMillis() - mensaDayLastUpdate) > TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS) || realm.where(Meal.class).count() == 0 || realm.where(Canteen.class).count() == 0) {
            Log.d(LOG_TAG, "Lade Mensa");

            final MensaHelper mensaHelper = new MensaHelper(context, (short) 80);

            mensaHelper.updateDayMeals(() -> {
                    },
                    () -> {
                        Log.i(LOG_TAG, "Mahlzeiten des Tages aktualisiert");
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong(Const.preferencesKey.PREFERENCES_MENSA_DAY_LASTUPDATE, calendar.getTimeInMillis());
                        editor.apply();
                    });
        }

        // Aktualisiere Studiengruppen
        if ((calendar.getTimeInMillis() - studyGroupsLastUpdate) > TimeUnit.MILLISECONDS.convert(14, TimeUnit.DAYS) || realm.where(StudyYear.class).count() == 0) {
            final Call<List<StudyYear>> studyGroups = iGeneralService.getStudyGroups();
            studyGroups.enqueue(new GenericCallback<List<StudyYear>>() {
                @Override
                void onSuccess(final List<StudyYear> response) {
                    final Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.delete(StudyGroup.class);
                    realm.delete(StudyCourse.class);
                    realm.delete(StudyYear.class);
                    realm.copyToRealmOrUpdate(response);
                    realm.commitTransaction();
                    realm.close();

                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putLong(Const.preferencesKey.PREFERENCES_STUDY_GROUP_LAST_UPDATE, Calendar.getInstance().getTimeInMillis())
                            .apply();
                    Log.i(LOG_TAG, "Studiengruppen aktualisiert");
                }

            });
        }

        // Aktualisiere Semesterplan
        if ((calendar.getTimeInMillis() - semesterplanLastUpdate) > TimeUnit.MILLISECONDS.convert(14, TimeUnit.DAYS) || realm.where(Semester.class).count() == 0) {
            final Call<List<Semester>> semester = iGeneralService.getSemesterplan();
            semester.enqueue(new GenericCallback<List<Semester>>() {
                @Override
                void onSuccess(final List<Semester> response) {
                    final Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.delete(TimePeriod.class);
                    realm.delete(Semester.class);
                    realm.copyToRealmOrUpdate(response);
                    realm.commitTransaction();
                    realm.close();

                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putLong(Const.preferencesKey.PREFERENCES_SEMESTERPLAN_UPDATETIME,
                                    Calendar
                                    .getInstance()
                                    .getTimeInMillis())
                            .apply();
                    Log.i(LOG_TAG, "Semesterplan aktualisiert");
                }
            });
        }
        realm.close();
    }

    abstract static class GenericCallback<T> implements Callback<T> {
        @Override
        public void onResponse(@NonNull final Call<T> call, @NonNull final Response<T> response) {
            if (response.isSuccessful()) {
                if (response.code() != 304) {
                    onSuccess(response.body());
                } else {
                    Log.i(LOG_TAG, "Keine neuen Daten verfügbar." + response);
                }
            } else {
                Log.i(LOG_TAG, "Fehler beim Ausführen des Requests. Code: " + response);
            }
        }

        @Override
        public void onFailure(@NonNull final Call<T> call, @NonNull final Throwable t) {
            Log.i(LOG_TAG, "Fehler beim Ausführen des Requests ", t);
        }

        abstract void onSuccess(T response);
    }

}
