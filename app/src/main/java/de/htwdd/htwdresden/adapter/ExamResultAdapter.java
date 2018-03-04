package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.types.exams.ExamResult;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Adapter für die Übersichtsseite der Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public class ExamResultAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final LayoutInflater mLayoutInflater;
    private final Realm realm;
    private final RealmResults<ExamResult> examHeaders;

    public ExamResultAdapter(@NonNull final Context context, final Realm realm) {
        super();
        this.realm = realm;
        this.context = context;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.examHeaders = realm.where(ExamResult.class).distinctValues(Const.database.ExamResults.SEMESTER).sort(Const.database.ExamResults.SEMESTER, Sort.DESCENDING).findAll();
    }

    @Override
    public int getGroupCount() {
        return examHeaders.size();
    }

    @Override
    public int getChildrenCount(final int i) {
        if (examHeaders.size() < i) {
            return 0;
        }

        final ExamResult examResult = examHeaders.get(i);
        return examResult != null ? (int) getChildren(examResult.semester).count() : 0;
    }

    @Override
    public ExamResult getGroup(final int i) {
        return examHeaders.get(i);
    }

    @Override
    @Nullable
    public ExamResult getChild(final int i, final int i1) {
        if (examHeaders.size() < i) {
            return null;
        }
        final ExamResult examResult = examHeaders.get(i);
        if (examResult != null) {
            final RealmQuery<ExamResult> examResults = getChildren(examResult.semester);
            if (examResults.count() >= i1) {
                return examResults.findAll().get(i1);
            }

        }
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, final boolean b, @Nullable View view, final ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.fragment_exams_result_group, viewGroup, false);
            view.setTag(viewHolder);
            viewHolder.textView1 = view.findViewById(R.id.listHeader);
        } else viewHolder = (ViewHolder) view.getTag();

        viewHolder.textView1.setText(ExamsHelper.getSemesterName(view.getResources(), getGroup(i).semester));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, final ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.fragment_exams_result_item, viewGroup, false);
            view.setTag(viewHolder);
            viewHolder.textView1 = view.findViewById(R.id.modulName);
            viewHolder.textView2 = view.findViewById(R.id.note);
            viewHolder.textView3 = view.findViewById(R.id.grade);
            viewHolder.textView4 = view.findViewById(R.id.credits);
        } else viewHolder = (ViewHolder) view.getTag();

        final ExamResult examResult = getChild(i, i1);
        if (examResult == null) {
            return view;
        }

        // Modulnamen setzen
        viewHolder.textView1.setText(examResult.text);
        viewHolder.textView1.setTypeface(null, Typeface.NORMAL);
        viewHolder.textView1.setTextColor(Color.BLACK);

        // Genauen Status setzen
        switch (examResult.state != null ? examResult.state : "") {
            case "AN":
                viewHolder.textView1.setTypeface(null, Typeface.ITALIC);
                viewHolder.textView1.setTextColor(Color.DKGRAY);
                break;
            // Student hat bestanden
            case "BE":
                if (examResult.credits == 0.0f) {
                    viewHolder.textView1.setTextColor(Color.BLACK);
                } else {
                    viewHolder.textView1.setTextColor(ContextCompat.getColor(context, R.color.exam_results_green));
                }
                break;
            // Student hat NICHT bestanden
            case "NB":
            case "EN":
                viewHolder.textView1.setTextColor(Color.RED);
                break;
        }

        // Vermerk setzten
        switch (examResult.note != null ? examResult.note : "") {
            // Leistung wurde anerkannt
            case "a":
                viewHolder.textView2.setText(R.string.exams_result_note_recognized);
                break;
            // Student hat sich abgemeldet
            case "e":
                viewHolder.textView2.setText(R.string.exams_result_note_sign_off);
                break;
            // Student ist gesperrt
            case "g":
                viewHolder.textView2.setText(R.string.exams_result_note_blocked);
                break;
            // Student war krank
            case "k":
                viewHolder.textView2.setText(R.string.exams_result_note_ill);
                break;
            // Student wurde nicht zugelassen
            case "nz":
                viewHolder.textView2.setText(R.string.exams_result_note_not_allowed);
                break;
            case "5ue":
                viewHolder.textView2.setText(R.string.exams_result_note_unexcused_missing);
                break;
            case "5na":
                viewHolder.textView2.setText(R.string.exams_result_note_not_started);
                break;
            case "kA":
                viewHolder.textView2.setText(R.string.exams_result_note_no_retest);
                break;
            case "PFV":
                viewHolder.textView2.setText(R.string.exams_result_note_free_try);
                break;
            case "mE":
                viewHolder.textView2.setText(R.string.exams_result_note_with_success);
                break;
            case "N":
                viewHolder.textView2.setText(R.string.exams_result_note_failed);
                break;
            case "VPo":
                viewHolder.textView2.setText(R.string.exams_result_note_pre_placement);
                break;
            case "f":
                viewHolder.textView2.setText(R.string.exams_result_note_voluntary_appointment);
                break;
            case "uV":
                viewHolder.textView2.setText(R.string.exams_result_note_conditional);
                break;
            case "TA":
                viewHolder.textView2.setText(R.string.exams_result_note_attempt);
                break;
            default:
                viewHolder.textView2.setText("");
                break;
        }

        // Note anzeigen
        final Float note = examResult.getGrade();
        viewHolder.textView3.setText(note != 0.0 ? Float.toString(note) : "");
        // Credits anzeigen
        viewHolder.textView4.setText(context.getString(R.string.exams_result_credits, examResult.credits));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private RealmQuery<ExamResult> getChildren(@NonNull final Integer semester) {
        return realm.where(ExamResult.class).equalTo(Const.database.ExamResults.SEMESTER, semester);
    }

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
    }
}
