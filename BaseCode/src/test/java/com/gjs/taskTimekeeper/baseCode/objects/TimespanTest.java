package com.gjs.taskTimekeeper.baseCode.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.utils.ObjectMapperUtilities;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.Test;

public class TimespanTest {

    private static final LocalDateTime now = LocalDateTime.now();
    private static final LocalDateTime nowPlusFive = now.plusMinutes(5);

    @Test
    public void basicTest() {
        Task testTask = new Task("test");
        LocalDateTime ldt = LocalDateTime.now();

        Timespan newSpan = new Timespan(testTask);

        assertEquals(testTask.getName(), newSpan.getTaskName());
        assertNull(newSpan.getStartTime());
        assertNull(newSpan.getEndTime());
        assertFalse(newSpan.isComplete());

        newSpan = new Timespan(testTask, ldt);
        assertEquals(testTask.getName(), newSpan.getTaskName());
        assertEquals(ldt, newSpan.getStartTime());
        assertNull(newSpan.getEndTime());
        assertFalse(newSpan.isComplete());

        newSpan = new Timespan(testTask, ldt, ldt);
        assertEquals(testTask.getName(), newSpan.getTaskName());
        assertEquals(ldt, newSpan.getStartTime());
        assertEquals(ldt, newSpan.getEndTime());
        assertTrue(newSpan.isComplete());
        assertNotNull(newSpan.getDuration());

        Timespan newSpanTwo = new Timespan(testTask, ldt, ldt);

        assertEquals(newSpan, newSpanTwo);
        assertEquals(newSpan.hashCode(), newSpanTwo.hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void testNullTask() {
        new Timespan((Task) null);
    }

    @Test
    public void testNotCompleteGetDuration() {
        assertEquals(Duration.ZERO, new Timespan(new Task("task")).getDuration());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadStart() {
        (new Timespan(new Task("")))
                .setEndTime(LocalDateTime.MIN)
                .setStartTime(LocalDateTime.now());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadEnd() {
        (new Timespan(new Task("")))
                .setStartTime(LocalDateTime.MAX)
                .setEndTime(LocalDateTime.now());
    }

    @Test
    public void testCompare() {
        LocalDateTime ldt = LocalDateTime.now();

        Timespan main = new Timespan(new Task("task"));
        Timespan o = new Timespan(new Task("task"));

        assertEquals(0, main.compareTo(o));

        main.setStartTime(ldt);
        assertEquals(-1, main.compareTo(o));

        main.setStartTime(null);
        o.setStartTime(ldt);
        assertEquals(1, main.compareTo(o));

        main.setStartTime(ldt);
        o.setStartTime(ldt);

        o.setStartTime(LocalDateTime.MIN);
        assertTrue(main.compareTo(o) > 0);

        o.setStartTime(LocalDateTime.MAX);
        assertTrue(main.compareTo(o) < 0);

        main = new Timespan(new Task("task"));
        o = new Timespan(new Task("task"));

        assertEquals(0, main.compareTo(o));
    }

    @Test(expected = NullPointerException.class)
    public void testCompareNull() {
        new Timespan(new Task("task")).compareTo(null);
    }

    @Test
    public void serialization() throws IOException {
        ObjectMapper mapper = ObjectMapperUtilities.getDefaultMapper();

        Timespan span = new Timespan(new Task("task"), now, nowPlusFive);

        String serialization = mapper.writeValueAsString(span);

        Timespan deserialized = mapper.readValue(serialization, Timespan.class);

        assertEquals(span, deserialized);
    }
}
