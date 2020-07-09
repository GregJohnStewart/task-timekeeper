package com.gjs.taskTimekeeper.baseCode.stats.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AllStatsTest {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	//TODO:: this
	//TODO:: test json makes sense
	
	@Test
	public void serializationTest() throws JsonProcessingException {
		AllStats stats = new AllStats();
		
		String statString = MAPPER.writeValueAsString(stats);
		AllStats deserilized = MAPPER.readValue(statString, AllStats.class);
		
		assertEquals(stats, deserilized);
	}
}