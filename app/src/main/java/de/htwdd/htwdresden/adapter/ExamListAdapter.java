package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.Exam;

/**
 * Adapter zur Anzeige von Prüfungen
 *
 * @author Kay Förster
 */
public class ExamListAdapter extends AbstractBaseAdapter<Exam> {

    public ExamListAdapter(Context context, ArrayList<Exam> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        // ViewHolder
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.exam_list_item, parent, false);
            convertView.setTag(viewHolder);

            viewHolder.exam_title = (TextView) convertView.findViewById(R.id.exam_title);
            viewHolder.exam_type = (TextView) convertView.findViewById(R.id.exam_type);
            viewHolder.exam_branch = (TextView) convertView.findViewById(R.id.exam_studyBranch);
            viewHolder.exam_day = (TextView) convertView.findViewById(R.id.exam_day);
            viewHolder.exam_time = (TextView) convertView.findViewById(R.id.exam_time);
            viewHolder.exam_room = (TextView) convertView.findViewById(R.id.exam_room);
        } else viewHolder = (ViewHolder) convertView.getTag();

        Exam exam = getItem(position);
        viewHolder.exam_title.setText(exam.title);
        viewHolder.exam_type.setText(exam.examType);
        viewHolder.exam_branch.setText(exam.studyBranch);
        viewHolder.exam_day.setText(exam.day);
        viewHolder.exam_room.setText(exam.rooms);
        if (exam.endTime.isEmpty())
            viewHolder.exam_time.setText(exam.startTime);
        else viewHolder.exam_time.setText(context.getString(R.string.exams_time_value, exam.startTime, exam.endTime));

        return convertView;
    }

    static class ViewHolder {
        public TextView exam_title;
        public TextView exam_type;
        public TextView exam_branch;
        public TextView exam_day;
        public TextView exam_time;
        public TextView exam_room;
    }
}
