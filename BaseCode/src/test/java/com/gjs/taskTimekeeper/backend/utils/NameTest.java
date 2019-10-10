package com.gjs.taskTimekeeper.backend.utils;

import com.gjs.taskTimekeeper.backend.TimeManager;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class NameTest {

	//<editor-fold desc="Validator Tests">
	@Test(expected = IllegalArgumentException.class)
	public void validateNameStringTestNull(){
		Name.validateName(null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void validateNameStringTestEmpty(){
		Name.validateName("");
	}
	@Test(expected = IllegalArgumentException.class)
	public void validateNameStringTestWhitespace(){
		Name.validateName("     \n\r");
	}
	@Test
	public void validateNameStringTest(){
		assertEquals("n", Name.validateName("n"));
		assertEquals("n", Name.validateName(" n \t\n\r"));
		assertEquals("n a", Name.validateName(" n a \t\n\r"));
	}
	//</editor-fold>
	//<editor-fold desc="Serialization Tests">
	@Test
	public void serializationTest() throws IOException {
		Name testName = new Name("testName");

		String nameJson = TimeManager.MAPPER.writeValueAsString(testName);

		Name otherTestName = TimeManager.MAPPER.readValue(nameJson, Name.class);
		assertEquals(testName, otherTestName);
	}
	//</editor-fold>
}
