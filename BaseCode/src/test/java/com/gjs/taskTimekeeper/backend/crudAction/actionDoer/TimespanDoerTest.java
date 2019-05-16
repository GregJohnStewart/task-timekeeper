package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class TimespanDoerTest extends ActionDoerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimespanDoerTest.class);
	public TimespanDoerTest() {
		super(KeeperObjectType.SPAN);
	}

	private TimeManager manager = getTestManager();

	@Before
	public void before(){
		ActionDoer.resetDoers();
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(2));
		LOGGER.info("Finished setup of timespan doer test.");
	}

	@After
	public void after(){
		ActionDoer.resetDoers();
	}

	@Test
	public void addNoneSelected() {
		ActionDoer.resetDoers();
		assertFalse(
			ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.ADD).setName(((Task)(getTestManager().getTasks().toArray()[0])).getName()))
		);
	}

	@Test
	public void addNoTask() {
		assertFalse(
			ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.ADD))
		);
	}

	@Test
	public void add() {
		int prevCount = ActionDoer.getSelectedWorkPeriod().getNumTimespans();
		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.ADD)
					.setName(((Task)(getTestManager().getTasks().toArray()[0])).getName())
			)
		);

		assertEquals(prevCount + 1, ActionDoer.getSelectedWorkPeriod().getNumTimespans());

		//ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW));
	}

	@Test
	public void edit() {
		WorkPeriod period = ActionDoer.getSelectedWorkPeriod();

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW));
		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(2)
					.setBefore(TimeParser.toOutputString(nowPlusHourFifteen.plusMinutes(5)))
			)
		);

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW));
	}

	@Test
	public void remove() {
		TimeManager manager = getTestManager();
		int prevCount = ActionDoer.getSelectedWorkPeriod().getNumTimespans();
		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE)
					.setIndex(1)
			)
		);

		assertEquals(prevCount - 1, ActionDoer.getSelectedWorkPeriod().getNumTimespans());
	}

	@Test
	public void view() {
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW));
	}
}