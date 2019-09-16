package de.htwdd.htwdresden.ui.models

import com.google.gson.annotations.SerializedName

//-------------------------------------------------------------------------------------------------- JSON
data class JCourse (
    @SerializedName("AbschlTxt") val abschlTxt: String,
    @SerializedName("POVersion") val poVersion: Long,
    @SerializedName("AbschlNr") val abschlNr: String,
    @SerializedName("StgNr") val stgNr: String,
    @SerializedName("StgTxt") val stgTxt: String
)

//-------------------------------------------------------------------------------------------------- Course
class Course(
    val graduation: String,
    val graduationNumber: String,
    val examinationRegulations: Long,
    val major: String,
    val majorNumber: String
) {
    companion object {
        fun from(json: JCourse): Course {
            return Course(
                json.abschlTxt,
                json.abschlNr,
                json.poVersion,
                json.stgTxt,
                json.stgNr)
        }
    }
}

