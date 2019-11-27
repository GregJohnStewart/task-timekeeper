package com.gjs.taskTimekeeper.baseCode.stats.results;

import static org.junit.Assert.*;

import org.junit.Test;

public class PercentResultsTest {

    private PercentResults<String> results = new PercentResults<>();

    @Test
    public void testSetValue() {
        assertTrue(this.results.getPercentages().isEmpty());

        assertNull(this.results.setValue("hello", 1.0));

        assertFalse(this.results.getPercentages().isEmpty());
        assertEquals((Double) 100.0, this.results.getPercentages().get("hello"));

        assertEquals((Double) 100.0, this.results.setValue("hello", 1.0));

        assertFalse(this.results.getPercentages().isEmpty());
        assertEquals((Double) 100.0, this.results.getPercentages().get("hello"));
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullValue() {
        this.results.setValue("hello", null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullObject() {
        this.results.setValue(null, 1.0);
    }

    @Test
    public void testRemove() {
        this.results.setValue("hello", 1.0);

        assertEquals((Double) 1.0, this.results.remove("hello"));

        assertTrue(this.results.getPercentages().isEmpty());

        assertNull(this.results.remove("hello"));
    }

    @Test
    public void testGetObjectsNumEntries() {
        assertEquals(0, this.results.getNumEntries());
        assertTrue(this.results.getObjects().isEmpty());

        this.results.setValue("hello", 1.0);

        assertEquals(1, this.results.getNumEntries());
        assertFalse(this.results.getObjects().isEmpty());
        assertTrue(this.results.getObjects().contains("hello"));
    }

    @Test
    public void percentageTest() {
        this.results.setValue("1", 1.0);

        assertEquals((Double) 100.0, this.results.getPercentages().get("1"));

        this.results.setValue("2", 1.0);

        assertEquals((Double) 50.0, this.results.getPercentages().get("1"));
        assertEquals((Double) 50.0, this.results.getPercentages().get("2"));

        this.results.setValue("3", 1.0);

        assertEquals((Double) 33.33333333333333, this.results.getPercentages().get("1"));
        assertEquals((Double) 33.33333333333333, this.results.getPercentages().get("2"));
        assertEquals((Double) 33.33333333333333, this.results.getPercentages().get("3"));

        this.results.setValue("4", 1.0);

        assertEquals((Double) 25.0, this.results.getPercentages().get("1"));
        assertEquals((Double) 25.0, this.results.getPercentages().get("2"));
        assertEquals((Double) 25.0, this.results.getPercentages().get("3"));
        assertEquals((Double) 25.0, this.results.getPercentages().get("4"));

        this.results.setValue("5", 1.0);
        this.results.setValue("6", 5.0);

        assertEquals((Double) 10.0, this.results.getPercentages().get("1"));
        assertEquals((Double) 10.0, this.results.getPercentages().get("2"));
        assertEquals((Double) 10.0, this.results.getPercentages().get("3"));
        assertEquals((Double) 10.0, this.results.getPercentages().get("4"));
        assertEquals((Double) 10.0, this.results.getPercentages().get("5"));
        assertEquals((Double) 50.0, this.results.getPercentages().get("6"));
    }
}
