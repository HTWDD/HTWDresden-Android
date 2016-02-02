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
        assertEquals("Bestimmung der 1. Stunde fehlgeschlagen", 1, Const.Timetable.getCurrentDS(27600000l), 0);
        assertEquals("Bestimmung der 2. Stunde fehlgeschlagen", 2, Const.Timetable.getCurrentDS(34200000l), 0);
        assertEquals("Bestimmung der 3. Stunde fehlgeschlagen", 3, Const.Timetable.getCurrentDS(41400000l), 0);
        assertEquals("Bestimmung der 4. Stunde fehlgeschlagen", 4, Const.Timetable.getCurrentDS(48000000l), 0);
        assertEquals("Bestimmung der 5. Stunde fehlgeschlagen", 5, Const.Timetable.getCurrentDS(54600000l), 0);
        assertEquals("Bestimmung der 6. Stunde fehlgeschlagen", 6, Const.Timetable.getCurrentDS(61200000l), 0);
        assertEquals("Bestimmung der 7. Stunde fehlgeschlagen", 7, Const.Timetable.getCurrentDS(67500000l), 0);
        assertEquals("Bestimmung der - Stunde fehlgeschlagen", 0, Const.Timetable.getCurrentDS(60000l), 0);
    }
}