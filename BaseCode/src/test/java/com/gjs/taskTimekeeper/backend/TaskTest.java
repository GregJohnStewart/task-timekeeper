package com.gjs.taskTimekeeper.backend;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskTest.class);

	@Test
	public void testBasics(){
		Task newTask = new Task("");

		assertEquals("", newTask.getName());

		HashMap<String, String> map = new HashMap<>();
		map.put("", "");

		newTask = new Task("", map);
		assertEquals("", newTask.getName());
		assertEquals(map, newTask.getAttributes());

		Task newTasktwo = new Task("", map);

		assertEquals(newTask, newTasktwo);
		assertEquals(newTask.hashCode(), newTasktwo.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void testNullSetName(){
		new Task(null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSetAttributes(){
		new Task("", null);
	}

}