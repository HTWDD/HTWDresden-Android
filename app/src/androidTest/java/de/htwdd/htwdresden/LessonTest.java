package de.htwdd.htwdresden;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.htwdd.htwdresden.classes.Const;

import static org.junit.Assert.assertEquals;

/**
 * Verschiedene Test im Zusammhang mit Veranstaltungen
 *
 * @author Kay Förster
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class LessonTest {
    @Test
    public void testGetCurrentDs() {
        assertEquals("Bestimmung der 1. Stunde fehlgeschlagen", 1, Const.Timetable.getCurrentDS(460L), 0);
        assertEquals("Bestimmung der 2. Stunde fehlgeschlagen", 2, Const.Timetable.getCurrentDS(580L), 0);
        assertEquals("Bestimmung der 3. Stunde fehlgeschlagen", 3, Const.Timetable.getCurrentDS(680L), 0);
        assertEquals("Bestimmung der 4. Stunde fehlgeschlagen", 4, Const.Timetable.getCurrentDS(810L), 0);
        assertEquals("Bestimmung der 5. Stunde fehlgeschlagen", 5, Const.Timetable.getCurrentDS(920L), 0);
        assertEquals("Bestimmung der 6. Stunde fehlgeschlagen", 6, Const.Timetable.getCurrentDS(1030L), 0);
        assertEquals("Bestimmung der 7. Stunde fehlgeschlagen", 7, Const.Timetable.getCurrentDS(1130L), 0);
        assertEquals("Bestimmung der - Stunde fehlgeschlagen", 0, Const.Timetable.getCurrentDS(300L), 0);
    }
}