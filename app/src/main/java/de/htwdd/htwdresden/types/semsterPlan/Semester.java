package de.htwdd.htwdresden.types.semsterPlan;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Plan eines Semesters
 *
 * @author Kay Förster
 */
public class Semester extends RealmObject {
    @Index
    private int year;
    @Index
    private String type;
    private TimePeriod period;
    private RealmList<TimePeriod> freeDays;
    private TimePeriod lecturePeriod;
    private TimePeriod examsPeriod;
    private TimePeriod reregistration;

    public int getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public RealmList<TimePeriod> getFreeDays() {
        return freeDays;
    }

    public TimePeriod getLecturePeriod() {
        return lecturePeriod;
    }

    public TimePeriod getExamsPeriod() {
        return examsPeriod;
    }

    public TimePeriod getReregistration() {
        return reregistration;
    }
}
