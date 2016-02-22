package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.ExamResult;

/**
 * Adapter zur Anzeige der Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public class ExamResultListAdapter extends BaseExpandableListAdapter {
    private final String[] semesterNames;
    private final HashMap<Integer, ArrayList<ExamResult>> data;
    private Integer[] header;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public ExamResultListAdapter(Context context, HashMap<Integer, ArrayList<ExamResult>> data) {
        this.context = context;
        this.data = data;
        // Array mit Headern erstellen
        this.header = Arrays.copyOf(data.keySet().toArray(), data.keySet().size(), Integer[].class);
        // Sortieren
        Arrays.sort(header);

        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        semesterNames = context.getResources().getStringArray(R.array.semesterName);
    }

    @Override
    public void notifyDataSetChanged() {
        // Array mit Headern erstellen
        this.header = Arrays.copyOf(data.keySet().toArray(), data.keySet().size(), Integer[].class);
        // Sortieren
        Arrays.sort(header);

        super.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return data.keySet().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return data.get(header[i]).size();
    }

    @Override
    public Object getGroup(int i) {
        return header[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        return data.get(header[i]).get(i1);
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
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.fragment_exams_result_group, viewGroup, false);
            view.setTag(viewHolder);
            viewHolder.textView1 = (TextView) view.findViewById(R.id.listHeader);
        } else viewHolder = (ViewHolder) view.getTag();

        viewHolder.textView1.setText(Const.Semester.getSemesterName(semesterNames, (Integer)getGroup(i)));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final ExamResult examResult = (ExamResult) getChild(i, i1);
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

        // Modullnamen setzen
        viewHolder.textView1.setText(examResult.modul);
        viewHolder.textView1.setTypeface(null, Typeface.NORMAL);
        viewHolder.textView1.setTextColor(Color.BLACK);

        // Vermerk leeren
        viewHolder.textView2.setText("");

        // Genauen Status setzen
        switch (examResult.status) {
            case "AN":
                viewHolder.textView1.setTypeface(null, Typeface.ITALIC);
                viewHolder.textView1.setTextColor(Color.GRAY);

                // Student hat sich abgemeldet
                switch (examResult.vermerk) {
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
                if (examResult.credits != 0.0f)
                    viewHolder.textView1.setTextColor(ContextCompat.getColor(context, R.color.exam_results_green));
                else
                    viewHolder.textView1.setTextColor(Color.BLACK);
                break;
            // Student hat NICHT bestanden
            case "NB":
            case "EN":
                viewHolder.textView1.setTextColor(Color.RED);
                break;
        }

        // Note und Credits setzen
        viewHolder.textView3.setText(context.getString(R.string.exams_result_grade, examResult.note));
        viewHolder.textView4.setText(context.getString(R.string.exams_result_credits, examResult.credits));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    static class ViewHolder {
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;
    }
}
