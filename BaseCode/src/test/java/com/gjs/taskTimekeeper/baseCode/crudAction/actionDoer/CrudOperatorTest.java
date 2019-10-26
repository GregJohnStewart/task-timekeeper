package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.baseCode.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSourceTest;
import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import org.junit.Test;

public class CrudOperatorTest extends DataSourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void setOperatorNullTest() {
        CrudOperator op = new CrudOperator(new TimeManager());

        op.setOutputter(null);
    }

    @Test
    public void getDoersTest() {
        TimeManager manager = new TimeManager();
        CrudOperator op = new CrudOperator(manager);

        assertNotNull(op.getTaskDoer());
        assertSame(manager, op.getTaskDoer().getTimeManager());
        assertNotNull(op.getTimespanDoer());
        assertSame(manager, op.getTimespanDoer().getTimeManager());
        assertNotNull(op.getWorkPeriodDoer());
        assertSame(manager, op.getWorkPeriodDoer().getTimeManager());
        assertNotNull(op.getSpecialDoer());
        assertSame(manager, op.getSpecialDoer().getTimeManager());
    }

    @Test
    public void getDoersWithObjectTypeTest() {
        TimeManager manager = new TimeManager();
        CrudOperator op = new CrudOperator(manager);

        for (KeeperObjectType type : KeeperObjectType.values()) {
            ActionDoer doer = op.getActionDoer(type);

            assertNotNull(doer);
            assertSame(manager, doer.getTimeManager());
        }
    }

    @Test
    public void doActionNullAction() {
        assertFalse(new CrudOperator(new TimeManager()).doObjAction(new ActionConfig()));
    }

    @Test
    public void doActionNullObjectType() {
        assertFalse(
                new CrudOperator(new TimeManager())
                        .doObjAction(new ActionConfig().setAction(Action.ADD)));
    }
}
