package de.htwdd.htwdresden.types.exams;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Repräsentation eines Prüfungstermins
 *
 * @author Kay Förster
 */
public class ExamDate {

    @SerializedName("Title")
    private String title;
    @SerializedName("ExamType")
    private String examType;
    @SerializedName("StudyBranch")
    private String studyBranch;
    @SerializedName("Day")
    private String day;
    @SerializedName("StartTime")
    private String startTime;
    @SerializedName("EndTime")
    private String endTime;
    @SerializedName("Examiner")
    private String examiner;
    @SerializedName("NextChance")
    private String nextChance;
    @SerializedName("Rooms")
    private List<String> rooms;

    public String getTitle() {
        return title;
    }

    public String getExamType() {
        return examType;
    }

    public String getStudyBranch() {
        return studyBranch;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getExaminer() {
        return examiner;
    }

    public String getNextChance() {
        return nextChance;
    }

    public List<String> getRooms() {
        return rooms;
    }
}
