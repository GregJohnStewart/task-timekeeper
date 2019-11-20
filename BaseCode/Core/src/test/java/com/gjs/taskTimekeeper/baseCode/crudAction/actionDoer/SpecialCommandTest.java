package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SpecialCommandTest extends ActionDoerExtendingTest {
    public SpecialCommandTest() {
        super(null);
    }

    // <editor-fold desc="Misc tests">
    @Test
    public void noValidCommand() {
        assertFalse(manager.doCrudAction(new ActionConfig().setSpecialAction("")));
        assertEquals(orig, manager);

        assertFalse(
                manager.doCrudAction(
                        new ActionConfig().setSpecialAction("some bad special command")));
        assertEquals(orig, manager);
    }
    // </editor-fold>
    // <editor-fold desc="completeSpans tests">
    @Test
    public void completespanNoSelectedPeriod() {
        assertFalse(manager.doCrudAction(new ActionConfig().setSpecialAction("completespans")));
        assertEquals(orig, manager);
    }

    @Test
    public void completespanCompletedSelectedPeriod() {
        this.selectPeriodAt(2);

        assertFalse(manager.doCrudAction(new ActionConfig().setSpecialAction("completespans")));
        assertEquals(orig, manager);
    }

    @Test
    public void completeSpans() {
        this.selectPeriodAt(1);

        assertTrue(manager.doCrudAction(new ActionConfig().setSpecialAction("completespans")));
        assertNotEquals(orig, manager);
        // TODO:: verify
    }
    // </editor-fold>
    // <editor-fold desc="newspan tests">
    @Test
    public void newspanNoSelectedPeriod() {
        assertFalse(
                manager.doCrudAction(
                        new ActionConfig().setSpecialAction("newspan").setName(TASK_ONE_NAME)));
        assertEquals(orig, manager);
    }

    @Test
    public void newspanNoTaskName() {
        this.selectPeriodAt(1);

        assertFalse(manager.doCrudAction(new ActionConfig().setSpecialAction("newspan")));
        assertEquals(orig, manager);
    }

    @Test
    public void newspan() {
        this.selectPeriodAt(1);

        assertTrue(
                manager.doCrudAction(
                        new ActionConfig().setSpecialAction("newspan").setName(TASK_ONE_NAME)));
        assertNotEquals(orig, manager);
        // TODO:: verify
    }
    // </editor-fold>
    // <editor-fold desc="selectnewest tests">
    @Test
    public void selectNewestNoPeriods() {
        this.manager = new TimeManager();
        this.updateOrig();

        assertFalse(manager.doCrudAction(new ActionConfig().setSpecialAction("selectnewest")));
        assertEquals(orig, manager);

        assertNull(manager.getCrudOperator().getSelectedWorkPeriod());
    }

    @Test
    public void selectnewest() {
        this.selectPeriodAt(2);

        assertFalse(manager.doCrudAction(new ActionConfig().setSpecialAction("selectnewest")));
        assertEquals(orig, manager);
        assertNotNull(manager.getCrudOperator().getSelectedWorkPeriod());
        // TODO:: verify
    }
    // </editor-fold>
    // <editor-fold desc="newPeriod tests">
    @Test
    public void newPeriod() {
        this.selectPeriodAt(1);

        assertTrue(manager.doCrudAction(new ActionConfig().setSpecialAction("newPeriod")));
        assertNotEquals(orig, manager);
        assertNotNull(manager.getCrudOperator().getSelectedWorkPeriod());
        // TODO:: verify
    }
    // </editor-fold>
}
