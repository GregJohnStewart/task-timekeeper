package com.gjs.taskTimekeeper.baseCode.core;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test to run through constructors of the various custom exceptions. Really just for code coverage
 * stats.
 */
public class ExceptionTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionTests.class);

    /** The different exceptions to test. */
    public static Collection exceptionsToTest() {
        return Arrays.asList(
                new Object[][] {
                    {TimeKeeperException.class},
                });
    }

    private static final Throwable TEST_THROWABLE = new Throwable("Test throwable message");
    private static final String TEST_MESSAGE = "TEST MESSAGE FOR EXCEPTION TESTING";

    @ParameterizedTest
    @MethodSource("exceptionsToTest")
    public void testExceptions(Class<? extends TimeKeeperException> curExceptionClass) throws Throwable {
        LOGGER.debug("Testing: {}", curExceptionClass.getName());
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
