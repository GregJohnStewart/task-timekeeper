package com.gjs.taskTimekeeper.baseCode.core.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO:: rewrite these better
@Slf4j
public class TaskTest {
	
	@Test
	public void testBasics() {
		Task newTask = new Task("task");
		
		assertEquals("task", newTask.getName().getName());
		
		HashMap<String, String> map = new HashMap<>();
		map.put("", "");
		
		newTask = new Task("task", map);
		assertEquals("task", newTask.getName().getName());
		assertEquals(map, newTask.getAttributes());
		
		Task newTasktwo = new Task("task", map);
		
		assertEquals(newTask, newTasktwo);
		assertEquals(newTask.hashCode(), newTasktwo.hashCode());
	}
	
	@Test
	public void testNullSetName() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new Task((Name)null);
		});
	}
	
	@Test
	public void testNullSetAttributes() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new Task("hello", null);
		});
	}
	
	@Test
	public void testWhitespaceName() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			new Task("\t \n");
		});
	}
	
	@Test
	public void serialization() throws IOException {
		ObjectMapper mapper = ObjectMapperUtilities.getDefaultMapper();
		
		Task testTask = new Task("test task");
		
		testTask.getAttributes().put("testAtt", "value");
		
		String string = mapper.writeValueAsString(testTask);
		
		Task deserializedTask = mapper.readValue(string, Task.class);
		
		assertTrue(testTask.equals(deserializedTask));
	}
}
