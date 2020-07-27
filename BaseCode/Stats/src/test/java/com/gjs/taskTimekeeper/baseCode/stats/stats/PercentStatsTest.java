package com.gjs.taskTimekeeper.baseCode.stats.stats;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TODO:: update for value strings
 */
public class PercentStatsTest {
	private PercentStats<String> results = new PercentStats<>();
	
	@Test
	public void testSetValue() {
		assertTrue(this.results.getPercentages().isEmpty());
		
		assertNull(this.results.setValue("hello", 1));
		
		assertFalse(this.results.getPercentages().isEmpty());
		assertEquals((Double)100.0, this.results.getPercentages().get("hello"));
		
		assertEquals((Double)100.0, this.results.setValue("hello", 1.0));
		
		assertFalse(this.results.getPercentages().isEmpty());
		assertEquals((Double)100.0, this.results.getPercentages().get("hello"));
	}
	
	@Test
	public void testSetNullValue() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			this.results.setValue("hello", null);
		});
	}
	
	@Test
	public void testSetNullObject() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			this.results.setValue(null, 1.0);
		});
	}
	
	@Test
	public void testRemove() {
		this.results.setValue("hello", 1.0);
		
		assertEquals((Double)1.0, this.results.remove("hello"));
		
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
		
		assertEquals((Double)100.0, this.results.getPercentages().get("1"));
		
		this.results.setValue("2", 1.0);
		
		assertEquals((Double)50.0, this.results.getPercentages().get("1"));
		assertEquals((Double)50.0, this.results.getPercentages().get("2"));
		
		this.results.setValue("3", 1.0);
		
		assertEquals((Double)33.33333333333333, this.results.getPercentages().get("1"));
		assertEquals((Double)33.33333333333333, this.results.getPercentages().get("2"));
		assertEquals((Double)33.33333333333333, this.results.getPercentages().get("3"));
		
		this.results.setValue("4", 1.0);
		
		assertEquals((Double)25.0, this.results.getPercentages().get("1"));
		assertEquals((Double)25.0, this.results.getPercentages().get("2"));
		assertEquals((Double)25.0, this.results.getPercentages().get("3"));
		assertEquals((Double)25.0, this.results.getPercentages().get("4"));
		
		this.results.setValue("5", 1.0);
		this.results.setValue("6", 5.0);
		
		assertEquals((Double)10.0, this.results.getPercentages().get("1"));
		assertEquals((Double)10.0, this.results.getPercentages().get("2"));
		assertEquals((Double)10.0, this.results.getPercentages().get("3"));
		assertEquals((Double)10.0, this.results.getPercentages().get("4"));
		assertEquals((Double)10.0, this.results.getPercentages().get("5"));
		assertEquals((Double)50.0, this.results.getPercentages().get("6"));
	}
	
	@Test
	public void setValues() {
		this.results.setValues(
			new HashMap<String, Number>() {
				{
					put("hello", 10.0);
					put("world", 90.0);
				}
			});
		
		assertEquals(2, this.results.getNumEntries());
		assertFalse(this.results.getObjects().isEmpty());
		assertTrue(this.results.getObjects().contains("hello"));
		assertEquals((Double)10.0, this.results.getPercentages().get("hello"));
		assertTrue(this.results.getObjects().contains("world"));
		assertEquals((Double)90.0, this.results.getPercentages().get("world"));
		
		this.results.setValues(
			new HashMap<String, Number>() {
				{
					put("hewwo", 25.0);
					put("warld", 75.0);
				}
			});
		
		assertEquals(2, this.results.getNumEntries());
		assertFalse(this.results.getObjects().isEmpty());
		assertTrue(this.results.getObjects().contains("hewwo"));
		assertEquals((Double)25.0, this.results.getPercentages().get("hewwo"));
		assertTrue(this.results.getObjects().contains("warld"));
		assertEquals((Double)75.0, this.results.getPercentages().get("warld"));
	}
	
	@Test
	public void setValuesNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			this.results.setValues(null);
		});
	}
	
	@Test
	public void addOrOverrideValues() {
		this.results.addOrOverrideValues(
			new HashMap<String, Number>() {
				{
					put("hello", 15.0);
					put("hewwo", 5.0);
					put("world", 80.0);
				}
			});
		
		assertEquals(3, this.results.getNumEntries());
		assertFalse(this.results.getObjects().isEmpty());
		assertTrue(this.results.getObjects().contains("hello"));
		assertEquals((Double)15.0, this.results.getPercentages().get("hello"));
		assertTrue(this.results.getObjects().contains("hewwo"));
		assertEquals((Double)5.0, this.results.getPercentages().get("hewwo"));
		assertTrue(this.results.getObjects().contains("world"));
		assertEquals((Double)80.0, this.results.getPercentages().get("world"));
		
		this.results.addOrOverrideValues(
			new HashMap<String, Number>() {
				{
					put("world", 20.0);
					put("warld", 60.0);
				}
			});
		
		assertEquals(4, this.results.getNumEntries());
		assertFalse(this.results.getObjects().isEmpty());
		assertTrue(this.results.getObjects().contains("hello"));
		assertEquals((Double)15.0, this.results.getPercentages().get("hello"));
		assertTrue(this.results.getObjects().contains("hewwo"));
		assertEquals((Double)5.0, this.results.getPercentages().get("hewwo"));
		assertTrue(this.results.getObjects().contains("world"));
		assertEquals((Double)20.0, this.results.getPercentages().get("world"));
		assertTrue(this.results.getObjects().contains("warld"));
		assertEquals((Double)60.0, this.results.getPercentages().get("warld"));
	}
	
	@Test
	public void addOrOverrideValuesNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			this.results.addOrOverrideValues(null);
		});
	}
	
	@Test
	public void constructWithMap() {
		this.results =
			new PercentStats<>(
				new HashMap<String, Number>() {
					{
						put("hello", 10.0);
						put("world", 90.0);
					}
				});
		
		assertEquals(2, this.results.getNumEntries());
		assertFalse(this.results.getObjects().isEmpty());
		assertTrue(this.results.getObjects().contains("hello"));
		assertEquals((Double)10.0, this.results.getPercentages().get("hello"));
		assertTrue(this.results.getObjects().contains("world"));
		assertEquals((Double)90.0, this.results.getPercentages().get("world"));
	}
	
	@Test
	public void constructWithNullMap() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new PercentStats<>(null);
		});
	}
	
	@Test
	public void equalsHashCode() {
		assertEquals(this.results, new PercentStats<String>());
		
		this.results.hashCode();
	}
}
