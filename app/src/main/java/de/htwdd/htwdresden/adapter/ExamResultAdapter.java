package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.ExamResult;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Adapter für die Notenübersichtsseite
 *
 * @author Kay Förster
 */
public class ExamResultAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final LayoutInflater mLayoutInflater;
    private final String[] semesterNames;
    private final Realm realm = Realm.getDefaultInstance();
    private final RealmResults<ExamResult> examHeaders = realm.where(ExamResult.class).distinct(Const.database.ExamResults.SEMESTER).sort(Const.database.ExamResults.SEMESTER, Sort.DESCENDING);

    public ExamResultAdapter(@NonNull final Context context) {
        super();
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        semesterNames = context.getResources().getStringArray(R.array.semesterName);
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return examHeaders.size();
    }

    @Override
    public int getChildrenCount(final int i) {
        if (examHeaders.size() < i) {
            Log.d("Adapter", "Kinder: immer 0");
            return 0;
        }
        return (int) getChildren(examHeaders.get(i).Semester).count();
    }

    @Override
    public ExamResult getGroup(final int i) {
        return examHeaders.get(i);
    }

    @Override
    public ExamResult getChild(int i, int i1) {
        if (examHeaders.size() < i)
            return null;
        final RealmQuery<ExamResult> examResults = getChildren(examHeaders.get(i).Semester);
        if (examResults.count() < i1)
            return null;
        return examResults.findAll().get(i1);
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
            viewHolder.textView1 = (TextView) view.findViewById(R.id.listHeader);
        } else viewHolder = (ViewHolder) view.getTag();

        viewHolder.textView1.setText(Const.Semester.getSemesterName(semesterNames, getGroup(i).Semester));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, final ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.fragment_exams_result_item, viewGroup, false);
            view.setTag(viewHolder);
            viewHolder.textView1 = (TextView) view.findViewById(R.id.modulName);
            viewHolder.textView2 = (TextView) view.findViewById(R.id.vermerk);
            viewHolder.textView3 = (TextView) view.findViewById(R.id.note);
            viewHolder.textView4 = (TextView) view.findViewById(R.id.credits);
        } else viewHolder = (ViewHolder) view.getTag();

        final ExamResult examResult = getChild(i, i1);
        final Float note = examResult.PrNote;

        // Modulnamen setzen
        viewHolder.textView1.setText(examResult.PrTxt);
        viewHolder.textView1.setTypeface(null, Typeface.NORMAL);
        viewHolder.textView1.setTextColor(Color.BLACK);

        // Vermerk leeren
        viewHolder.textView2.setText("");

        // Genauen Status setzen
        switch (examResult.Status) {
            case "AN":
                viewHolder.textView1.setTypeface(null, Typeface.ITALIC);
                viewHolder.textView1.setTextColor(Color.GRAY);
                switch (examResult.Vermerk) {
                    // Student hat sich abgemeldet
                    case "e":
                        viewHolder.textView2.setText(R.string.exams_result_sign_off);
                        break;
                    // Student war krank
                    case "k":
                        viewHolder.textView2.setText(R.string.exams_result_ill);
                        break;
                    // Student wurde nicht zugelassen
                    case "nz":
                        viewHolder.textView2.setText(R.string.exams_result_not_allowed);
                        break;
                }
                break;
            // Student hat bestanden
            case "BE":
                if (examResult.EctsCredits == null || examResult.EctsCredits == 0.0f) {
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

        // Note anzeigen
        if (note != null) {
            viewHolder.textView3.setText(context.getString(R.string.exams_result_grade, note / 100));
        } else viewHolder.textView3.setText(R.string.exams_result_grade_empty);
        // Credits anzeigen
        viewHolder.textView4.setText(context.getString(R.string.exams_result_credits, examResult.EctsCredits));

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
