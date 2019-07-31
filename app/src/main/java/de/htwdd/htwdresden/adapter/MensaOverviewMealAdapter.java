package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.RealmResults;

public class MensaOverviewMealAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, RealmResults<Meal>> listDataChild;

    public MensaOverviewMealAdapter(Context context, List<String> listDataHeader,
                                    HashMap<String, RealmResults<Meal>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Meal getChild(int groupPosition, int childPosition) {

        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Meal child = (Meal) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert infalInflater != null;
            convertView = infalInflater.inflate(R.layout.fragment_mensa_detail_item_week_group_item, null);
        }

        TextView txtNameChild = (TextView) convertView
                .findViewById(R.id.mensa_title);
        TextView txtPriceStudentChild = (TextView) convertView
                .findViewById(R.id.mensa_price_student);
        TextView txtPriceEmployeeChild = (TextView) convertView
                .findViewById(R.id.mensa_price_employee);

        LinearLayout imgPork = (LinearLayout) convertView
                .findViewById(R.id.mensa_layout_pork);
        LinearLayout imgBeef = (LinearLayout) convertView
                .findViewById(R.id.mensa_layout_beef);
        LinearLayout imgVegetarian = (LinearLayout) convertView
                .findViewById(R.id.mensa_layout_vegetarian);
        LinearLayout imgVegan = (LinearLayout) convertView
                .findViewById(R.id.mensa_layout_vegan);
        LinearLayout imgGarlic = (LinearLayout) convertView
                .findViewById(R.id.mensa_layout_garlic);
        LinearLayout imgAlcohol = (LinearLayout) convertView
                .findViewById(R.id.mensa_layout_alcohol);

        txtNameChild.setText(child.getName());

        // Preis anzeigen
        final float priceStudent = (float) child.getPrices().getStudents();
        final float priceEmployee = (float) child.getPrices().getEmployees();

        txtPriceStudentChild.setText(priceStudent == 0 ? context.getString(R.string.mensa_price_student_no_price) : context.getString(R.string.mensa_price_student, priceStudent));
        txtPriceEmployeeChild.setText(priceEmployee == 0 ? context.getString(R.string.mensa_price_employee_no_price) : context.getString(R.string.mensa_price_employee, priceEmployee));

        // Eigenschaften als Icon anzeigen
        imgPork.setVisibility(child.getNotes().contains("enthält Schweinefleisch") ? View.VISIBLE : View.GONE);
        imgBeef.setVisibility(child.getNotes().contains("enthält Rindfleisch") ? View.VISIBLE : View.GONE);
        imgVegetarian.setVisibility(child.getNotes().contains("vegetarisch") ? View.VISIBLE : View.GONE);
        imgVegan.setVisibility(child.getNotes().contains("vegan") ? View.VISIBLE : View.GONE);
        imgGarlic.setVisibility(child.getNotes().contains("enthält Knoblauch") ? View.VISIBLE : View.GONE);
        imgAlcohol.setVisibility(child.getNotes().contains("enthält Alkohol") ? View.VISIBLE : View.GONE);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}