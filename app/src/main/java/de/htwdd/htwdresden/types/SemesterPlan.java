package de.htwdd.htwdresden.types;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.Period;
import de.htwdd.htwdresden.interfaces.IGetContentValues;
import de.htwdd.htwdresden.interfaces.IParseJSON;

/**
 * @author Vitali Drazdovich, Artyom Dyadechkin
 */
public class SemesterPlan implements IParseJSON, IGetContentValues {
    private int year;
    private String type;
    private Period period;
    private FreeDay freeDays[];
    private Period lecturePeriod;
    private Period examsPeriod;
    private Period reregistration;

    public SemesterPlan(@NonNull final JSONObject semestePlan) throws JSONException {
        parseFromJSON(semestePlan);
    }

    public SemesterPlan(int year, String type, Period period, FreeDay[] freeDays, Period lecturePeriod, Period examsPeriod, Period reregistration) {
        this.year = year;
        this.type = type;
        this.period = period;
        this.freeDays = freeDays;
        this.lecturePeriod = lecturePeriod;
        this.examsPeriod = examsPeriod;
        this.reregistration = reregistration;
    }

    public int getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public FreeDay[] getFreeDays() {
        return freeDays;
    }

    public Period getLecturePeriod() {
        return lecturePeriod;
    }

    public Period getExamsPeriod() {
        return examsPeriod;
    }

    public Period getReregistration() {
        return reregistration;
    }

    @Override
    public void parseFromJSON(JSONObject semestePlanJSON) throws JSONException {
        final String endDay = "endDay";
        final String beginDay = "beginDay";

        year = semestePlanJSON.getInt("year");
        type = semestePlanJSON.getString("type");

        JSONObject period = semestePlanJSON.getJSONObject("period");
        this.period = new Period(period.getString(beginDay), period.getString(endDay));

        JSONObject lecturePeriod = semestePlanJSON.getJSONObject("lecturePeriod");
        this.lecturePeriod = new Period(lecturePeriod.getString(beginDay), lecturePeriod.getString(endDay));

        JSONObject examsPeriod = semestePlanJSON.getJSONObject("examsPeriod");
        this.examsPeriod = new Period(examsPeriod.getString(beginDay), examsPeriod.getString(endDay));

        JSONObject reregistration = semestePlanJSON.getJSONObject("reregistration");
        this.reregistration = new Period(reregistration.getString(beginDay), reregistration.getString(endDay));

        List<FreeDay> freeDaysList = new ArrayList<>();
        JSONArray freeDaysJSON = semestePlanJSON.getJSONArray("freeDays");
        for (int i = 0; i < freeDaysJSON.length(); i++) {
            JSONObject day = freeDaysJSON.getJSONObject(i);
            freeDaysList.add(new FreeDay(day.getString("name"), day.getString(beginDay), day.getString(endDay)));
        }

        freeDays = freeDaysList.toArray(new FreeDay[freeDaysList.size()]);
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_YEAR, year);
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_TYPE, type);
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_BEGIN, lecturePeriod.getBeginDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_LECTURE_PERIOD_END, lecturePeriod.getEndDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_BEGIN, examsPeriod.getBeginDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_EXAM_PERIOD_END, examsPeriod.getEndDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_BEGIN, period.getBeginDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_PERIOD_END, period.getEndDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_BEGIN, reregistration.getBeginDay());
        contentValues.put(Const.database.SemesterPlanTable.COLUMN_NAME_REG_PERIOD_END, reregistration.getEndDay());
        return contentValues;
    }
}
