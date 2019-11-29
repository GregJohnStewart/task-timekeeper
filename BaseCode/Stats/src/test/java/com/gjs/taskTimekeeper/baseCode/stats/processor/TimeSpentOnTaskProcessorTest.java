package com.gjs.taskTimekeeper.baseCode.stats.processor;

import org.junit.Test;

public class TimeSpentOnTaskProcessorTest extends StatProcessorTest<TimeSpentOnTaskProcessor> {

    @Override
    public void setupProcessor() {
        this.processor = new TimeSpentOnTaskProcessor();
    }

    @Test
    public void process() {
        // TODO:: make super class with appropriate canned data setup
    }
}
