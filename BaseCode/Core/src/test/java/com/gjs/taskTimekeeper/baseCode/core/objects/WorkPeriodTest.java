package com.gjs.taskTimekeeper.baseCode.core.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class WorkPeriodTest {
	private static final Task testTask = new Task("Test task");
	private static final Task testTaskTwo = new Task("Test task Two");
	
	private static final LocalDateTime now = LocalDateTime.now();
	private static final LocalDateTime nowPlusFive = now.plusMinutes(5);
	private static final LocalDateTime nowPlusSix = now.plusMinutes(6);
	private static final LocalDateTime nowPlusSeven = now.plusMinutes(7);
	private static final LocalDateTime nowPlusEight = now.plusMinutes(8);
	private static final LocalDateTime nowPlusTen = now.plusMinutes(10);
	
	@Test
	public void testEquals() {
		WorkPeriod periodOne = new WorkPeriod();
		WorkPeriod periodTwo = new WorkPeriod();
		
		assertEquals(periodOne, periodTwo);
		
		String attOneKey = "one";
		String attOneVal = "one";
		
		periodOne.getAttributes().put(attOneKey, attOneVal);
		periodTwo.getAttributes().put(attOneKey, attOneVal);
		
		assertEquals(periodOne, periodTwo);
		
		periodTwo.getAttributes().put(attOneKey, "something else");
		
		assertNotEquals(periodOne, periodTwo);
	}
	
	@Test
	public void testBasics() {
		WorkPeriod period = new WorkPeriod();
		WorkPeriod periodTwo = new WorkPeriod();
		
		assertEquals(period, periodTwo);
		assertNotNull(period.getTimespans());
		assertNotNull(period.getAttributes());
		
		period = new WorkPeriod(new TreeSet<>(), new HashMap<>());
		TreeSet<Timespan> timespans = new TreeSet<>();
		
		assertEquals(timespans, period.getTimespans());
		
		Map<String, String> atts = new HashMap<>();
		
		period = new WorkPeriod(timespans, atts);
		
		assertEquals(atts, period.getAttributes());
		
		period.addTimespan(new Timespan(testTask));
		period.addTimespan(new Timespan(testTask, now));
		
		assertEquals(2, period.getTimespans().size());
		
		period.hashCode();
	}
	
	@Test
	public void testCompare() {
		WorkPeriod main = new WorkPeriod();
		WorkPeriod o = new WorkPeriod();
		
		Timespan spanOne = new Timespan(testTask, now, nowPlusFive);
		Timespan spanTwo = new Timespan(testTask, nowPlusFive, nowPlusTen);
		
		// -1 if this starts before o
		main.addTimespan(spanOne);
		o.addTimespan(spanTwo);
		
		assertEquals(-1, main.compareTo(o));
		
		// -1 if o has no start
		main = new WorkPeriod();
		o = new WorkPeriod();
		main.addTimespan(spanOne);
		
		assertEquals(-1, main.compareTo(o));
		
		//  0 if this starts at the same time as o
		main = new WorkPeriod();
		o = new WorkPeriod();
		main.addTimespan(spanOne);
		o.addTimespan(spanOne);
		
		assertEquals(0, main.compareTo(o));
		
		//  0 if both this and o have no start time
		main = new WorkPeriod();
		o = new WorkPeriod();
		assertEquals(0, main.compareTo(o));
		
		//  1 if this starts after o
		main = new WorkPeriod();
		o = new WorkPeriod();
		main.addTimespan(spanTwo);
		o.addTimespan(spanOne);
		assertEquals(1, main.compareTo(o));
		
		//  1 if this has no start
		main = new WorkPeriod();
		o = new WorkPeriod();
		o.addTimespan(spanOne);
		assertEquals(1, main.compareTo(o));
	}
	
	@Test
	public void testComparetoNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			//noinspection ResultOfMethodCallIgnored
			new WorkPeriod().compareTo((WorkPeriod)null);
		});
	}
	
	@Test
	public void testDuration() {
		WorkPeriod period = new WorkPeriod();
		
		assertEquals(0, Duration.ZERO.compareTo(period.getTotalTime()));
		
		period.addTimespan(new Timespan(testTask, now, nowPlusFive));
		
		assertEquals(Duration.ofMinutes(5), period.getTotalTime());
		
		Timespan secondTimespan = new Timespan(testTaskTwo, now);
		
		period.addTimespan(secondTimespan);
		
		assertEquals(Duration.ofMinutes(5), period.getTotalTime());
		
		secondTimespan.setEndTime(nowPlusFive);
		
		assertEquals(Duration.ofMinutes(10), period.getTotalTime());
	}
	
	@Test
	public void testSetTimespans() {
		Timespan spanOne = new Timespan(testTask, now, nowPlusFive);
		Timespan spanTwo = new Timespan(testTask, nowPlusFive, nowPlusTen);
		
		WorkPeriod period = new WorkPeriod(Arrays.asList(spanOne, spanTwo));
		
		assertFalse(period.getTimespans().isEmpty());
		assertEquals(2, period.getTimespans().size());
		
		period = new WorkPeriod();
		
		SortedSet<Timespan> set = new TreeSet<>();
		set.add(spanOne);
		set.add(spanTwo);
		
		period.setTimespans(set);
		
		assertFalse(period.getTimespans().isEmpty());
		assertEquals(2, period.getNumTimespans());
	}
	
	@Test
	public void testSetNullTimespans() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new WorkPeriod(Arrays.asList((Timespan)null));
		});
	}
	
	@Test
	public void addTimespans() {
		WorkPeriod period = new WorkPeriod();
		
		Timespan spanOne = new Timespan(testTask);
		
		assertTrue(period.addTimespan(spanOne));
		assertEquals(1, period.getNumTimespans());
		assertTrue(period.contains(spanOne));
		
		period.addTimespans(new Timespan(testTask, now), new Timespan(testTask, nowPlusFive));
		
		assertEquals(3, period.getNumTimespans());
	}
	
	@Test
	public void setNullTimespans() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new WorkPeriod().setTimespans(null);
		});
	}
	
	@Test
	public void setNullAtts() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new WorkPeriod().setAttributes(null);
		});
	}
	
	@Test
	public void addNullTimespan() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new WorkPeriod().addTimespan(null);
		});
	}
	
	@Test
	public void hasTimespansWithTask() {
		WorkPeriod period = new WorkPeriod();
		
		Timespan spanOne = new Timespan(new Task("task"), now, nowPlusFive);
		Timespan spanTwo = new Timespan(testTask, nowPlusFive, nowPlusTen);
		
		assertFalse(period.hasTimespansWith(testTask));
		
		period.addTimespan(spanOne);
		assertFalse(period.hasTimespansWith(testTask));
		
		period.addTimespan(spanTwo);
		assertTrue(period.hasTimespansWith(testTask));
	}
	
	@Test
	public void getTimespansWithTask() {
		WorkPeriod period = new WorkPeriod();
		
		Timespan spanOne = new Timespan(new Task("task"), now, nowPlusFive);
		Timespan spanTwo = new Timespan(testTask, nowPlusFive, nowPlusTen);
		
		assertTrue(period.getTimespansWith(testTask).isEmpty());
		
		period.addTimespan(spanOne);
		assertTrue(period.getTimespansWith(testTask).isEmpty());
		
		period.addTimespan(spanTwo);
		assertFalse(period.getTimespansWith(testTask).isEmpty());
		assertEquals(1, period.getTimespansWith(testTask).size());
	}
	
	@Test
	public void getStartNoTimespans() {
		assertNull(new WorkPeriod().getStart());
	}
	
	@Test
	public void getStartNoStartTimes() {
		WorkPeriod period = new WorkPeriod();
		period.addTimespans(new Timespan(testTask));
		
		assertNull(period.getStart());
	}
	
	@Test
	public void getStart() {
		WorkPeriod period = new WorkPeriod();
		period.addTimespan(new Timespan(testTask, now));
		
		assertEquals(now, period.getStart());
	}
	
	@Test
	public void getEndNoTimespans() {
		assertNull(new WorkPeriod().getEnd());
	}
	
	@Test
	public void getEndNoEndTimes() {
		WorkPeriod period = new WorkPeriod();
		period.addTimespans(new Timespan(testTask));
		
		assertNull(period.getEnd());
	}
	
	@Test
	public void getEnd() {
		WorkPeriod period = new WorkPeriod();
		period.addTimespan(new Timespan(testTask, now, nowPlusFive));
		
		assertEquals(nowPlusFive, period.getEnd());
	}
	
	@Test
	public void getHasUnfinishedTimespans() {
		WorkPeriod period = new WorkPeriod();
		
		assertTrue(period.isUnCompleted());
		assertTrue(period.getUnfinishedTimespans().isEmpty());
		assertFalse(period.hasUnfinishedTimespans());
		
		Timespan spanOne = new Timespan(testTask);
		
		period.addTimespan(spanOne);
		
		assertFalse(period.getUnfinishedTimespans().isEmpty());
		assertEquals(1, period.getUnfinishedTimespans().size());
		assertTrue(period.hasUnfinishedTimespans());
		
		spanOne.setStartTime(now);
		spanOne.setEndTime(nowPlusFive);
		
		assertTrue(period.getUnfinishedTimespans().isEmpty());
		assertFalse(period.hasUnfinishedTimespans());
		assertFalse(period.isUnCompleted());
	}
	
	@Test
	public void getTasks() {
		WorkPeriod period = new WorkPeriod();
		
		assertTrue(period.getTaskNames().isEmpty());
		
		period.addTimespan(new Timespan(testTask));
		
		assertFalse(period.getTaskNames().isEmpty());
		assertTrue(period.getTaskNames().contains(testTask.getName()));
		
		period.addTimespan(new Timespan(testTaskTwo));
		
		assertTrue(
			period.getTaskNames()
				  .containsAll(Arrays.asList(testTask.getName(), testTaskTwo.getName())));
	}
	
	@Test
	public void serialization() throws IOException {
		ObjectMapper mapper = ObjectMapperUtilities.getDefaultMapper();
		
		WorkPeriod period = new WorkPeriod();
		
		period.getAttributes().put("test", "value");
		period.addTimespan(new Timespan(new Task("test task")));
		
		String serialized = mapper.writeValueAsString(period);
		
		WorkPeriod deserialized = mapper.readValue(serialized, WorkPeriod.class);
		
		assertEquals(period, deserialized);
	}
	
	@Test
	public void compareToWorkPeriod() {
		WorkPeriod one = new WorkPeriod();
		WorkPeriod two = new WorkPeriod();
		assertEquals(0, one.compareTo(two));
		
		try {
			//noinspection ResultOfMethodCallIgnored
			one.compareTo((WorkPeriod)null);
			fail();
		} catch(NullPointerException e) {
			// expected
		}
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask)));
		assertEquals(0, one.compareTo(two));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask)));
		two = new WorkPeriod(Arrays.asList(new Timespan(testTask)));
		assertEquals(0, one.compareTo(two));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask)));
		two = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		assertEquals(1, one.compareTo(two));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		two = new WorkPeriod(Arrays.asList(new Timespan(testTask)));
		assertEquals(-1, one.compareTo(two));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		two = new WorkPeriod(Arrays.asList(new Timespan(testTask)));
		assertEquals(-1, one.compareTo(two));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		two = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		assertEquals(0, one.compareTo(two));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		two = new WorkPeriod(Arrays.asList(new Timespan(testTask, nowPlusFive)));
		assertEquals(-1, one.compareTo(two));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, nowPlusFive)));
		two = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		assertEquals(1, one.compareTo(two));
	}
	
	@Test
	public void compareToTimePeriod() {
		WorkPeriod one = new WorkPeriod(Arrays.asList(new Timespan(testTask)));
		assertEquals(0, one.compareTo((LocalDateTime)null));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask)));
		assertEquals(0, one.compareTo(now));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		assertEquals(0, one.compareTo(now));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now, nowPlusTen)));
		assertEquals(0, one.compareTo(now));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now, nowPlusTen)));
		assertEquals(0, one.compareTo(nowPlusFive));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now, nowPlusFive)));
		assertEquals(-1, one.compareTo(nowPlusSix));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, nowPlusFive, nowPlusEight)));
		assertEquals(-1, one.compareTo(nowPlusTen));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, nowPlusFive, nowPlusEight)));
		assertEquals(1, one.compareTo(now));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, null, now)));
		assertEquals(0, one.compareTo(now));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, null, nowPlusEight)));
		assertEquals(1, one.compareTo(now));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, null, nowPlusEight)));
		assertEquals(-1, one.compareTo(nowPlusTen));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, now)));
		assertEquals(0, one.compareTo(now));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, nowPlusEight)));
		assertEquals(1, one.compareTo(now));
		
		one = new WorkPeriod(Arrays.asList(new Timespan(testTask, nowPlusEight)));
		assertEquals(-1, one.compareTo(nowPlusTen));
	}
}
