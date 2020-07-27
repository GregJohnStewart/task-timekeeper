package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceParsingException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class DataSourceFromStringTest {
	
	public static Stream<Arguments> data() {
		return Stream.of(
			Arguments.of("", DataSourceParsingException.class),
			Arguments.of("hello world", DataSourceParsingException.class),
			Arguments.of("hello/world", DataSourceParsingException.class),
			Arguments.of("/hello/world", DataSourceParsingException.class),
			Arguments.of("mailto:hello/world", DataSourceParsingException.class),
			Arguments.of("/hello/", DataSourceParsingException.class),
			Arguments.of("file://hello/world", FileDataSource.class),
			Arguments.of("file:/hello/world", FileDataSource.class),
			Arguments.of("file:hello/world", FileDataSource.class),
			Arguments.of("ftp:/hello/world", FtpDataSource.class)
		);
	}
	
	@ParameterizedTest
	@MethodSource("data")
	public void test(String sourceString, Class expectedSourceType) {
		if(Exception.class.isAssignableFrom(expectedSourceType)) {
			try {
				DataSource.fromString(sourceString);
				fail();
			} catch(Exception e) {
				assertEquals(expectedSourceType, e.getClass());
			}
		} else {
			DataSource source = DataSource.fromString(sourceString);
			assertEquals(expectedSourceType, source.getClass());
		}
	}
}
