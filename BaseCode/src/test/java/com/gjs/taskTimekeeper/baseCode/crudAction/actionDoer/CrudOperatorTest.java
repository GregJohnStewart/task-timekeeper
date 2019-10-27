package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.baseCode.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSourceTest;
import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.utils.Outputter;
import org.junit.Test;

public class CrudOperatorTest extends DataSourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void setOperatorNullTest() {
        CrudOperator op = new CrudOperator(new TimeManager());

        op.setOutputter(null);
    }

    @Test
    public void setOutputterTest() {
        CrudOperator op = new CrudOperator(new TimeManager());

        op.setOutputter(new Outputter());
        // TODO: verify new outputter was set
    }

    @Test
    public void getDoersTest() {
        TimeManager manager = new TimeManager();
        Outputter outputter = new Outputter();
        CrudOperator op = new CrudOperator(manager, outputter);

        assertSame(outputter, op.getOutputter());

        assertNotNull(op.getTaskDoer());
        assertSame(manager, op.getTaskDoer().getTimeManager());
        assertNotNull(op.getTaskDoer().getOutputter());
        //        assertSame(outputter, op.getTaskDoer().getOutputter());
        assertNotNull(op.getTimespanDoer());
        assertSame(manager, op.getTimespanDoer().getTimeManager());
        assertNotNull(op.getTimespanDoer().getOutputter());
        //        assertSame(outputter, op.getTimespanDoer().getOutputter());
        assertNotNull(op.getWorkPeriodDoer());
        assertSame(manager, op.getWorkPeriodDoer().getTimeManager());
        assertNotNull(op.getWorkPeriodDoer().getOutputter());
        //        assertSame(outputter, op.getWorkPeriodDoer().getOutputter());
        assertNotNull(op.getSpecialDoer());
        assertSame(manager, op.getSpecialDoer().getTimeManager());
        assertNotNull(op.getSpecialDoer().getOutputter());
        //        assertSame(outputter, op.getSpecialDoer().getOutputter());
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
