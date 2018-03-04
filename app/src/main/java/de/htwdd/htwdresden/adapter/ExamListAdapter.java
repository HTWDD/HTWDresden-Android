package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.exams.ExamDate;

/**
 * Adapter zur Anzeige von Prüfungen
 *
 * @author Kay Förster
 */
public class ExamListAdapter extends AbstractBaseAdapter<ExamDate> {

    public ExamListAdapter(Context context, ArrayList<ExamDate> data) {
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

            viewHolder.exam_title = (TextView) convertView.findViewById(R.id.stats_semester);
            viewHolder.exam_type = (TextView) convertView.findViewById(R.id.exam_type);
            viewHolder.exam_branch = (TextView) convertView.findViewById(R.id.exam_studyBranch);
            viewHolder.exam_day = (TextView) convertView.findViewById(R.id.exam_day);
            viewHolder.exam_time = (TextView) convertView.findViewById(R.id.exam_time);
            viewHolder.exam_room = (TextView) convertView.findViewById(R.id.exam_room);
        } else viewHolder = (ViewHolder) convertView.getTag();

        ExamDate examDate = getItem(position);
        viewHolder.exam_title.setText(examDate.title);
        viewHolder.exam_type.setText(examDate.examType);
        viewHolder.exam_branch.setText(examDate.studyBranch);
        viewHolder.exam_day.setText(examDate.day);
        viewHolder.exam_room.setText(examDate.rooms);
        if (examDate.endTime.isEmpty())
            viewHolder.exam_time.setText(examDate.startTime);
        else viewHolder.exam_time.setText(context.getString(R.string.exams_time_value, examDate.startTime, examDate.endTime));

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
