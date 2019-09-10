package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
	//<editor-fold desc="newspan tests">
	@Ignore
	@Test
	public void newspan(){
		//TODO
	}
	//</editor-fold>
	//<editor-fold desc="selectnewest tests">
	@Ignore
	@Test
	public void selectnewest(){

	}
	//</editor-fold>
	//<editor-fold desc="completeSpans tests">
	@Ignore
	@Test
	public void completeSpans(){

	}
	//</editor-fold>
	//<editor-fold desc="newPeriod tests">
	@Ignore
	@Test
	public void newPeriod(){

	}
	//</editor-fold>
}
