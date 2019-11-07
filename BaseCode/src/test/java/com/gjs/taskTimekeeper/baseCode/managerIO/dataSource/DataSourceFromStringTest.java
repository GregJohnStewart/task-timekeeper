package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import static org.junit.Assert.assertEquals;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceParsingException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class DataSourceFromStringTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFromStringTest.class);

    private final String sourceString;
    private final Class expectedSourceType;

    public DataSourceFromStringTest(String sourceString, Class expectedSourceType) {
        this.sourceString = sourceString;
        this.expectedSourceType = expectedSourceType;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] {
                    {"", DataSourceParsingException.class},
                    {"hello world", DataSourceParsingException.class},
                    {"hello/world", DataSourceParsingException.class},
                    {"/hello/world", DataSourceParsingException.class},
                    {"mailto:hello/world", DataSourceParsingException.class},
                    {"/hello/", DataSourceParsingException.class},
                    {"file://hello/world", FileDataSource.class},
                    {"file:/hello/world", FileDataSource.class},
                    {"file:hello/world", FileDataSource.class},
                });
    }

    @Test
    public void test() {
        if (Exception.class.isAssignableFrom(this.expectedSourceType)) {
            try {
                DataSource.fromString(this.sourceString);
                Assert.fail();
            } catch (Exception e) {
                assertEquals(expectedSourceType, e.getClass());
            }
        } else {
            DataSource source = DataSource.fromString(this.sourceString);
            assertEquals(expectedSourceType, source.getClass());
        }
    }
}
