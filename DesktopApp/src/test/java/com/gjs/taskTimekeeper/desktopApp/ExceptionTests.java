package com.gjs.taskTimekeeper.desktopApp;

import com.gjs.taskTimekeeper.desktopApp.config.exception.ConfigKeyDoesNotExistException;
import com.gjs.taskTimekeeper.desktopApp.config.exception.ConfigurationException;
import com.gjs.taskTimekeeper.desktopApp.config.exception.SetReadOnlyPropertyException;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.DoExit;
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
public class ExceptionTests <T extends DesktopAppException> {
	
	/**
	 * The different exceptions to test.
	 */
	public static Collection exceptionsToTest() {
		return Arrays.asList(
			new Object[][]{
				{DesktopAppException.class},
				{DoExit.class},
				{ConfigurationException.class},
				{ConfigKeyDoesNotExistException.class},
				{SetReadOnlyPropertyException.class},
				});
	}
	
	private static final Throwable TEST_THROWABLE = new Throwable("Test throwable message");
	private static final String TEST_MESSAGE = "TEST MESSAGE FOR EXCEPTION TESTING";
	
	@ParameterizedTest
	@MethodSource("exceptionsToTest")
	public void testExceptions(Class<? extends T> curExceptionClass) throws Throwable {
		log.debug("Testing: {}", curExceptionClass.getName());
		Constructor<? extends T> constBase = curExceptionClass.getConstructor();
		Constructor<? extends T> constStr = curExceptionClass.getConstructor(String.class);
		Constructor<? extends T> constThro = curExceptionClass.getConstructor(Throwable.class);
		Constructor<? extends T> constStrThro =
			curExceptionClass.getConstructor(String.class, Throwable.class);
		Constructor<? extends T> constStrThroSupWritable =
			curExceptionClass.getConstructor(
				String.class, Throwable.class, boolean.class, boolean.class);
		
		T curException = constBase.newInstance();
		
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
