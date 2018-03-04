package de.htwdd.htwdresden.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Stack;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.API.IExamResultsService;
import de.htwdd.htwdresden.classes.API.Retrofit2Qis;
import de.htwdd.htwdresden.types.ExamResult;
import de.htwdd.htwdresden.types.exams.Course;
import io.realm.Realm;
import okhttp3.Credentials;
import retrofit2.Call;

/**
 * Service zum Aktualisieren der PrüfungsergebnisNse
 *
 * @author Kay Förster
 */
public class ExamSyncService extends AbstractSyncHelper {
    public final static String INTENT_SYNC_EXAMS = "de.htwdd.htwdresden.exams";
    private final static String LOG_TAG = "ExamSyncService";
    private final Stack<ExamResult> results = new Stack<>();
    protected String credentials;
    protected IExamResultsService examResultsService;

    public ExamSyncService() {
        super("ExamSyncService", INTENT_SYNC_EXAMS);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        credentials = Credentials.basic("s" + sharedPreferences.getString("sNummer", ""), sharedPreferences.getString("RZLogin", ""));
        examResultsService = Retrofit2Qis.getInstance(context).getRetrofit().create(IExamResultsService.class);

        // Alle Noten laden
        getGradeResults();
        // Auf fertigstellung warten
        waitForFinish();
        // Ergebnisse speichern
        if (!isCancel()) {
            final boolean result = saveGrades();
            if (result && broadcastNotifier != null) {
                broadcastNotifier.notifyStatus(0);
            }
        }
    }

    /**
     * Fordert alle Noten eines Studenten vom Webservice an
     */
    void getGradeResults() {
        final Call<List<Course>> courses = examResultsService.getCourses(credentials);
        courses.enqueue(new GenericCallback<List<Course>>() {
            @Override
            void onSuccess(final List<Course> response) {
                // Download der Noten je Studiengang
                for (final Course course : response) {
                    getGrades(course.getAbschlNr(), course.getStgNr(), course.getPOVersion());
                }
                queueCount.decrementCountQueue();
            }
        });
        queueCount.incrementCountQueue();
    }

    /**
     * Lädt Noten vom Webservice herunter und speichert des Response in {@link #results}
     * @param abschlussNummer    interne Nummer des Abschlusses
     * @param studiengangsnummer interne Nummer des Studienganges
     * @param poVersion          Version der Prüfungsordnung
     */
    private void getGrades(@NonNull final String abschlussNummer, @NonNull final String studiengangsnummer, final int poVersion) {
        final Call<List<ExamResult>> grades = examResultsService.getGrades(credentials, abschlussNummer, studiengangsnummer, String.valueOf(poVersion));
        grades.enqueue(new GenericCallback<List<ExamResult>>() {
            @Override
            void onSuccess(final List<ExamResult> response) {
                results.addAll(response);
                queueCount.decrementCountQueue();
            }
        });
        queueCount.incrementCountQueue();
    }

    /**
     * Speichert alle Noten aus {@link #results} in die Datenbank
     *
     * @return true wenn Speichern erfolgreich
     */
    boolean saveGrades() {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try {
            realm.delete(ExamResult.class);
            realm.copyToRealmOrUpdate(results);
            realm.commitTransaction();
        } catch (final Exception e) {
            Log.e(LOG_TAG, "[Fehler] Beim Speichern der Noten", e);
            realm.cancelTransaction();
            setError(context.getString(R.string.info_error_save), -1);
            return false;
        } finally {
            realm.close();
        }
        return true;
    }
}
