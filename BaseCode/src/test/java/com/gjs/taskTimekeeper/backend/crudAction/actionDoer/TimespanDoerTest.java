package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

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
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));
		LOGGER.info("Finished setup of timespan doer test.");
	}

	@After
	public void after(){
		ActionDoer.resetDoers();
	}

	//<editor-fold desc="Adding Tests">
	@Test
	public void addNoneSelected() {
		ActionDoer.resetDoers();
		TimeManager orig = this.manager.clone();
		assertFalse(
			ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.ADD).setName(((Task)(getTestManager().getTasks().toArray()[0])).getName()))
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void addNoTask() {
		TimeManager orig = this.manager.clone();
		assertFalse(
			ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.ADD))
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void addBadTask() {
		TimeManager orig = this.manager.clone();
		assertFalse(
			ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.ADD).setName("bad task name"))
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void addBadStart() {
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.ADD).setObjectOperatingOn(KeeperObjectType.PERIOD));
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));
		TimeManager orig = this.manager.clone();
		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.ADD)
					.setName(((Task)(getTestManager().getTasks().toArray()[0])).getName())
					.setStart("bad start datetime")
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void addBadEnd() {
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.ADD).setObjectOperatingOn(KeeperObjectType.PERIOD));
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));
		TimeManager orig = this.manager.clone();
		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.ADD)
					.setName(((Task)(getTestManager().getTasks().toArray()[0])).getName())
					.setEnd("bad end datetime")
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void addStartAfterEnd() {
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.ADD).setObjectOperatingOn(KeeperObjectType.PERIOD));
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));
		TimeManager orig = this.manager.clone();
		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.ADD)
					.setName(((Task)(getTestManager().getTasks().toArray()[0])).getName())
					.setStart(TimeParser.toOutputString(nowPlusFifteen))
					.setEnd(TimeParser.toOutputString(nowPlusFive))
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void addNoDatetimes() {
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.ADD).setObjectOperatingOn(KeeperObjectType.PERIOD));
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));
		TimeManager orig = manager.clone();
		Task task = (Task)getTestManager().getTasks().toArray()[0];
		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.ADD)
					.setName(task.getName())
			)
		);
		assertNotEquals(orig, manager);
		assertEquals(1, ActionDoer.getSelectedWorkPeriod().getNumTimespans());

		assertEquals(task, ActionDoer.getSelectedWorkPeriod().getTimespans().first().getTask());
	}

	@Test
	public void addWithDatetimes() {
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.ADD).setObjectOperatingOn(KeeperObjectType.PERIOD));
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));
		TimeManager orig = manager.clone();
		Task task = (Task)getTestManager().getTasks().toArray()[0];
		LocalDateTime start = TimeParser.parse(TimeParser.toOutputString(nowPlusFive));
		LocalDateTime end = TimeParser.parse(TimeParser.toOutputString(nowPlusTen));

		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.ADD)
					.setName(task.getName())
					.setStart(TimeParser.toOutputString(start))
					.setEnd(TimeParser.toOutputString(end))
			)
		);
		assertNotEquals(orig, manager);
		assertEquals(1, ActionDoer.getSelectedWorkPeriod().getNumTimespans());

		Timespan span =  ActionDoer.getSelectedWorkPeriod().getTimespans().first();

		assertEquals(task, span.getTask());
		assertTrue(start.isEqual(span.getStartTime()));
		assertTrue(end.isEqual(span.getEndTime()));
	}

	@Test
	public void addWithStartDatetime() {
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.ADD).setObjectOperatingOn(KeeperObjectType.PERIOD));
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));
		TimeManager orig = manager.clone();
		Task task = (Task)getTestManager().getTasks().toArray()[0];
		LocalDateTime start = TimeParser.parse(TimeParser.toOutputString(nowPlusFive));

		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.ADD)
					.setName(task.getName())
					.setStart(TimeParser.toOutputString(start))
			)
		);
		assertNotEquals(orig, manager);
		assertEquals(1, ActionDoer.getSelectedWorkPeriod().getNumTimespans());

		Timespan span =  ActionDoer.getSelectedWorkPeriod().getTimespans().first();

		assertEquals(task, span.getTask());
		assertTrue(start.isEqual(span.getStartTime()));
		assertNull(span.getEndTime());
	}

	@Test
	public void addWithEndDatetime() {
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.ADD).setObjectOperatingOn(KeeperObjectType.PERIOD));
		ActionDoer.doObjAction(this.manager, this.getActionConfig(Action.VIEW).setObjectOperatingOn(KeeperObjectType.PERIOD).setSelect(true).setIndex(1));
		TimeManager orig = manager.clone();
		Task task = (Task)getTestManager().getTasks().toArray()[0];
		LocalDateTime end = TimeParser.parse(TimeParser.toOutputString(nowPlusTen));

		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.ADD)
					.setName(task.getName())
					.setEnd(TimeParser.toOutputString(end))
			)
		);
		assertNotEquals(orig, manager);
		assertEquals(1, ActionDoer.getSelectedWorkPeriod().getNumTimespans());

		Timespan span =  ActionDoer.getSelectedWorkPeriod().getTimespans().first();

		assertEquals(task, span.getTask());
		assertNull(span.getStartTime());
		assertTrue(end.isEqual(span.getEndTime()));
	}
	//</editor-fold>

	//<editor-fold desc="Edit Tests">
	@Test
	public void editNoInput(){
		TimeManager orig = this.manager.clone();
		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void editBadIndex(){
		TimeManager orig = this.manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(0)
					.setEnd(TimeParser.toOutputString(nowPlusHourFifteen.plusMinutes(5)))
			)
		);

		assertEquals(orig, this.manager);
	}

	@Test
	public void editMalformedStart(){
		TimeManager orig = this.manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart("bad datetime")
					.setEnd(TimeParser.toOutputString(nowPlusHourFifteen.plusMinutes(5)))
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void editBadStart(){
		TimeManager orig = this.manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(TimeParser.toOutputString(nowPlusHourFifteen.plusMinutes(6)))
					.setEnd(TimeParser.toOutputString(nowPlusHourFifteen.plusMinutes(5)))
			)
		);
		assertEquals(orig, this.manager);

		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(TimeParser.toOutputString(nowPlusHourFifteen.plusWeeks(50)))
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void editMalformedEnd(){
		TimeManager orig = this.manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(TimeParser.toOutputString(nowPlusHourFifteen.plusMinutes(5)))
					.setEnd("bad datetime")
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void editBadEnd(){
		TimeManager orig = this.manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setEnd(TimeParser.toOutputString(nowPlusHourFifteen.minusWeeks(50)))
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void ediBadStartEnd(){
		TimeManager orig = this.manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(TimeParser.toOutputString(nowPlusHourFifteen.plusMinutes(6)))
					.setEnd(TimeParser.toOutputString(nowPlusHourFifteen.plusMinutes(5)))
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void ediBadTask(){
		TimeManager orig = this.manager.clone();

		assertFalse(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setName("bad task name")
			)
		);
		assertEquals(orig, this.manager);
	}

	@Test
	public void editTask(){
		TimeManager orig = this.manager.clone();

		Task newTask = (Task) this.manager.getTaskByName(TASK_ONE_NAME);

		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setName(newTask.getName())
			)
		);
		assertNotEquals(orig, this.manager);

		Timespan span = ActionDoer.getSelectedWorkPeriod().getTimespans().first();

		assertEquals(newTask, span.getTask());
	}

	@Test
	public void editStart(){
		TimeManager orig = this.manager.clone();

		Timespan span = ActionDoer.getSelectedWorkPeriod().getTimespans().first();
		LocalDateTime start = TimeParser.parse(TimeParser.toOutputString(span.getStartTime().minusWeeks(1)));

		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(TimeParser.toOutputString(start))
			)
		);
		assertNotEquals(orig, this.manager);

		assertEquals(start, span.getStartTime());
	}

	@Test
	public void editEnd(){
		TimeManager orig = this.manager.clone();

		Timespan span = ActionDoer.getSelectedWorkPeriod().getTimespans().first();
		LocalDateTime end = TimeParser.parse(TimeParser.toOutputString(span.getEndTime().plusWeeks(1)));

		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setEnd(TimeParser.toOutputString(end))
			)
		);
		assertNotEquals(orig, this.manager);

		assertEquals(end, span.getEndTime());
	}

	@Test
	public void editStartEnd(){
		TimeManager orig = this.manager.clone();

		Timespan span = ActionDoer.getSelectedWorkPeriod().getTimespans().first();
		LocalDateTime end = TimeParser.parse(TimeParser.toOutputString(span.getEndTime().plusMinutes(1)));
		LocalDateTime start = TimeParser.parse(TimeParser.toOutputString(span.getStartTime().minusMinutes(1)));

		assertTrue(
			ActionDoer.doObjAction(
				this.manager,
				this.getActionConfig(Action.EDIT)
					.setIndex(1)
					.setStart(TimeParser.toOutputString(start))
					.setEnd(TimeParser.toOutputString(end))
			)
		);
		assertNotEquals(orig, this.manager);

		assertEquals(start, span.getStartTime());
		assertEquals(end, span.getEndTime());
	}
	//</editor-fold>

	//<editor-fold desc="Remove Tests">
	@Test
	public void removeBadIndex() {
		TimeManager orig = this.manager.clone();
		int prevCount = ActionDoer.getSelectedWorkPeriod().getNumTimespans();
		assertFalse(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.REMOVE)
					.setIndex(0)
			)
		);
		assertEquals(orig, this.manager);

		assertEquals(prevCount, ActionDoer.getSelectedWorkPeriod().getNumTimespans());
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
	//</editor-fold>

	//<editor-fold desc="View/ Search Tests">
	@Test
	public void view() {
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW));
	}
	//</editor-fold>
}