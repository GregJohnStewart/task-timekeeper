package com.gjs.taskTimekeeper.desktopApp.config;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RunModeTest {
	@Test
	public void test() {
		assertNotNull(RunMode.values());
	}
}
