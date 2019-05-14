package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class PeriodDoerTest extends ActionDoerTest{

	public PeriodDoerTest() {
		super(KeeperObjectType.PERIOD);
	}

	@Test
	public void add() {
		TimeManager manager = getTestManager();
	}

	@Before
	public void before(){
		ActionDoer.resetDoers();
	}

	@After
	public void after(){
		ActionDoer.resetDoers();
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
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW).setIndex(1));

	}

	@Test
	public void search() {
		TimeManager manager = getTestManager();
		ActionConfig config = this.getActionConfig(Action.VIEW);

		Collection<WorkPeriod> results = new PeriodDoer().search(manager, config);
		//TODO:: this, but better
	}

	@Test
	public void selecting(){
		//TODO:: this
	}

	@Test
	public void remembersSelected(){
		//TODO:: this
	}
}