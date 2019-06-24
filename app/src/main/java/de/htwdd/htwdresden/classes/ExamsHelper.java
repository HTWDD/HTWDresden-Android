package de.htwdd.htwdresden.classes;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.exams.ExamResult;
import de.htwdd.htwdresden.types.exams.ExamStats;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Hilfsklasse für Noten
 */
public final class ExamsHelper {

    public static boolean checkPreferences(@NonNull final Context context) {


        int lengthName = 0;
        int lengthPasswd = 0;

        try {
            Account account = AccountManager.get(context).getAccounts()[0];

            lengthName = account.name.length();
            lengthPasswd = AccountManager.get(context).getUserData(account, "RZLogin").length();
        }
        catch (Exception e){
            Toast.makeText(context, context.getString(R.string.error_loading_grades) + context.getString(R.string.no_account), Toast.LENGTH_SHORT).show();
        }


        return !(lengthName < 5 || lengthPasswd < 3);
    }

    /**
     * Erstellt eine Noten-Statistik für jedes Semester
     *
     * @return Liste der Noten-Statistik je Semester
     */
    public static ArrayList<ExamStats> getExamStats() {
        final Realm realm = Realm.getDefaultInstance();
        final ArrayList<ExamStats> stats = new ArrayList<>();
        final RealmResults<ExamResult> examHeaders = realm.where(ExamResult.class).distinct(Const.database.ExamResults.SEMESTER).sort(Const.database.ExamResults.SEMESTER, Sort.DESCENDING).findAll();

        for (final ExamResult result : examHeaders) {
            stats.add(getExamStatsForSemester(realm, result.semester));
        }
        realm.close();
        return stats;
    }

    /**
     * Erstellt die Noten-Statistik über das Semester. Wenn kein Semester angegeben wird, wird das ganze Studium berücksichtigt.
     *
     * @param realm    aktuelle Datenbankverbindung
     * @param semester Semester für welches die Statistik erstellt wird
     * @return {@link ExamStats} Objekt welches die Statistik enthält
     */
    public static ExamStats getExamStatsForSemester(@NonNull final Realm realm, @Nullable final Integer semester) {
        // Rückgabe-Objekt erstellen
        final ExamStats stats = new ExamStats();
        stats.semester = semester;

        // Datenbankabfrage
        final RealmQuery<ExamResult> realmQuery = realm.where(ExamResult.class)
                .isNotNull(Const.database.ExamResults.GRADE)
                .notEqualTo(Const.database.ExamResults.GRADE, 0f);
        // Wenn ein Semester angegeben, die Abfrage auf dieses einschränken
        if (semester != null) {
            realmQuery.equalTo(Const.database.ExamResults.SEMESTER, semester);
        }
        // Wenn keine Noten vorhanden sind, leeres Objekt zurückgeben, um Fehler beim ermitteln der St
        if (realmQuery.count() == 0) {
            return stats;
        }

        final float credits = realmQuery.sum(Const.database.ExamResults.CREDITS).floatValue();
        final Number bestGrade = realmQuery.min(Const.database.ExamResults.GRADE);
        final Number worstGrade = realmQuery.max(Const.database.ExamResults.GRADE);
        if (bestGrade != null) {
            stats.setGradeBest(bestGrade.floatValue());
        }
        if (worstGrade != null) {
            stats.setGradeWorst(worstGrade.floatValue());
        }
        stats.gradeCount = realmQuery.count();
        stats.setCredits(credits);
        // Berechne Durchschnitt
        if (credits > 0) {
            final RealmResults<ExamResult> noten = realmQuery.notEqualTo(Const.database.ExamResults.CREDITS, 0f).isNotNull(Const.database.ExamResults.GRADE).findAll();
            float average = 0f;
            for (final ExamResult examResult : noten) {
                if (examResult.grade != null)
                    average += examResult.grade * examResult.credits;
            }
            average /= credits;
            stats.setAverage(average);
        } else {
            stats.setAverage(realmQuery.average(Const.database.ExamResults.GRADE));
        }

        return stats;
    }

    /**
     * Wandelt den ausgewählten String aus {@see SharedPreferences} in Millisekunden für den Update Service um
     *
     * @param intervalFromPreference String aus {@see SharedPreferences} mit Key {@see de.htwdd.htwdresden.classes.Const.preferencesKey.REFERENCES_AUTO_EXAM_UPDATE}
     * @return UpdateInterval in Millisekunden
     */
    public static long getUpdateInterval(@NonNull final String intervalFromPreference) {
        final int parsedValue = Integer.valueOf(intervalFromPreference);
        return TimeUnit.MILLISECONDS.convert(parsedValue, TimeUnit.HOURS);
    }

    /**
     * Liefert die Semesterbezeichnung
     *
     * @param resources App-Ressourcen
     * @param semester  Semesterkennzeichnung als Zahl
     * @return Semesterbezeichnung als String
     */
    public static String getSemesterName(final @NonNull Resources resources, final @NonNull Integer semester) {
        int semesterCalc = semester - 20000;
        if (semesterCalc % 2 == 1)
            return resources.getString(R.string.academic_year_summer) + " " + semesterCalc / 10;
        else
            return resources.getString(R.string.academic_year_winter) + " " + semesterCalc / 10 + " / " + ((semesterCalc / 10) + 1);
    }
}
