package com.gjs.taskTimekeeper.baseCode;

import static org.junit.Assert.assertEquals;

import com.gjs.taskTimekeeper.baseCode.utils.ObjectMapperUtilities;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerSerializationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerSerializationTest.class);

    private TimeManager manager;

    @Before
    public void setup() {
        manager = new TimeManager();
        Task task =
                new Task(
                        "Test Task",
                        new HashMap<String, String>() {
                            {
                                put("att", "val");
                            }
                        });
        manager.addTask(task);

        manager.addWorkPeriod(
                new WorkPeriod(
                        Stream.of(
                                        new Timespan(
                                                task,
                                                LocalDateTime.now(),
                                                LocalDateTime.now().plusMinutes(5)),
                                        new Timespan(
                                                task,
                                                LocalDateTime.now().plusMinutes(10),
                                                LocalDateTime.now().plusMinutes(15)))
                                .collect(Collectors.toList()),
                        new HashMap<String, String>() {
                            {
                                put("att2", "value");
                            }
                        }));
        manager.addWorkPeriod(
                new WorkPeriod(
                        Stream.of(
                                        new Timespan(
                                                task,
                                                LocalDateTime.now().plusMinutes(20),
                                                LocalDateTime.now().plusMinutes(25)),
                                        new Timespan(
                                                task,
                                                LocalDateTime.now().plusMinutes(30),
                                                LocalDateTime.now().plusMinutes(35)))
                                .collect(Collectors.toList()),
                        new HashMap<String, String>() {
                            {
                                put("att3", "value2");
                            }
                        }));
    }

    @Test
    public void testDeserialization() throws IOException {
        String output = ObjectMapperUtilities.getDefaultMapper().writeValueAsString(this.manager);

        LOGGER.debug("Serialized time manager: {}", output);

        TimeManager deserialized =
                ObjectMapperUtilities.getDefaultMapper().readValue(output, TimeManager.class);

        assertEquals(this.manager, deserialized);
    }
}
