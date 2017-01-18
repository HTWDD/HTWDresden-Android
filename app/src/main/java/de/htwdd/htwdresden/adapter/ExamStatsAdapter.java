package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.types.ExamStats;
import io.realm.Realm;

/**
 * Adapter zur Anzeige der Notenstatistik
 *
 * @author Kay Förster
 */
public class ExamStatsAdapter extends AbstractBaseAdapter<ExamStats> {
    private final String[] semesterNames;

    public ExamStatsAdapter(@NonNull final Context context) {
        super(context, ExamsHelper.getExamStats());
        data.add(0, ExamsHelper.getExamStatsForSemester(Realm.getDefaultInstance(), null));
        semesterNames = context.getResources().getStringArray(R.array.semesterName);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        // ViewHolder
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.exam_stats_item, parent, false);
            convertView.setTag(viewHolder);
            viewHolder.semester = (TextView) convertView.findViewById(R.id.stats_semester);
            viewHolder.average = (TextView) convertView.findViewById(R.id.stats_average);
            viewHolder.countGrades = (TextView) convertView.findViewById(R.id.stats_countGrade);
            viewHolder.countCredits = (TextView) convertView.findViewById(R.id.stats_countCredits);
            viewHolder.gradeBest = (TextView) convertView.findViewById(R.id.stats_gradeBest);
            viewHolder.gradeWorst = (TextView) convertView.findViewById(R.id.stats_gradeWorst);
        } else viewHolder = (ViewHolder) convertView.getTag();

        final ExamStats examStats = getItem(position);
        if (examStats.semester != null)
            viewHolder.semester.setText(Const.Semester.getSemesterName(semesterNames, examStats.semester));
        else viewHolder.semester.setText(R.string.exams_stats_study);
        viewHolder.average.setText(context.getString(R.string.exams_stats_average, String.format("%.2f", examStats.getAverage())));

        viewHolder.countGrades.setText(context.getResources().getQuantityString(R.plurals.exams_stats_count_grade, (int) examStats.gradeCount, (int) examStats.gradeCount));
        viewHolder.countCredits.setText(context.getString(R.string.exams_stats_count_credits, examStats.getCredits()));
        viewHolder.gradeBest.setText(context.getString(R.string.exams_stats_gradeBest, examStats.getGradeBest()));
        viewHolder.gradeWorst.setText(context.getString(R.string.exams_stats_gradeWorst, examStats.getGradeWorst()));

        return convertView;
    }

    private static class ViewHolder {
        TextView semester;
        TextView average;
        TextView countGrades;
        TextView countCredits;
        TextView gradeBest;
        TextView gradeWorst;
    }

    @Override
    public void notifyDataSetChanged() {
        this.data.clear();
        this.data.add(ExamsHelper.getExamStatsForSemester(Realm.getDefaultInstance(), null));
        this.data.addAll(ExamsHelper.getExamStats());
        super.notifyDataSetChanged();
    }
}
