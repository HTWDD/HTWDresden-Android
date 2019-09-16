package de.htwdd.htwdresden.ui.models


//-------------------------------------------------------------------------------------------------- JSON
data class JStudyYear(
    val studyYear: Long,
    val studyCourses: List<JStudyCourse>
)

data class JStudyCourse(
    val studyCourse: String,
    val name: String,
    val studyGroups: List<JStudyGroup>
)

data class JStudyGroup (
    val studyGroup: String,
    val name: String,
    val grade: Long
)

//-------------------------------------------------------------------------------------------------- Model
class StudyYear(
    val studyYear: Long,
    val studyCourses: List<StudyCourse>
) {
    companion object {
        fun from(json: JStudyYear): StudyYear {
            return StudyYear(
                json.studyYear,
                json.studyCourses.map { StudyCourse.from(it) }
            )
        }
    }
}

class StudyCourse(
    val studyCourse: String,
    val name: String,
    val studyGroups: List<StudyGroup>
) {
    companion object {
        fun from(json: JStudyCourse): StudyCourse {
            return StudyCourse(
                json.studyCourse,
                json.name,
                json.studyGroups.map { StudyGroup.from(it) }
            )
        }
    }
}

class StudyGroup(
    val studyGroup: String,
    val name: String,
    val grade: Long
) {
    companion object {
        fun from(json: JStudyGroup): StudyGroup {
            return StudyGroup(
                json.studyGroup,
                json.name,
                json.grade)
        }
    }
}
