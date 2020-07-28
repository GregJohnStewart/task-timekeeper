package com.gjs.taskTimekeeper.baseCode.core.objects;

import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ManagerSerializationTest {
	
	private TimeManager manager;
	
	@BeforeEach
	public void setup() {
		manager = new TimeManager();
		Task task =
			new Task(
				"Test Task",
				new HashMap<String, String>() {
					{
						put("att", "val");
					}
				}
			);
		manager.addTask(task);
		
		manager.addWorkPeriod(
			new WorkPeriod(
				Stream.of(
					new Timespan(
						task,
						LocalDateTime.now(),
						LocalDateTime.now().plusMinutes(5)
					),
					new Timespan(
						task,
						LocalDateTime.now().plusMinutes(10),
						LocalDateTime.now().plusMinutes(15)
					)
				)
					  .collect(Collectors.toList()),
				new HashMap<String, String>() {
					{
						put("att2", "value");
					}
				}
			));
		manager.addWorkPeriod(
			new WorkPeriod(
				Stream.of(
					new Timespan(
						task,
						LocalDateTime.now().plusMinutes(20),
						LocalDateTime.now().plusMinutes(25)
					),
					new Timespan(
						task,
						LocalDateTime.now().plusMinutes(30),
						LocalDateTime.now().plusMinutes(35)
					)
				)
					  .collect(Collectors.toList()),
				new HashMap<String, String>() {
					{
						put("att3", "value2");
					}
				}
			));
	}
	
	@Test
	public void testDeserialization() throws IOException {
		String output = ObjectMapperUtilities.getDefaultMapper().writeValueAsString(this.manager);
		
		log.debug("Serialized time manager: {}", output);
		
		TimeManager deserialized =
			ObjectMapperUtilities.getDefaultMapper().readValue(output, TimeManager.class);
		
		assertEquals(this.manager, deserialized);
	}
}
