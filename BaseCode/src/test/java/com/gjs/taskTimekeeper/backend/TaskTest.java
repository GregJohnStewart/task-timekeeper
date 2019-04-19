package com.gjs.taskTimekeeper.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class TaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskTest.class);

	@Test
	public void testBasics() {
		Task newTask = new Task("");

		assertEquals("", newTask.getName());

		HashMap<String, String> map = new HashMap<>();
		map.put("", "");

		newTask = new Task("", map);
		assertEquals("", newTask.getName());
		assertEquals(map, newTask.getAttributes());

		Task newTasktwo = new Task("", map);

		assertNotEquals(newTask, newTasktwo);
		assertNotEquals(newTask.hashCode(), newTasktwo.hashCode());

		assertNotNull(newTask.getUuid());
	}

	@Test(expected = NullPointerException.class)
	public void testNullUuid() {
		new Task(null, "");
	}

	@Test(expected = NullPointerException.class)
	public void testNullSetName() {
		new Task(null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSetAttributes() {
		new Task("", null);
	}

	@Test
	public void serialization() throws IOException {
		ObjectMapper mapper = TimeManager.MAPPER;

		Task testTask = new Task("test task");

		testTask.getAttributes().put("testAtt", "value");

		String string = mapper.writeValueAsString(testTask);

		Task deserializedTask = mapper.readValue(string, Task.class);

		assertTrue(testTask.equals(deserializedTask));
	}

}