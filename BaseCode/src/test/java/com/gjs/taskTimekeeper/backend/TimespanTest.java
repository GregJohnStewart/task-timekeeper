package com.gjs.taskTimekeeper.backend;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

public class TimespanTest {


	@Test
	public void basicTest(){
		Task testTask = new Task("test");
		LocalDateTime ldt = LocalDateTime.now();

		Timespan newSpan = new Timespan(testTask);

		assertEquals(testTask, newSpan.getTask());
		assertNull(newSpan.getStartTime());
		assertNull(newSpan.getEndTime());
		assertFalse(newSpan.isComplete());

		newSpan = new Timespan(testTask, ldt);
		assertEquals(testTask, newSpan.getTask());
		assertEquals(ldt, newSpan.getStartTime());
		assertNull(newSpan.getEndTime());
		assertFalse(newSpan.isComplete());

		newSpan = new Timespan(testTask, ldt, ldt);
		assertEquals(testTask, newSpan.getTask());
		assertEquals(ldt, newSpan.getStartTime());
		assertEquals(ldt, newSpan.getEndTime());
		assertTrue(newSpan.isComplete());
		assertNotNull(newSpan.getDuration());

		Timespan newSpanTwo = new Timespan(testTask, ldt, ldt);

		assertEquals(newSpan, newSpanTwo);
		assertEquals(newSpan.hashCode(), newSpanTwo.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void testNullTask(){
		new Timespan(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testNotCompleteGetDuration(){
		new Timespan(new Task("")).getDuration();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadStart(){
		(new Timespan(new Task(""))).setEndTime(LocalDateTime.MIN).setStartTime(LocalDateTime.now());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadEnd(){
		(new Timespan(new Task(""))).setStartTime(LocalDateTime.MAX).setEndTime(LocalDateTime.now());
	}

	@Test
	public void testCompare(){
		LocalDateTime ldt = LocalDateTime.now();

		UUID uuid = UUID.randomUUID();

		Timespan main = new Timespan(new Task(uuid, ""));
		Timespan o = new Timespan(new Task(uuid, ""));

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

		main = new Timespan(new Task(""));
		o = new Timespan(new Task(""));

		assertNotEquals(0, main.compareTo(o));
	}

	@Test(expected = NullPointerException.class)
	public void testCompareNull(){
		new Timespan(new Task("")).compareTo(null);
	}

}