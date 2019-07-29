package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.OrderedRealmCollection;

public class MensaOverviewExpandableWeekAdapter extends BaseExpandableListAdapter {
    private static final String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();

    private List<String> listHeaderData;
    private HashMap<String, List<Meal>> listChildData;

    public MensaOverviewExpandableWeekAdapter(List<String> listHeaderData, HashMap<String, List<Meal>> listChildData) {
        this.listHeaderData = listHeaderData;
        this.listChildData = listChildData;
    }

    @Override
    public int getGroupCount() {
        return this.listHeaderData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listChildData.get(this.listHeaderData.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listHeaderData.get(groupPosition);
    }

    @Override
    public Meal getChild(int groupPosition, int childPosititon) {

        return (this.listChildData.get(this.listHeaderData.get(groupPosition)))
                .get(childPosititon);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.fragment_mensa_detail_item_week_group_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.day_title);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, @Nullable View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_mensa_detail_item_day, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.mensa_title);
            viewHolder.price_student = convertView.findViewById(R.id.mensa_price_student);
            viewHolder.price_employee = convertView.findViewById(R.id.mensa_price_employee);
            viewHolder.imagePork = convertView.findViewById(R.id.mensa_layout_pork);
            viewHolder.imageBeef = convertView.findViewById(R.id.mensa_layout_beef);
            viewHolder.imageVegetarian = convertView.findViewById(R.id.mensa_layout_vegetarian);
            viewHolder.imageVegan = convertView.findViewById(R.id.mensa_layout_vegan);
            viewHolder.imageGarlic = convertView.findViewById(R.id.mensa_layout_garlic);
            viewHolder.imageAlcohol = convertView.findViewById(R.id.mensa_layout_alcohol);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Meal meal = getChild(groupPosition, childPosition);
        if (meal == null) {
            return convertView;
        }

        viewHolder.title.setText(meal.getName());

        // Preis anzeigen
        final float priceStudent = (float) meal.getPrices().getStudents();
        final float priceEmployee = (float) meal.getPrices().getEmployees();

        viewHolder.price_student.setText(priceStudent == 0 ? context.getString(R.string.mensa_price_student_no_price) : context.getString(R.string.mensa_price_student, priceStudent));
        viewHolder.price_employee.setText(priceEmployee == 0 ? context.getString(R.string.mensa_price_employee_no_price) : context.getString(R.string.mensa_price_employee, priceEmployee));

        // Eigenschaften als Icon anzeigen
        viewHolder.imagePork.setVisibility(meal.getNotes().contains("enthält Schweinefleisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageBeef.setVisibility(meal.getNotes().contains("enthält Rindfleisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageVegetarian.setVisibility(meal.getNotes().contains("vegetarisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageVegan.setVisibility(meal.getNotes().contains("vegan") ? View.VISIBLE : View.GONE);
        viewHolder.imageGarlic.setVisibility(meal.getNotes().contains("enthält Knoblauch") ? View.VISIBLE : View.GONE);
        viewHolder.imageAlcohol.setVisibility(meal.getNotes().contains("enthält Alkohol") ? View.VISIBLE : View.GONE);

        return convertView;
    }



    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private static class ViewHolder {
        TextView title;
        TextView price_student;
        TextView price_employee;
        LinearLayout imagePork;
        LinearLayout imageBeef;
        LinearLayout imageVegetarian;
        LinearLayout imageVegan;
        LinearLayout imageGarlic;
        LinearLayout imageAlcohol;
    }
}

