package com.gjs.taskTimekeeper.baseCode.core.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	
	@Test
	public void testNullTask() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new Timespan((Task)null);
		});
	}
	
	@Test
	public void testNotCompleteGetDuration() {
		assertEquals(Duration.ZERO, new Timespan(new Task("task")).getDuration());
	}
	
	@Test
	public void testBadStart() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			(new Timespan(new Task("")))
				.setEndTime(LocalDateTime.MIN)
				.setStartTime(LocalDateTime.now());
		});
	}
	
	@Test
	public void testBadEnd() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			(new Timespan(new Task("")))
				.setStartTime(LocalDateTime.MAX)
				.setEndTime(LocalDateTime.now());
		});
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
	
	@Test
	public void testCompareNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new Timespan(new Task("task")).compareTo(null);
		});
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
