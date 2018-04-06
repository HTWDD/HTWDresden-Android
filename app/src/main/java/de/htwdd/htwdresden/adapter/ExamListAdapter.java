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

            viewHolder.exam_title = convertView.findViewById(R.id.stats_semester);
            viewHolder.exam_type = convertView.findViewById(R.id.exam_type);
            viewHolder.exam_branch = convertView.findViewById(R.id.exam_studyBranch);
            viewHolder.exam_day = convertView.findViewById(R.id.exam_day);
            viewHolder.exam_time = convertView.findViewById(R.id.exam_time);
            viewHolder.exam_room = convertView.findViewById(R.id.exam_room);
        } else viewHolder = (ViewHolder) convertView.getTag();

        ExamDate examDate = getItem(position);
        viewHolder.exam_title.setText(examDate.getTitle());
        viewHolder.exam_type.setText(examDate.getExamType());
        viewHolder.exam_branch.setText(examDate.getStudyBranch());
        viewHolder.exam_day.setText(examDate.getDay());
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String room : examDate.getRooms()) {
            stringBuilder.append(room).append("; ");
        }
        viewHolder.exam_room.setText(stringBuilder);
        if (examDate.getEndTime().isEmpty()) {
            viewHolder.exam_time.setText(examDate.getStartTime());
        } else viewHolder.exam_time.setText(context.getString(R.string.exams_time_value, examDate.getStartTime(), examDate.getEndTime()));

        return convertView;
    }

    static class ViewHolder {
        TextView exam_title;
        TextView exam_type;
        TextView exam_branch;
        TextView exam_day;
        TextView exam_time;
        TextView exam_room;
    }
}
