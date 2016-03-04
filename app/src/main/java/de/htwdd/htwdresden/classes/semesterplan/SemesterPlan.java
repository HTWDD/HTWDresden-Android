package de.htwdd.htwdresden.classes.semesterplan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.htwdd.htwdresden.interfaces.IParseJSON;

public class SemesterPlan implements IParseJSON{

    public static final String END_DAY = "endDay";
    public static final String BEGIN_DAY = "beginDay";
    public static final String FREE_DAYS = "freeDays";
    public static final String NAME = "name";
    public static final String PERIOD = "period";
    public static final String YEAR = "year";
    public static final String TYPE = "type";
    public static final String LECTURE_PERIOD = "lecturePeriod";
    public static final String EXAMS_PERIOD = "examsPeriod";
    public static final String REREGISTRATION = "reregistration";

    public SemesterPlan(JSONObject semestePlan) throws JSONException {
        parseFromJSON(semestePlan);
    }

    public SemesterPlan() {
    }

    @Override
    public void parseFromJSON(JSONObject semestePlan) throws JSONException {
        year          = semestePlan.getInt(YEAR);
        type       = semestePlan.getString(TYPE);

        JSONObject p = semestePlan.getJSONObject(PERIOD);
        period = new Period(p.getString(BEGIN_DAY),p.getString(END_DAY));

        JSONObject l = semestePlan.getJSONObject(LECTURE_PERIOD);
        lecturePeriod = new Period(l.getString(BEGIN_DAY),l.getString(END_DAY));

        JSONObject e = semestePlan.getJSONObject(EXAMS_PERIOD);
        examsPeriod = new Period(e.getString(BEGIN_DAY),e.getString(END_DAY));

        JSONObject r = semestePlan.getJSONObject(REREGISTRATION);
        reregistration = new Period(r.getString(BEGIN_DAY),r.getString(END_DAY));


        List<FreeDay> freeDaysList = new ArrayList<FreeDay>();
        JSONArray freeDaysJSON = semestePlan.getJSONArray(FREE_DAYS);
        for (int i=0 ; i<freeDaysJSON.length();  i++){
            JSONObject day = freeDaysJSON.getJSONObject(i);
            freeDaysList.add(new FreeDay(day.getString(NAME),day.getString(BEGIN_DAY),day.getString(END_DAY)));
        }

        freeDays = freeDaysList.toArray(new FreeDay[freeDaysList.size()]);
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

    private int year;
    private String type;

    private Period period;
    private FreeDay freeDays[];
    private Period lecturePeriod;
    private Period examsPeriod;
    private Period reregistration;

    public int getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public Period getPeriod() {
        return period;
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
    public String toString() {
        return "SemesterPlan{" + "\n" +
                "year=" + year + ",\n" +
                "type='" + type + '\'' + ",\n" +
                "period=" + period + ",\n" +
                "freeDays=" + Arrays.toString(freeDays) + ",\n" +
                "lecturePeriod=" + lecturePeriod + ",\n" +
                "examsPeriod=" + examsPeriod + ",\n" +
                "reregistration=" + reregistration + "\n" +
                '}';
    }
    public boolean isThisSemester(int year,String type){
        return this.year == year && this.type.equalsIgnoreCase(type);
    }
    public static void main(String[] args) {
        SemesterPlan sp = new SemesterPlan(
                2015,
                "w",
                new Period("2015-09-01","2015-09-01"),
                new FreeDay[]{new FreeDay("Freeday 1", "2015-09-01", "2015-09-01"), new FreeDay("Freeday 2", "2015-09-01", "2015-09-01")},
                new Period("2015-09-01","2015-09-01"),
                new Period("2015-09-01","2015-09-01"),
                new Period("2015-09-01","2015-09-01")
        );
        System.out.println(sp.toString());
    }

}
