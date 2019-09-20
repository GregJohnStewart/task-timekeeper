package com.gjs.taskTimekeeper.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//TODO:: rewrite these better
public class TaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskTest.class);

	@Test
	public void testBasics() {
		Task newTask = new Task("task");

		assertEquals("task", newTask.getName());

		HashMap<String, String> map = new HashMap<>();
		map.put("", "");

		newTask = new Task("task", map);
		assertEquals("task", newTask.getName());
		assertEquals(map, newTask.getAttributes());

		Task newTasktwo = new Task("task", map);

		assertEquals(newTask, newTasktwo);
		assertEquals(newTask.hashCode(), newTasktwo.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void testNullSetName() {
		new Task(null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSetAttributes() {
		new Task("hello", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWhitespaceName(){
		new Task("\t \n");
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