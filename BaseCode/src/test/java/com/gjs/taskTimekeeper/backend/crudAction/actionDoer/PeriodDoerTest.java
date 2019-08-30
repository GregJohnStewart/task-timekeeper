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

import static org.junit.Assert.*;

public class PeriodDoerTest extends ActionDoerTest{

	public PeriodDoerTest() {
		super(KeeperObjectType.PERIOD);
	}

	@Before
	public void before(){
		ActionDoer.resetDoers();
	}

	@After
	public void after(){
		ActionDoer.resetDoers();
	}

	//<editor-fold desc="Adding Tests">
	@Test
	public void addSimple(){
		TimeManager manager = new TimeManager();

		assertTrue(ActionDoer.doObjAction(manager, this.getActionConfig(Action.ADD)));

		assertEquals(1, manager.getWorkPeriods().size());

		assertNull(ActionDoer.getSelectedWorkPeriod());
	}

	@Test
	public void addAndSelectSimple(){
		TimeManager manager = new TimeManager();

		assertTrue(ActionDoer.doObjAction(manager, this.getActionConfig(Action.ADD).setSelect(true)));

		assertEquals(1, manager.getWorkPeriods().size());

		assertEquals(manager.getWorkPeriods().first(), ActionDoer.getSelectedWorkPeriod());
	}

	@Test
	public void addWithAttributeSimple(){
		TimeManager manager = new TimeManager();

		assertTrue(ActionDoer.doObjAction(manager, this.getActionConfig(Action.ADD).setAttributeName("newAttName").setAttributeVal("val").setSelect(true)));

		assertEquals(1, manager.getWorkPeriods().size());

		WorkPeriod period = manager.getWorkPeriods().first();

		assertTrue(period.getAttributes().containsKey("newAttName"));
		assertEquals("val", period.getAttributes().get("newAttName"));
	}
	//</editor-fold>

	//<editor-fold desc="Editing Tests">
	@Test
	public void edit() {
		TimeManager manager = getTestManager();
		int selectedInd = 2;

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.EDIT).setNewAttributeName("new Att").setNewAttributeVal("New val")
			)
		);

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setSelect(true).setIndex(selectedInd));

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.EDIT).setNewAttributeName("new Att").setNewAttributeVal("New val")
			)
		);

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setIndex(selectedInd));
	}
	//</editor-fold>

	//<editor-fold desc="Removing Tests">
	@Test
	public void remove() {
		TimeManager manager = getTestManager();

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setIndex(2)
			)
		);

		assertEquals(1, manager.getWorkPeriods().size());

		//TODO:: better; before date, etc
	}
	//</editor-fold>

	//<editor-fold desc="View/Search Tests">
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
	//</editor-fold>

	//<editor-fold desc="Selecting">
	@Test
	public void selecting(){
		TimeManager manager = getTestManager();
		int selectedInd = 2;

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setSelect(true));

		assertNull(ActionDoer.getSelectedWorkPeriod());

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setSelect(true).setIndex(selectedInd));
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW));

		WorkPeriod selected = ActionDoer.getSelectedWorkPeriod();
		assertNotNull(selected);
		assertEquals(new PeriodDoer().search(manager, this.getActionConfig(Action.VIEW)).get(selectedInd - 1), selected);
	}

	@Test
	public void remembersSelected(){
		int selectedInd = 2;

		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW).setSelect(true));

		assertNull(ActionDoer.getSelectedWorkPeriod());

		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW).setSelect(true).setIndex(selectedInd));
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW));

		WorkPeriod selected = ActionDoer.getSelectedWorkPeriod();
		assertNotNull(selected);
		assertEquals(new PeriodDoer().search(getTestManager(), this.getActionConfig(Action.VIEW)).get(selectedInd - 1), selected);
	}
	//</editor-fold>
}