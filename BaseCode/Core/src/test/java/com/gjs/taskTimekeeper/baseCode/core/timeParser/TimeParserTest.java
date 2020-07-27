package com.gjs.taskTimekeeper.baseCode.core.timeParser;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class TimeParserTest {
	
	@Test
	public void listParseFormats() {
		TimeParser.outputHelp();
	}
	
	@Test
	public void toConsoleString() {
		log.info(TimeParser.toOutputString(LocalDateTime.now()));
	}
	
	@Test
	public void durationToString() {
		Duration duration = Duration.ofHours(1).plusMinutes(15);
		
		assertEquals("1:15", TimeParser.toDurationString(duration));
		
		duration = Duration.ofHours(25).plusMinutes(70);
		
		assertEquals("26:10", TimeParser.toDurationString(duration));
	}
	
	@Test
	public void parse() {
		// TODO:: make parameterized, test that times equal each other
		assertNotNull(TimeParser.parse("01:00"));
		assertNotNull(TimeParser.parse("2019-05-12T16:28:06"));
		assertNotNull(TimeParser.parse("16:28:06"));
		assertNotNull(TimeParser.parse("2019-05-12"));
		
		assertNotNull(TimeParser.parse("1:05 PM"));
		assertNotNull(TimeParser.parse("01:05 PM"));
		assertNotNull(TimeParser.parse("13:05"));
		assertNotNull(TimeParser.parse("15/05 2019"));
		assertNotNull(TimeParser.parse("15/5 1:05 PM 2019"));
		assertNotNull(TimeParser.parse("15/5 13:05 2019"));
		
		assertNotNull(TimeParser.parse("NOW"));
		assertNotNull(TimeParser.parse("now"));
		assertNotNull(TimeParser.parse("this week"));
		assertNotNull(TimeParser.parse("last_week"));
	}
}
