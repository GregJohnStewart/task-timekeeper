package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSourceTest;
import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import java.util.Map;
import org.junit.Test;

public class ActionDoerTest extends DataSourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void testActionDoerNullTimeManager() {
        new TaskDoer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActionDoerNullOutputter() {
        new TaskDoer(new TimeManager(), null);
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

    @Test(expected = IllegalArgumentException.class)
    public void parseAttributesNullString() {
        ActionDoer.parseAttributes(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseAttributesEmptyString() {
        ActionDoer.parseAttributes("");
    }
    // </editor-fold>
}
