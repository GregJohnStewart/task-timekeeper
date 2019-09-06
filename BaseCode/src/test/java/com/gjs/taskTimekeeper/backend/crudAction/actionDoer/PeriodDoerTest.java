package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.Assert.*;

public class PeriodDoerTest extends ActionDoerTest{

	public PeriodDoerTest() {
		super(KeeperObjectType.PERIOD);
	}

	@Before
	public void before(){
		ActionDoer.setConsoleOutputLevel(OutputLevel.VERBOSE);
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

	@Test
	public void addWithExisting(){
		TimeManager manager = getTestManager();
		int origSize = manager.getWorkPeriods().size();

		assertTrue(ActionDoer.doObjAction(manager, this.getActionConfig(Action.ADD).setAttributeName("testAtt").setAttributeVal("theVal")));

		assertEquals(origSize + 1, manager.getWorkPeriods().size());

		assertNull(ActionDoer.getSelectedWorkPeriod());

		WorkPeriod period = manager.getWorkPeriods().last();

		assertTrue(period.getAttributes().containsKey("testAtt"));
		assertEquals("theVal", period.getAttributes().get("testAtt"));
	}
	//</editor-fold>

	//<editor-fold desc="Editing Tests">

	@Test
	public void editFailNoneSelected(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.EDIT).setAttributeName("new Att").setAttributeVal("New val")
			)
		);
		assertEquals(orig, manager);
	}

	@Test
	public void editAddAtt(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();
		int selectedInd = 2;

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setSelect(true).setIndex(selectedInd));

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.EDIT).setAttributeName("new Att").setAttributeVal("New val")
			)
		);

		assertNotEquals(orig, manager);

		WorkPeriod period = new PeriodDoer().search(manager, getActionConfig(Action.VIEW)).get(1);

		assertTrue(period.getAttributes().containsKey("new Att"));
		assertEquals("New val", period.getAttributes().get("new Att"));
	}

	@Test
	public void editChangeAtt(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();
		int selectedInd = 1;

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setSelect(true).setIndex(selectedInd));

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.EDIT).setAttributeName("attOne").setAttributeVal("New val")
			)
		);

		assertNotEquals(orig, manager);

		WorkPeriod period = new PeriodDoer().search(manager, getActionConfig(Action.VIEW)).get(0);

		assertTrue(period.getAttributes().containsKey("attOne"));
		assertEquals("New val", period.getAttributes().get("attOne"));
	}

	@Test
	public void editRemoveAtt(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();
		int selectedInd = 1;

		ActionDoer.doObjAction(manager, this.getActionConfig(Action.VIEW).setSelect(true).setIndex(selectedInd));

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.EDIT).setAttributeName("attOne")
			)
		);

		assertNotEquals(orig, manager);

		WorkPeriod period = new PeriodDoer().search(manager, getActionConfig(Action.VIEW)).get(0);

		assertFalse(period.getAttributes().containsKey("attOne"));
	}
	//</editor-fold>

	//<editor-fold desc="Removing Tests">
	@Test
	public void removeNoPeriods(){
		TimeManager manager = new TimeManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setIndex(1)
			)
		);

		assertEquals(orig, manager);
	}

	@Test
	public void removeOneBadIndex(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setIndex(0)
			)
		);
		assertEquals(orig, manager);
	}

	@Test
	public void removeNoSpecify(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE)
			)
		);
		assertEquals(orig, manager);
	}

	@Test
	public void removeAtIndex(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();
		int selectedInd = 1;

		WorkPeriod period = new PeriodDoer().search(manager, getActionConfig(Action.VIEW)).get(selectedInd - 1);

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setIndex(selectedInd)
			)
		);

		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
	}

	@Test
	public void removeBeforeBadDatetime(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setBefore("bad dateTime")
			)
		);

		assertEquals(orig, manager);
	}
	@Test
	public void removeAfterBadDatetime(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setAfter("bad dateTime")
			)
		);

		assertEquals(orig, manager);
	}

	@Test
	public void removeBeforeAfterAfter(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setBefore(TimeParser.toOutputString(nowPlusFive)).setAfter(TimeParser.toOutputString(nowPlusFifteen))
			)
		);

		assertEquals(orig, manager);
	}

	@Test
	public void removeBefore(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();
		int selectedInd = 1;

		WorkPeriod period = new PeriodDoer().search(manager, getActionConfig(Action.VIEW)).get(selectedInd - 1);

		LocalDateTime dateTime = nowPlusFifteen.plusMinutes(2);

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setBefore(TimeParser.toOutputString(dateTime))
			)
		);

		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
		assertEquals(1, manager.getWorkPeriods().size());
		//TODO:: assert that the end set contains what it should
	}

	@Test
	public void removeAfter(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();
		int selectedInd = 2;

		WorkPeriod period = new PeriodDoer().search(manager, getActionConfig(Action.VIEW)).get(selectedInd - 1);

		LocalDateTime dateTime = nowPlusFifteen.plusMinutes(2);

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE).setAfter(TimeParser.toOutputString(dateTime))
			)
		);

		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
		assertEquals(1, manager.getWorkPeriods().size());
		//TODO:: assert that the end set contains what it should
	}

	@Test
	public void removeBetween(){
		TimeManager manager = getTestManager();
		TimeManager orig = manager.clone();
		int selectedInd = 2;

		WorkPeriod period = new PeriodDoer().search(manager, getActionConfig(Action.VIEW)).get(selectedInd - 1);

		LocalDateTime after = nowPlusFifteen.plusMinutes(2);
		LocalDateTime before = nowPlusHourFifteen.plusMinutes(2);

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE)
					.setBefore(TimeParser.toOutputString(before))
					.setAfter(TimeParser.toOutputString(after))
			)
		);

		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
		assertEquals(1, manager.getWorkPeriods().size());
		//TODO:: assert that the end set contains what it should
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