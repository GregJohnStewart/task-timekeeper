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
		assertNotNull(TimeParser.parse("01:00"));
		assertNotNull(TimeParser.parse("NOW"));
		assertNotNull(TimeParser.parse("2019-05-12T16:28:06"));
		assertNotNull(TimeParser.parse("16:28:06"));
		assertNotNull(TimeParser.parse("2019-05-12"));
	}

	//TODO:: more testing
}