package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimespanDoerTest extends ActionDoerExtendingTest {
	
	public TimespanDoerTest() {
		super(KeeperObjectType.SPAN);
	}
	
	@BeforeEach
	public void before() {
		this.selectPeriodAt(1);
	}
	//
	//	@After
	//	public void after(){
	//		ActionDoer.resetDoers();
	//	}
	
	// <editor-fold desc="Adding Tests">
	@Test
	public void addNoneSelected() {
		this.manager.getCrudOperator().getWorkPeriodDoer().setSelected(null);
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setName(
						((Task)(getTestManager().getTasks().toArray()[0]))
							.getName()
							.toString())));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void addNoTask() {
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.ADD)));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void addBadTask() {
		TimeManager orig = this.manager.clone();
		assertFalse(
			manager.doCrudAction(this.getActionConfig(Action.ADD).setName("bad task name")));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void addBadStart() {
		this.createNewPeriodAndSelect();
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setName(
						((Task)(getTestManager().getTasks().toArray()[0]))
							.getName()
							.toString())
					.setStart("bad start datetime")));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void addBadEnd() {
		this.createNewPeriodAndSelect();
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setName(
						((Task)(getTestManager().getTasks().toArray()[0]))
							.getName()
							.toString())
					.setEnd("bad end datetime")));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void addStartAfterEnd() {
		this.createNewPeriodAndSelect();
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setName(
						((Task)(getTestManager().getTasks().toArray()[0]))
							.getName()
							.toString())
					.setStart(TimeParser.toOutputString(nowPlusFifteen))
					.setEnd(TimeParser.toOutputString(nowPlusFive))));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void addNoDatetimes() {
		this.createNewPeriodAndSelect();
		Task task = (Task)getTestManager().getTasks().toArray()[0];
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD).setName(task.getName().toString())));
		assertNotEquals(orig, manager);
		assertEquals(1, manager.getCrudOperator().getSelectedWorkPeriod().getNumTimespans());
		
		assertEquals(
			task.getName(),
			manager.getCrudOperator()
				   .getSelectedWorkPeriod()
				   .getTimespans()
				   .first()
				   .getTaskName()
		);
		//		assertSame(task, ActionDoer.getSelectedWorkPeriod().getTimespans().first().getTask());
	}
	
	@Test
	public void addWithDatetimes() {
		this.createNewPeriodAndSelect();
		Task task = (Task)getTestManager().getTasks().toArray()[0];
		LocalDateTime start = TimeParser.parse(TimeParser.toOutputString(nowPlusFive));
		LocalDateTime end = TimeParser.parse(TimeParser.toOutputString(nowPlusTen));
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setName(task.getName().toString())
					.setStart(TimeParser.toOutputString(start))
					.setEnd(TimeParser.toOutputString(end))));
		assertNotEquals(orig, manager);
		assertEquals(1, manager.getCrudOperator().getSelectedWorkPeriod().getNumTimespans());
		
		Timespan span = manager.getCrudOperator().getSelectedWorkPeriod().getTimespans().first();
		
		assertEquals(task.getName(), span.getTaskName());
		//		assertSame(task, span.getTask());
		assertTrue(start.isEqual(span.getStartTime()));
		assertTrue(end.isEqual(span.getEndTime()));
	}
	
	@Test
	public void addWithStartDatetime() {
		this.createNewPeriodAndSelect();
		Task task = (Task)getTestManager().getTasks().toArray()[0];
		LocalDateTime start = TimeParser.parse(TimeParser.toOutputString(nowPlusFive));
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setName(task.getName().toString())
					.setStart(TimeParser.toOutputString(start))));
		assertNotEquals(orig, manager);
		assertEquals(1, manager.getCrudOperator().getSelectedWorkPeriod().getNumTimespans());
		
		Timespan span = manager.getCrudOperator().getSelectedWorkPeriod().getTimespans().first();
		
		assertEquals(task.getName(), span.getTaskName());
		//		assertSame(task, span.getTask());
		assertTrue(start.isEqual(span.getStartTime()));
		assertNull(span.getEndTime());
	}
	
	@Test
	public void addWithEndDatetime() {
		this.createNewPeriodAndSelect();
		Task task = (Task)getTestManager().getTasks().toArray()[0];
		LocalDateTime end = TimeParser.parse(TimeParser.toOutputString(nowPlusTen));
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.ADD)
					.setName(task.getName().toString())
					.setEnd(TimeParser.toOutputString(end))));
		assertNotEquals(orig, manager);
		assertEquals(1, manager.getCrudOperator().getSelectedWorkPeriod().getNumTimespans());
		
		Timespan span = manager.getCrudOperator().getSelectedWorkPeriod().getTimespans().first();
		
		assertEquals(task.getName(), span.getTaskName());
		//		assertSame(task, span.getTask());
		assertNull(span.getStartTime());
		assertTrue(end.isEqual(span.getEndTime()));
	}
	// </editor-fold>
	
	// <editor-fold desc="Edit Tests">
	@Test
	public void editNoInput() {
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.EDIT).setIndex(1)));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void editBadIndex() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(0)
					.setEnd(
						TimeParser.toOutputString(
							nowPlusHourFifteen.plusMinutes(5)))));
		
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void editMalformedStart() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart("bad datetime")
					.setEnd(
						TimeParser.toOutputString(
							nowPlusHourFifteen.plusMinutes(5)))));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void editBadStart() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(
						TimeParser.toOutputString(
							nowPlusHourFifteen.plusMinutes(6)))
					.setEnd(
						TimeParser.toOutputString(
							nowPlusHourFifteen.plusMinutes(5)))));
		assertEquals(orig, this.manager);
		
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(
						TimeParser.toOutputString(
							nowPlusHourFifteen.plusWeeks(50)))));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void editMalformedEnd() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(
						TimeParser.toOutputString(
							nowPlusHourFifteen.plusMinutes(5)))
					.setEnd("bad datetime")));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void editBadEnd() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setEnd(
						TimeParser.toOutputString(
							nowPlusHourFifteen.minusWeeks(50)))));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void ediBadStartEnd() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(
						TimeParser.toOutputString(
							nowPlusHourFifteen.plusMinutes(6)))
					.setEnd(
						TimeParser.toOutputString(
							nowPlusHourFifteen.plusMinutes(5)))));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void ediBadTask() {
		assertFalse(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT).setIndex(1).setName("bad task name")));
		assertEquals(orig, this.manager);
	}
	
	@Test
	public void editTask() {
		Task newTask = this.manager.getTaskByName(TASK_ONE_NAME);
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setName(newTask.getName().toString())));
		assertNotEquals(orig, this.manager);
		
		Timespan span = manager.getCrudOperator().getSelectedWorkPeriod().getTimespans().first();
		
		assertEquals(newTask.getName(), span.getTaskName());
	}
	
	@Test
	public void editStart() {
		Timespan span = manager.getCrudOperator().getSelectedWorkPeriod().getTimespans().first();
		LocalDateTime start =
			TimeParser.parse(TimeParser.toOutputString(span.getStartTime().minusWeeks(1)));
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(TimeParser.toOutputString(start))));
		assertNotEquals(orig, this.manager);
		
		assertEquals(start, span.getStartTime());
	}
	
	@Test
	public void editEnd() {
		Timespan span = manager.getCrudOperator().getSelectedWorkPeriod().getTimespans().first();
		LocalDateTime end =
			TimeParser.parse(TimeParser.toOutputString(span.getEndTime().plusWeeks(1)));
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setEnd(TimeParser.toOutputString(end))));
		assertNotEquals(orig, this.manager);
		
		assertEquals(end, span.getEndTime());
	}
	
	@Test
	public void editStartEnd() {
		Timespan span = manager.getCrudOperator().getSelectedWorkPeriod().getTimespans().first();
		LocalDateTime end =
			TimeParser.parse(TimeParser.toOutputString(span.getEndTime().plusMinutes(1)));
		LocalDateTime start =
			TimeParser.parse(TimeParser.toOutputString(span.getStartTime().minusMinutes(1)));
		
		assertTrue(
			manager.doCrudAction(
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(TimeParser.toOutputString(start))
					.setEnd(TimeParser.toOutputString(end))));
		assertNotEquals(orig, this.manager);
		
		assertEquals(start, span.getStartTime());
		assertEquals(end, span.getEndTime());
	}
	// </editor-fold>
	
	// <editor-fold desc="Remove Tests">
	@Test
	public void removeBadIndex() {
		int prevCount = manager.getCrudOperator().getSelectedWorkPeriod().getNumTimespans();
		assertFalse(manager.doCrudAction(this.getActionConfig(Action.REMOVE).setIndex(0)));
		assertEquals(orig, this.manager);
		
		assertEquals(
			prevCount, manager.getCrudOperator().getSelectedWorkPeriod().getNumTimespans());
	}
	
	@Test
	public void remove() {
		int prevCount = manager.getCrudOperator().getSelectedWorkPeriod().getNumTimespans();
		assertTrue(manager.doCrudAction(this.getActionConfig(Action.REMOVE).setIndex(1)));
		
		assertEquals(
			prevCount - 1, manager.getCrudOperator().getSelectedWorkPeriod().getNumTimespans());
	}
	// </editor-fold>
	
	// <editor-fold desc="View/ Search Tests">
	// TODO:: improve these
	@Test
	public void view() {
		manager.doCrudAction(this.getActionConfig(Action.VIEW));
	}
	// </editor-fold>
}
