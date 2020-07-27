package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionDoerTest {
	
	@Test
	public void testActionDoerNullTimeManager() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			new TaskDoer(null);
		});
	}
	
	@Test
	public void testActionDoerNullOutputter() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			new TaskDoer(new TimeManager(), null);
		});
	}
	
	// <editor-fold desc="Attribute parsing tests">
	@Test
	public void parseAttributes() {
		Map<String, String> atts = ActionDoer.parseAttributes("hello,world");
		assertEquals(1, atts.size());
		assertTrue(atts.containsKey("hello"));
		assertEquals("world", atts.get("hello"));
		
		atts = ActionDoer.parseAttributes("hello,world;");
		assertEquals(1, atts.size());
		assertTrue(atts.containsKey("hello"));
		assertEquals("world", atts.get("hello"));
		
		atts = ActionDoer.parseAttributes("hello,world;key,val");
		assertEquals(2, atts.size());
		assertTrue(atts.containsKey("hello"));
		assertEquals("world", atts.get("hello"));
		assertTrue(atts.containsKey("key"));
		assertEquals("val", atts.get("key"));
		
		atts = ActionDoer.parseAttributes("EMPTY");
		assertEquals(0, atts.size());
	}
	
	@Test
	public void parseAttributesNullString() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			ActionDoer.parseAttributes(null);
		});
	}
	
	@Test
	public void parseAttributesEmptyString() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			ActionDoer.parseAttributes("");
		});
	}
	// </editor-fold>
}
