package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.ExamStats;

/**
 * Adapter zur Anzeige der Notenstatistik
 *
 * @author Kay Förster
 */
public class ExamStatsAdapter extends AbstractBaseAdapter<ExamStats> {
    private final String[] semesterNames;

    public ExamStatsAdapter(Context context, ArrayList<ExamStats> data) {
        super(context, data);
        semesterNames = context.getResources().getStringArray(R.array.semesterName);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

        ExamStats examStats = getItem(position);
        if (examStats.semester != null)
            viewHolder.semester.setText(Const.Semester.getSemesterName(semesterNames, examStats.semester));
        else viewHolder.semester.setText(R.string.exams_stats_study);
        viewHolder.average.setText(context.getString(R.string.exams_stats_average, String.format("%.2f", examStats.average)));
        viewHolder.countGrades.setText(context.getResources().getQuantityString(R.plurals.exams_stats_count_grade, examStats.gradeCount, examStats.gradeCount));
        viewHolder.countCredits.setText(context.getString(R.string.exams_stats_count_credits, examStats.credits));
        viewHolder.gradeBest.setText(context.getString(R.string.exams_stats_gradeBest, examStats.gradeBest));
        viewHolder.gradeWorst.setText(context.getString(R.string.exams_stats_gradeWorst, examStats.gradeWorst));

        return convertView;
    }


    static class ViewHolder {
        public TextView semester;
        public TextView average;
        public TextView countGrades;
        public TextView countCredits;
        public TextView gradeBest;
        public TextView gradeWorst;
    }
}
