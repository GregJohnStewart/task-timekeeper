package com.gjs.taskTimekeeper.baseCode.stats;

import com.gjs.taskTimekeeper.baseCode.core.TimeKeeperException;
import com.gjs.taskTimekeeper.baseCode.stats.processor.StatProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test to run through constructors of the various custom exceptions. Really just for code coverage
 * stats.
 */
@Slf4j
public class ExceptionTests {
	
	/**
	 * The different exceptions to test.
	 */
	
	public static Collection exceptionsToTest() {
		return Arrays.asList(
			new Object[][]{
				{TimeManagerStatsException.class},
				{StatProcessingException.class},
				});
	}
	
	private static final Throwable TEST_THROWABLE = new Throwable("Test throwable message");
	private static final String TEST_MESSAGE = "TEST MESSAGE FOR EXCEPTION TESTING";
	
	@ParameterizedTest
	@MethodSource("exceptionsToTest")
	public void testExceptions(Class<? extends TimeKeeperException> curExceptionClass) throws Throwable {
		log.debug("Testing: {}", curExceptionClass.getName());
		Constructor<? extends TimeKeeperException> constBase = curExceptionClass.getConstructor();
		Constructor<? extends TimeKeeperException> constStr =
			curExceptionClass.getConstructor(String.class);
		Constructor<? extends TimeKeeperException> constThro =
			curExceptionClass.getConstructor(Throwable.class);
		Constructor<? extends TimeKeeperException> constStrThro =
			curExceptionClass.getConstructor(String.class, Throwable.class);
		Constructor<? extends TimeKeeperException> constStrThroSupWritable =
			curExceptionClass.getConstructor(
				String.class, Throwable.class, boolean.class, boolean.class);
		
		TimeKeeperException curException = constBase.newInstance();
		
		curException = constStr.newInstance(TEST_MESSAGE);
		assertEquals(TEST_MESSAGE, curException.getMessage());
		
		curException = constThro.newInstance(TEST_THROWABLE);
		assertEquals(TEST_THROWABLE, curException.getCause());
		
		curException = constStrThro.newInstance(TEST_MESSAGE, TEST_THROWABLE);
		assertEquals(TEST_MESSAGE, curException.getMessage());
		assertEquals(TEST_THROWABLE, curException.getCause());
		
		curException =
			constStrThroSupWritable.newInstance(TEST_MESSAGE, TEST_THROWABLE, false, false);
		assertEquals(TEST_MESSAGE, curException.getMessage());
		assertEquals(TEST_THROWABLE, curException.getCause());
	}
}
