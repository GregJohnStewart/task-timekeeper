package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.TimeManager;
import com.gjs.taskTimekeeper.baseCode.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.crudAction.KeeperObjectType;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpecialCommandTest extends ActionDoerTest {
	public SpecialCommandTest() {
		super(null);
	}

	//<editor-fold desc="Misc tests">
	@Test
	public void noValidCommand(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("")
			)
		);
		assertEquals(orig, manager);

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("some bad special command")
			)
		);
		assertEquals(orig, manager);
	}
	//</editor-fold>
	//<editor-fold desc="completeSpans tests">
	@Test
	public void completespanNoSelectedPeriod(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("completespans").setName(TASK_ONE_NAME)
			)
		);
		assertEquals(orig, manager);
	}
	@Test
	public void completespanCompletedSelectedPeriod(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(2));

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("completespans").setName(TASK_ONE_NAME)
			)
		);
		assertEquals(orig, manager);
	}
	@Test
	public void completeSpans(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("completespans").setName(TASK_ONE_NAME)
			)
		);
		assertNotEquals(orig, manager);
		//TODO:: verify
	}
	//</editor-fold>
	//<editor-fold desc="newspan tests">
	@Test
	public void newspanNoSelectedPeriod(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("newspan").setName(TASK_ONE_NAME)
			)
		);
		assertEquals(orig, manager);
	}
	@Test
	public void newspanNoTaskName(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("newspan")
			)
		);
		assertEquals(orig, manager);
	}
	@Test
	public void newspan(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("newspan").setName(TASK_ONE_NAME)
			)
		);
		assertNotEquals(orig, manager);
		//TODO:: verify
	}
	//</editor-fold>
	//<editor-fold desc="selectnewest tests">
	@Test
	public void selectnewestNoPeriods(){
		TimeManager manager = new TimeManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("selectnewest")
			)
		);
		assertEquals(orig, manager);

		assertNull(ActionDoer.getSelectedWorkPeriod());
	}
	@Test
	public void selectnewest(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(2));

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("selectnewest")
			)
		);
		assertEquals(orig, manager);
		assertNotNull(ActionDoer.getSelectedWorkPeriod());
		//TODO:: verify
	}
	//</editor-fold>
	//<editor-fold desc="newPeriod tests">
	@Test
	public void newPeriod(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				new ActionConfig().setSpecialAction("newPeriod")
			)
		);
		assertNotEquals(orig, manager);
		assertNotNull(ActionDoer.getSelectedWorkPeriod());
		//TODO:: verify
	}
	//</editor-fold>
}
