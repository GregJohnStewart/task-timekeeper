package com.gjs.taskTimekeeper.desktopApp.gui.utils;

import org.assertj.swing.exception.WaitTimedOutError;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.timing.Timeout;
import org.junit.Assert;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class GuiAssertions {
	
	public static final long WAIT_TIMEOUT_DURATION = 1000;
	public static final Pattern NOT_FOUND_REGEX = Pattern.compile(
		"^Timed out waiting for (.*) to be found using matcher ([\\s\\S]*)$");
	
	public static void assertNoDialog(FrameFixture fixture) {
		try {
			fixture.dialog(Timeout.timeout(WAIT_TIMEOUT_DURATION));
			Assert.fail();
		} catch(WaitTimedOutError e) {
			assertTrue(
				NOT_FOUND_REGEX.matcher(e.getMessage()).matches()
			);
		}
	}
}
