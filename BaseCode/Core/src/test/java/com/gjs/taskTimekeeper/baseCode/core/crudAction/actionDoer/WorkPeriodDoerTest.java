package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class WorkPeriodDoerTest extends ActionDoerExtendingTest {
	
	public WorkPeriodDoerTest() {
		super(KeeperObjectType.PERIOD);
	}
	
	@Test
	public void isSelectedTest() {
		int selectedInd = 2;
		this.selectPeriodAt(selectedInd);
		
		WorkPeriod selected = this.manager.getCrudOperator().getSelectedWorkPeriod();
		WorkPeriod notSelected = this.manager.getCrudOperator().getWorkPeriodDoer().search().get(0);
		
		assertTrue(this.manager.getCrudOperator().getWorkPeriodDoer().isSelected(selected));
		assertFalse(this.manager.getCrudOperator().getWorkPeriodDoer().isSelected(notSelected));
	}
	
	// <editor-fold desc="Adding Tests">
	@Test
	public void addSimple() {
		int beforeCount = manager.getWorkPeriods().size();
		
		assertTrue(manager.doCrudAction(this.getActionConfig(Action.ADD)));
		assertNotEquals(manager, orig);
		assertEquals(beforeCount + 1, manager.getWorkPeriods().size());
		
		assertNull(manager.getCrudOperator().getSelectedWorkPeriod());
	}
	
	@Test
	public void addAndSelectSimple() {
		TimeManager manager = new TimeManager();
		
		assertTrue(manager.doCrudAction(this.getActionConfig(Action.ADD).setSelect(true)));
		
		assertEquals(1, manager.getWorkPeriods().size());
		
		assertEquals(
			manager.getWorkPeriods().first(),
			manager.getCrudOperator().getSelectedWorkPeriod()
		);
	}
	
	@Test
	public void addWithAttributeSimple() {
		this.manager = new TimeManager();
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setAttributeName("newAttName")
					.setAttributeVal("val")
					.setSelect(true)));
		
		assertEquals(1, manager.getWorkPeriods().size());
		
		WorkPeriod period = manager.getWorkPeriods().first();
		
		assertTrue(period.getAttributes().containsKey("newAttName"));
		assertEquals("val", period.getAttributes().get("newAttName"));
	}
	
	@Test
	public void addWithAttributes() {
		TimeManager manager = new TimeManager();
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setAttributes("attOne,valOne;attTwo,valTwo;")
					.setSelect(true)));
		
		assertEquals(1, manager.getWorkPeriods().size());
		
		WorkPeriod period = manager.getWorkPeriods().first();
		
		assertEquals("valOne", period.getAttributes().get("attOne"));
		assertEquals("valTwo", period.getAttributes().get("attTwo"));
	}
	
	@Test
	public void addWithAttAndAttributes() {
		TimeManager manager = new TimeManager();
		
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setAttributeName("att")
					.setAttributeVal("val")
					.setAttributes("attOne,valOne;attTwo,valTwo;")
					.setSelect(true)));
		assertEquals(this.orig, this.manager);
	}
	
	@Test
	public void addWithBadAttributes() {
		TimeManager manager = new TimeManager();
		
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD).setAttributes(" ").setSelect(true)));
		assertEquals(this.orig, this.manager);
	}
	
	@Test
	public void addWithExisting() {
		int origSize = manager.getWorkPeriods().size();
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setAttributeName("testAtt")
					.setAttributeVal("theVal")));
		
		assertEquals(origSize + 1, manager.getWorkPeriods().size());
		
		assertNull(manager.getCrudOperator().getSelectedWorkPeriod());
		
		WorkPeriod period = manager.getWorkPeriods().last();
		
		assertTrue(period.getAttributes().containsKey("testAtt"));
		assertEquals("theVal", period.getAttributes().get("testAtt"));
	}
	
	@Test
	public void addWithEmpty() {
		this.addSimple();
		this.updateOrig();
		
		assertFalse(this.manager.getCrudOperator().doObjAction(this.getActionConfig(Action.ADD)));
		assertEquals(this.orig, this.manager);
	}
	
	// </editor-fold>
	
	// <editor-fold desc="Editing Tests">
	@Test
	public void editFailNoneSelected() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setAttributeName("new Att")
					.setAttributeVal("New val")));
		assertEquals(orig, manager);
	}
	
	@Test
	public void editAtts() {
		int selectedInd = 2;
		this.selectPeriodAt(selectedInd);
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setAttributes("attOne,valOne;attTwo,valTwo;")));
		
		assertNotEquals(orig, manager);
		
		WorkPeriod period = manager.getCrudOperator().getSelectedWorkPeriod();
		
		assertEquals("valOne", period.getAttributes().get("attOne"));
		assertEquals("valTwo", period.getAttributes().get("attTwo"));
	}
	
	@Test
	public void editBadAtts() {
		int selectedInd = 2;
		this.selectPeriodAt(selectedInd);
		
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.EDIT).setAttributes(" ")));
		assertEquals(orig, manager);
	}
	
	@Test
	public void editAttAndAtts() {
		int selectedInd = 2;
		this.selectPeriodAt(selectedInd);
		
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setAttributes("attOne,valOne;attTwo,valTwo;")
					.setAttributeName("att")
					.setAttributeVal("val")));
		assertEquals(orig, manager);
	}
	
	@Test
	public void editAddAtt() {
		int selectedInd = 2;
		this.selectPeriodAt(selectedInd);
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setAttributeName("new Att")
					.setAttributeVal("New val")));
		
		assertNotEquals(orig, manager);
		
		WorkPeriod period = manager.getCrudOperator().getSelectedWorkPeriod();
		
		assertTrue(period.getAttributes().containsKey("new Att"));
		assertEquals("New val", period.getAttributes().get("new Att"));
	}
	
	@Test
	public void editChangeAtt() {
		int selectedInd = 1;
		this.selectPeriodAt(selectedInd);
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setAttributeName("attOne")
					.setAttributeVal("New val")));
		
		assertNotEquals(orig, manager);
		
		WorkPeriod period = manager.getCrudOperator().getSelectedWorkPeriod();
		
		assertTrue(period.getAttributes().containsKey("attOne"));
		assertEquals("New val", period.getAttributes().get("attOne"));
	}
	
	@Test
	public void editRemoveAtt() {
		int selectedInd = 3;
		this.selectPeriodAt(selectedInd);
		
		assertTrue(
			manager.doCrudAction(this.getActionConfig(Action.EDIT).setAttributeName("attOne")));
		
		assertNotEquals(orig, manager);
		
		WorkPeriod period = manager.getCrudOperator().getSelectedWorkPeriod();
		
		assertFalse(period.getAttributes().containsKey("attOne"));
	}
	// </editor-fold>
	
	// <editor-fold desc="Removing Tests">
	@Test
	public void removeNoPeriods() {
		manager = new TimeManager();
		orig = manager.clone();
		
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.REMOVE).setIndex(1)));
		
		assertEquals(orig, manager);
	}
	
	@Test
	public void removeOneBadIndex() {
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.REMOVE).setIndex(0)));
		assertEquals(orig, manager);
	}
	
	@Test
	public void removeNoSpecify() {
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.REMOVE)));
		assertEquals(orig, manager);
	}
	
	@Test
	public void removeAtIndex() {
		int selectedInd = 1;
		
		WorkPeriod period =
			manager.getCrudOperator()
				   .getWorkPeriodDoer()
				   .search(getActionConfig(Action.VIEW))
				   .get(selectedInd - 1);
		
		assertTrue(manager.doCrudAction(this.getActionConfig(Action.REMOVE).setIndex(selectedInd)));
		
		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
	}
	
	@Test
	public void removeSelected() {
		int selectedInd = 1;
		this.selectPeriodAt(selectedInd);
		
		WorkPeriod period =
			manager.getCrudOperator()
				   .getWorkPeriodDoer()
				   .search(getActionConfig(Action.VIEW))
				   .get(selectedInd - 1);
		
		assertTrue(manager.doCrudAction(this.getActionConfig(Action.REMOVE).setIndex(selectedInd)));
		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
		assertNull(manager.getCrudOperator().getSelectedWorkPeriod());
	}
	
	@Test
	public void removeBeforeBadDatetime() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.REMOVE).setBefore("bad dateTime")));
		
		assertEquals(orig, manager);
	}
	
	@Test
	public void removeAfterBadDatetime() {
		assertFalse(
			manager.doCrudAction(this.getActionConfig(Action.REMOVE).setAfter("bad dateTime")));
		
		assertEquals(orig, manager);
	}
	
	@Test
	public void removeBeforeAfterAfter() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.REMOVE)
					.setBefore(TimeParser.toOutputString(nowPlusFive))
					.setAfter(TimeParser.toOutputString(nowPlusFifteen))));
		
		assertEquals(orig, manager);
	}
	
	@Test
	public void removeBefore() {
		int selectedInd = 3;
		
		WorkPeriod period =
			manager.getCrudOperator()
				   .getWorkPeriodDoer()
				   .search(getActionConfig(Action.VIEW))
				   .get(selectedInd - 1);
		
		LocalDateTime dateTime = nowPlusFifteen.plusMinutes(2);
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.REMOVE)
					.setBefore(TimeParser.toOutputString(dateTime))));
		
		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
		assertEquals(2, manager.getWorkPeriods().size());
		// TODO:: assert that the end set contains what it should
	}
	
	@Test
	public void removeAfter() {
		int selectedInd = 1;
		
		WorkPeriod period =
			manager.getCrudOperator()
				   .getWorkPeriodDoer()
				   .search(getActionConfig(Action.VIEW))
				   .get(selectedInd - 1);
		
		LocalDateTime dateTime = nowPlusFifteen.plusMinutes(2);
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.REMOVE)
					.setAfter(TimeParser.toOutputString(dateTime))));
		
		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
		assertEquals(1, manager.getWorkPeriods().size());
		// TODO:: assert that the end set contains what it should
	}
	
	@Test
	public void removeBetween() {
		int selectedInd = 2;
		
		WorkPeriod period =
			manager.getCrudOperator()
				   .getWorkPeriodDoer()
				   .search(getActionConfig(Action.VIEW))
				   .get(selectedInd - 1);
		
		LocalDateTime after = nowPlusFifteen.plusMinutes(2);
		LocalDateTime before = nowPlusHourFifteen.plusMinutes(2);
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.REMOVE)
					.setBefore(TimeParser.toOutputString(before))
					.setAfter(TimeParser.toOutputString(after))));
		
		assertNotEquals(orig, manager);
		assertFalse(manager.getWorkPeriods().contains(period));
		assertEquals(2, manager.getWorkPeriods().size());
		// TODO:: assert that the end set contains what it should
	}
	// </editor-fold>
	
	// <editor-fold desc="View/Search Tests">
	@Test
	public void view() {
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.VIEW)));
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.VIEW).setIndex(1)));
		
		this.resetPrintStreams();
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.VIEW).setIndex(0)));
		this.assertErrOutputContains("No result found at index.");
		
		assertEquals(orig, manager);
	}
	
	@Test
	public void search() {
		ActionConfig config = this.getActionConfig(Action.VIEW);
		
		Collection<WorkPeriod> results =
			manager.getCrudOperator().getWorkPeriodDoer().search(config);
		
		// TODO:: this, but better
	}
	// </editor-fold>
	
	// <editor-fold desc="Selecting">
	@Test
	public void selecting() {
		int selectedInd = 2;
		
		manager.doCrudAction(this.getActionConfig(Action.VIEW).setSelect(true));
		
		assertNull(manager.getCrudOperator().getSelectedWorkPeriod());
		
		manager.doCrudAction(
			this.getActionConfig(Action.VIEW).setSelect(true).setIndex(selectedInd));
		
		WorkPeriod selected = manager.getCrudOperator().getSelectedWorkPeriod();
		
		assertNotNull(selected);
		assertSame(
			manager.getCrudOperator()
				   .getWorkPeriodDoer()
				   .search(this.getActionConfig(Action.VIEW))
				   .get(selectedInd - 1),
			selected
		);
	}
	// </editor-fold>
}
