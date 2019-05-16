package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimespanDoerTest extends ActionDoerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimespanDoerTest.class);
	public TimespanDoerTest() {
		super(KeeperObjectType.SPAN);
	}

	@Before
	public void before(){
		ActionDoer.resetDoers();
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(2));
		LOGGER.info("Finished setup of timespan doer test.");
	}

	@After
	public void after(){
		ActionDoer.resetDoers();
	}

	@Test
	public void add() {
		//TODO:: this
	}

	@Test
	public void edit() {
		//TODO:: this
	}

	@Test
	public void remove() {
		//TODO:: this
	}

	@Test
	public void view() {
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW));
	}

	@Test
	public void search() {
		//TODO:: this
	}

	@Test
	public void getViewHeaders() {
		//TODO:: this
	}

	@Test
	public void getViewRowEntries() {
		//TODO:: this
	}
}