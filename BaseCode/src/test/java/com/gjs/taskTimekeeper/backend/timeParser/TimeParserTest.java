package com.gjs.taskTimekeeper.backend.timeParser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.junit.Assert.assertNotNull;

public class TimeParserTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeParserTest.class);

	@Test
	public void listParseFormats(){
		TimeParser.outputHelp();
	}

	@Test
	public void toConsoleString(){
		LOGGER.info(TimeParser.toOutputString(LocalDateTime.now()));
	}

	@Test
	public void parse() {
		//TODO:: make parameterized, test that times equal each other
		assertNotNull(TimeParser.parse("01:00"));
		assertNotNull(TimeParser.parse("NOW"));
		assertNotNull(TimeParser.parse("2019-05-12T16:28:06"));
		assertNotNull(TimeParser.parse("16:28:06"));
		assertNotNull(TimeParser.parse("2019-05-12"));

		assertNotNull(TimeParser.parse("1:05 PM"));
		assertNotNull(TimeParser.parse("01:05 PM"));
		assertNotNull(TimeParser.parse("13:05"));
		assertNotNull(TimeParser.parse("15/05 2019"));
		assertNotNull(TimeParser.parse("15/5 1:05 PM 2019"));
		assertNotNull(TimeParser.parse("15/5 13:05 2019"));
	}

	//TODO:: more testing
}